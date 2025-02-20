package com.wifianalyzer.wifianalyzerproject.formulaTest

import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.LUDecomposition
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.RealVector
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.SingularMatrixException
import org.apache.commons.math3.linear.SingularValueDecomposition
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Gerçek Zamanlı ERLAK Hesaplaması için temiz kod örneği.
 *
 * Kullanım:
 *  - Her yeni WiFi taramasında (ör. her 1 saniyede bir),
 *    RSSI değerlerini [rssiValues] olarak bu fonksiyona gönderin.
 *  - Fonksiyon size (x, y) konumunu döndürür.
 */
object ERLAKCalculator {

    /**
     * Ana fonksiyon:
     * @param rssiValues   Her AP'den alınan RSSI değerleri [dBm] (ör. DoubleArray(10) => 10 AP).
     * @param apCoordinates Erişim noktalarının (x,y) koordinatları. rssiValues ile aynı boyutta olmalı.
     * @param pathLossExponent (n), default 2.0
     * @param referenceRSSI  (K'yı bulurken ilk yaklaşık) Varsayılan -35.92 (ort. P0)
     * @param iterationCount ERLAK iterasyon sayısı (varsayılan 10)
     *
     * @return Pair(x, y) -> Tahmin edilen konum (metre cinsinden).
     */
    fun estimateRealTimeLocation(
        rssiValues: DoubleArray,
        apCoordinates: Array<Pair<Double, Double>>,
        pathLossExponent: Double = 2.0,
        referenceRSSI: Double = -35.92,
        iterationCount: Int = 10
    ): Pair<Double, Double> {

        // 1) İlk konum kestirimi (Weighted LS yaklaşımı)
        val initialXY = calcWeightedLSInitialLocation(rssiValues, apCoordinates, pathLossExponent)

        // 2) K sabiti tahmini
        val kEstimate = calcK(initialXY, rssiValues, apCoordinates, pathLossExponent)

        // 3) K sabitine göre mesafeleri yeniden hesapla
        val distances = computeDistances(kEstimate, rssiValues, pathLossExponent)

        // 4) ERLAK iterasyonları ile konumu iyileştir
        val finalXY = refineLocationWithERLAK(
            initialXY,
            apCoordinates,
            distances,
            pathLossExponent,
            iterationCount
        )

        // Son konumu döndür
        return finalXY
    }

    /**
     * Aşama 1) Weighted LS ile ilk konum kestirimi
     * (calcNewFormulaXestim'in sadeleştirilmiş hâli).
     */
    private fun calcWeightedLSInitialLocation(
        rssi: DoubleArray,
        apCoords: Array<Pair<Double, Double>>,
        n: Double
    ): Pair<Double, Double> {

        // N = AP sayısı
        val N = rssi.size
        // H matrisi (N-1 x 3), b matrisi (N-1 x 1)
        val H = Array2DRowRealMatrix(N - 1, 3)
        val b = Array2DRowRealMatrix(N - 1, 1)

        // AP[0] referans alınarak, AP[i+1] ile RSSI farkları -> factor
        val (x0, y0) = apCoords[0]

        for (i in 0 until (N - 1)) {
            val prDiff = rssi[i + 1] - rssi[0]
            val factor = 10.0.pow(prDiff / (5 * n))

            val (xi, yi) = apCoords[i + 1]

            // H satırını doldur
            H.setEntry(i, 0, 2.0 * factor * xi - 2.0 * x0)
            H.setEntry(i, 1, 2.0 * factor * yi - 2.0 * y0)
            H.setEntry(i, 2, 1 - factor)

            // b satırını doldur
            val valB = factor * (xi * xi + yi * yi) - (x0 * x0 + y0 * y0)
            b.setEntry(i, 0, valB)
        }

        // (H^T * H)^(-1) * (H^T * b)
        val xMat = solveMatrixEquation(H, b)
        val x = xMat[0].coerceIn(0.1, 9999.9)
        val y = xMat[1].coerceIn(0.1, 9999.9)

        return Pair(x, y)
    }

    /**
     * Aşama 2) K sabiti hesaplama
     *  K_i = PR_i + 10 * n * log10(d_i)
     *  Sonra K = average(K_i)
     */
    private fun calcK(
        xyEst: Pair<Double, Double>,
        rssi: DoubleArray,
        apCoords: Array<Pair<Double, Double>>,
        n: Double
    ): Double {
        val (xEst, yEst) = xyEst
        val N = rssi.size
        val kArr = DoubleArray(N)

        for (i in 0 until N) {
            val (xi, yi) = apCoords[i]
            val dist = sqrt((xi - xEst).pow(2) + (yi - yEst).pow(2))
            // K_i
            kArr[i] = rssi[i] + 10.0 * n * log10(dist.coerceAtLeast(0.0001))
        }
        return kArr.average()
    }

    /**
     * Aşama 3) K sabiti kullanarak AP'lere olan mesafeleri hesapla
     *  d_i = 10^((K - PR_i) / (10*n))
     */
    private fun computeDistances(
        kValue: Double,
        rssi: DoubleArray,
        n: Double
    ): DoubleArray {
        val N = rssi.size
        val dist = DoubleArray(N)
        for (i in 0 until N) {
            dist[i] = 10.0.pow((kValue - rssi[i]) / (10.0 * n))
        }
        return dist
    }

    /**
     * Aşama 4) ERLAK iterasyonları (leastSquareTrackingWithC)
     *  - x ve y'yi belirli sayıda tekrar (iterationCount) iyileştirir.
     */
    private fun refineLocationWithERLAK(
        initialXY: Pair<Double, Double>,
        apCoords: Array<Pair<Double, Double>>,
        dist: DoubleArray,
        n: Double,
        iterationCount: Int
    ): Pair<Double, Double> {

        // xEst ve yEst'i güncelleyeceğimiz değişken
        var (xEst, yEst) = initialXY
        val N = dist.size

        // iterationCount kez yineleme
        repeat(iterationCount) {
            // 1) Mevcut konum tahminiyle mesafeleri (destimx) hesapla
            val destimx = DoubleArray(N) { i ->
                val (xi, yi) = apCoords[i]
                sqrt((xEst - xi).pow(2) + (yEst - yi).pow(2)).coerceAtLeast(0.0001)
            }

            // 2) Ksi (N-1 boyutlu) -> [ (dist[i+1]-dist[i]) - (destimx[i+1]-destimx[i]) ]
            val ksi = DoubleArray(N - 1) { i ->
                (dist[i + 1] - dist[i]) - (destimx[i + 1] - destimx[i])
            }

            // 3) cosalfa, sinalfa hesaplama
            val cosalfa = DoubleArray(N) { i -> (xEst - apCoords[i].first) / destimx[i] }
            val sinalfa = DoubleArray(N) { i -> (yEst - apCoords[i].second) / destimx[i] }

            // 4) Htrack matrisi (N-1 x 2)
            val Htrack = Array2DRowRealMatrix(N - 1, 2)
            for (i in 0 until (N - 1)) {
                Htrack.setEntry(i, 0, cosalfa[i + 1] - cosalfa[i])
                Htrack.setEntry(i, 1, sinalfa[i + 1] - sinalfa[i])
            }

            // 5) C matrisi (N-1 x N-1)
            //    diyagonal = dist[i]^2 + dist[i+1]^2
            //    komşu elemanlar = -dist[i+1]^2
            val C = Array2DRowRealMatrix(N - 1, N - 1)
            for (i in 0 until (N - 1)) {
                for (j in 0 until (N - 1)) {
                    C.setEntry(i, j, when {
                        i == j -> dist[i].pow(2) + dist[i + 1].pow(2)
                        i == j - 1 -> -dist[i + 1].pow(2)
                        i == j + 1 -> -dist[j + 1].pow(2)
                        else -> 0.0
                    })
                }
            }

            // 6) teta = (Htrack^T C^-1 Htrack)^-1 * (Htrack^T C^-1 * ksi)
            val ksiVector = ArrayRealVector(ksi)
            val Ht = Htrack.transpose()
            val Cinv = invertMatrix(C)
            val left = Ht.multiply(Cinv).multiply(Htrack)
            val leftInv = invertMatrix(left)
            val right = Ht.multiply(Cinv).operate(ksiVector)
            val tetaVector = leftInv.operate(right)  // Bu bir RealVector
            val xdelta = tetaVector.getEntry(0)
            val ydelta = tetaVector.getEntry(1)

            // 7) (xEst, yEst) güncelle
            xEst = (xEst + xdelta).coerceIn(0.1, 9999.9)
            yEst = (yEst + ydelta).coerceIn(0.1, 9999.9)

            // 8) dist[i] değerlerini yeni konuma göre güncelle
            for (i in 0 until N) {
                val (xi, yi) = apCoords[i]
                dist[i] = destimx[i] +
                        ((xEst - xi) / destimx[i]) * xdelta +
                        ((yEst - yi) / destimx[i]) * ydelta
            }
        }

        return Pair(xEst, yEst)
    }

    /**
     * Yardımcı fonksiyon: (H^T * H)^(-1) * (H^T * b) benzeri lineer denklemleri çözmek için
     * matris çarpımı ve ters alma işlemleri.
     * Dönüş değeri: vektör (x, y, vb.)
     */
    private fun solveMatrixEquation(H: RealMatrix, b: RealMatrix): DoubleArray {
        val Ht = H.transpose()
        val HtH = Ht.multiply(H)
        val invHtH = invertMatrix(HtH)
        val HtB = Ht.multiply(b)
        val result = invHtH.multiply(HtB)

        // result boyutu (2x1), (3x1) vb. olabilir
        // Dizi olarak döndürüyoruz
        return DoubleArray(result.rowDimension) { row -> result.getEntry(row, 0) }
    }

    /**
     * Yardımcı fonksiyon: Bir matrisi LUDecomposition ile tersine çevirir.
     */
    private fun invertMatrix(matrix: RealMatrix): RealMatrix {
        return try {
            LUDecomposition(matrix).solver.inverse
        } catch (e: SingularMatrixException) {
            // LU yöntemi başarısızsa, SVD ile pseudo-inverse hesapla
            SingularValueDecomposition(matrix).solver.inverse
        }
    }

}