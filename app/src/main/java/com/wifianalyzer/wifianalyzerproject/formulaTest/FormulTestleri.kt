package com.wifianalyzer.wifianalyzerproject.formulaTest

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wifianalyzer.wifianalyzerproject.ui.fragment.formulaTest.adapter.FormulaTestFragmentAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.math3.linear.*
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * FormulTestleri:
 *  - RSSI verilerini okuyup 3D diziye yerleştirir,
 *  - Sadece MS3 (msIndex=2) için Hyperbolic, LS, Proposed (ERLAK) algoritmalarını uygular,
 *  - Elde edilen 1000 hata değerinden CDF çizimi yapar.
 */
class FormulTestleri(
    private val mContext: Context,
    val recyclerViewGraph: RecyclerView
) {

    companion object {
        // AP Koordinatları
        val Xo = doubleArrayOf(5.80,  8.50, 14.10, 18.15, 27.55, 32.20, 41.15, 50.05, 45.95, 61.80)
        val Yo = doubleArrayOf(0.35, 12.00,  2.55, 11.70,  3.30, 10.40,  0.35,  0.35, 12.00,  0.75)

        // Path-loss parametreleri
        const val P0 = -35.92
        const val nreal = 2.0

        // Ölçüm noktaları (7 adet)
        val pointx = doubleArrayOf(50.10, 5.80, 28.80, 42.60, 12.00, 26.10, 32.70)
        val pointy = doubleArrayOf( 5.50,10.80,  6.20,  5.95, 10.10,  8.65,  1.45)

        val centerCdfArraylist = ArrayList<CenterCDFDataClass>()
        private lateinit var adapter: FormulaTestFragmentAdapter
        private var isAnalysisCompleted = false
        private var isAnalysisCompletedCounter = 0

    }

    fun main() {
        val AP_number = 10
        val measured_points = 7
        val measured_counts = 1000

        // TXT dosya isimleri
        val accessPointNames = listOf(
            "TPLINK03-64-70-02-F8-6F-9C",
            "TPLINK04-64-70-02-F8-70-D0",
            "TPLINK06-64-70-02-F8-70-72",
            "TPLINK07-64-70-02-F8-AB-74",
            "TPLINK13-64-70-02-F8-6F-A4",
            "TPLINK15-64-70-02-F8-AB-AE",
            "TPLINK16-64-70-02-F8-5D-B4",
            "TPLINK17-64-70-02-F8-70-94",
            "TPLINKB-64-70-02-F8-6F-92",
            "TPLINKN-64-70-02-F8-AB-BA"
        )

        // 1) Dosyaları okuyup 2D dizi (Prs[i][j]) -> 7x10 boyut
        val Prs = Array(measured_points) { arrayOfNulls<List<Int>>(AP_number) }
        for (i in 0 until measured_points) {
            for (j in 0 until AP_number) {
                val filePath = "1/Results/N${i + 1}/${accessPointNames[j]}.txt"
                try {
                    mContext.assets.open(filePath).use { inputStream ->
                        val valuesRead = inputStream.bufferedReader().readLines()
                            .mapNotNull { it.toIntOrNull() }
                        Prs[i][j] = valuesRead
                    }
                } catch (e: Exception) {
                    Log.e("State", "Dosya okunamadı: i=$i, j=$j, $filePath", e)
                }
            }
        }

        // 2) 3D dizi (7x10x1000)
        val mValues = convertTo3D(measured_points, AP_number, measured_counts, Prs)

/*
        // N tane ölçüm noktalarını yazdır.
        CoroutineScope(Dispatchers.Main).launch {
            for (ms in 0 until measured_points) {
                val result = async(Dispatchers.Main) {
                    loopBeginsForSingleMS(ms, measured_counts, AP_number, mValues)
                }.await() // İşlemin tamamlanmasını bekle
                // Gerekirse burada işlem sonucu ile başka bir işlem yapabilirsiniz
            }
        }*/

        for (ms in 0 until measured_points) {
            loopBeginsForSingleMS(ms, measured_counts, AP_number, mValues)
        }

        //loopBeginsForSingleMS(1,measured_counts,AP_number,mValues)

        // 3) Yalnızca MS3 (fiteration=2) için hesaplama ve CDF plot
       /* loopBeginsForSingleMS(
            msIndex = 6,  // MS3
            measuredCounts = measured_counts,
            apNumber = AP_number,
            mValues = mValues
        )*/
    }

    /**
     * Prs -> 3D dizi: [measuredPoints][apNumber][1000]
     */
    private fun convertTo3D(
        measuredPoints: Int,
        apNumber: Int,
        measuredCounts: Int,
        prs: Array<Array<List<Int>?>>
    ): Array<Array<IntArray>> {
        val mValues = Array(measuredPoints) {
            Array(apNumber) {
                IntArray(measuredCounts)
            }
        }
        for (i in 0 until measuredPoints) {
            for (j in 0 until apNumber) {
                val rssiList = prs[i][j] ?: emptyList()
                val limit = minOf(rssiList.size, measuredCounts)
                for (k in 0 until limit) {
                    mValues[i][j][k] = rssiList[k]
                }
            }
        }
        return mValues
    }

    /**
     * Sadece tek bir ölçüm noktası (msIndex) için,
     * 1000 ölçümde 3 algoritmayı (Hyperbolic, LS, Proposed) çalıştırır.
     * Elde edilen hatalarla CDF grafiği oluşturur.
     */
    private fun loopBeginsForSingleMS(
        msIndex: Int,
        measuredCounts: Int,
        apNumber: Int,
        mValues: Array<Array<IntArray>>
    ) {
        // 3 dizi, her biri 1000 uzunluk: Hyperbolic, LS, Proposed hataları
        val Dhyp = DoubleArray(measuredCounts)
        val Dzero = DoubleArray(measuredCounts)
        val Dlst = DoubleArray(measuredCounts)

        // 1000 ölçüm üzerinden
        for (miteration in 0 until measuredCounts) {
            // 1) RSSI
            val Pr = DoubleArray(apNumber)
            for (apIteration in 0 until apNumber) {
                Pr[apIteration] = mValues[msIndex][apIteration][miteration].toDouble()
            }

            // 2) Mesafeye geçiş
            val destim = DoubleArray(apNumber)
            for (i in 0 until apNumber) {
                destim[i] = 10.0.pow((P0 - Pr[i]) / (10.0 * nreal))
            }

            // -- HYPERBOLIC --
            val xestimhyp = hyperbolicAlgorithm(Xo, Yo, apNumber, destim)
            val derrorhyp = sqrt(
                (pointx[msIndex] - xestimhyp[0]).pow(2) +
                        (pointy[msIndex] - xestimhyp[1]).pow(2)
            )
            Dhyp[miteration] = derrorhyp

            // -- LEAST SQUARE --
            val xzero = calculateXzeroLS(Xo, Yo, apNumber, destim)
            xzero[0] = xzero[0].coerceIn(0.1, 99.9)
            xzero[1] = xzero[1].coerceIn(0.1, 99.9)

            val dzero = sqrt(
                (pointx[msIndex] - xzero[0]).pow(2) +
                        (pointy[msIndex] - xzero[1]).pow(2)
            )
            Dzero[miteration] = dzero

            // -- PROPOSED (ERLAK) --
            val xestimWls = calcNewFormulaXestim(Pr, Xo, Yo)
            val xinitial = xestimWls.copyOf()

            // K parametresi
            val Karr = DoubleArray(apNumber)
            for (i in 0 until apNumber) {
                val dK = sqrt((Xo[i] - xinitial[0]).pow(2) + (Yo[i] - xinitial[1]).pow(2))
                Karr[i] = Pr[i] + 10.0 * nreal * log10(dK)
            }
            val Kestim = Karr.average()

            val destimlst = DoubleArray(apNumber)
            for (i in 0 until apNumber) {
                destimlst[i] = 10.0.pow((Kestim - Pr[i]) / (10.0 * nreal))
            }

            val xestimlst = leastSquareTrackingWithC(Xo, Yo, apNumber, xinitial, destimlst, 10)
            xestimlst[0] = xestimlst[0].coerceIn(0.1, 99.9)
            xestimlst[1] = xestimlst[1].coerceIn(0.1, 99.9)

            val derrorlst = sqrt(
                (pointx[msIndex] - xestimlst[0]).pow(2) +
                        (pointy[msIndex] - xestimlst[1]).pow(2)
            )
            Dlst[miteration] = derrorlst
        }

        // Histogram & CDF
        val histZero = getHistogram(Dzero, measuredCounts)
        val cdfZero  = getCdf(histZero.counts, measuredCounts)
        val histHyp  = getHistogram(Dhyp, measuredCounts)
        val cdfHyp   = getCdf(histHyp.counts, measuredCounts)
        val histLst  = getHistogram(Dlst, measuredCounts)
        val cdfLst   = getCdf(histLst.counts, measuredCounts)



        // Grafiğe çiz
       /* plotCdfChart(
            histZero.centers, cdfZero,
            histHyp.centers,  cdfHyp,
            histLst.centers,  cdfLst
        )*/

        val centerCDF = CenterCDFDataClass(
            histZero.centers, cdfZero,
            histHyp.centers,  cdfHyp,
            histLst.centers,  cdfLst
        )

        // ekranda 7 tane farklı grafik çıkamsı beklenirken 7 tane aynı çıkıyor.

        centerCdfArraylist.add(centerCDF)
        isAnalysisCompleted = true
        isAnalysisCompletedCounter++

        if (centerCdfArraylist.size == pointx.size){
            recyclerViewGraph.setHasFixedSize(true)
            recyclerViewGraph.layoutManager = LinearLayoutManager(mContext)
            adapter = FormulaTestFragmentAdapter(mContext, centerCdfArraylist)
            recyclerViewGraph.adapter = adapter

        }


        Log.e("SingleMS", "Plot completed for MS${msIndex+1}")
    }

    /**
     * MATLAB'teki hyperbolic_algorithm fonksiyonuna denk
     */
    /*fun hyperbolicAlgorithm(
        X: DoubleArray,
        Y: DoubleArray,
        N: Int,
        destim: DoubleArray
    ): DoubleArray {
        // H, b
        val H = Array2DRowRealMatrix(N - 1, 2)
        val b = ArrayRealVector(N - 1)
        for (i in 0 until (N - 1)) {
            H.setEntry(i, 0, 2.0 * X[i + 1])
            H.setEntry(i, 1, 2.0 * Y[i + 1])
            val bi = X[i + 1].pow(2) + Y[i + 1].pow(2)
            - destim[i + 1].pow(2) + destim[0].pow(2)
            b.setEntry(i, bi)
        }

        // S matrisi (MATLAB: Vard(i)=destim(i)^4)
        val Vard = DoubleArray(N) { destim[it].pow(4) }
        val S = Array2DRowRealMatrix(N - 1, N - 1)
        for (i in 0 until (N - 1)) {
            for (j in 0 until (N - 1)) {
                if (i == j) {
                    S.setEntry(i, j, Vard[0] + Vard[i + 1])
                } else {
                    S.setEntry(i, j, Vard[0])
                }
            }
        }

        val Ht = H.transpose()
        val Sinv = LUDecomposition(S).solver.inverse
        val M1 = Ht.multiply(Sinv) // RealMatrix
        val v1 = M1.operate(b)     // RealVector
        val M2 = M1.multiply(H)    // RealMatrix
        val M2inv = LUDecomposition(M2).solver.inverse
        val xEstVec = M2inv.operate(v1)

        val x = xEstVec.getEntry(0).coerceIn(0.1, 99.9)
        val y = xEstVec.getEntry(1).coerceIn(0.1, 99.9)
        return doubleArrayOf(x, y)
    }*/

    fun hyperbolicAlgorithm(
        X: DoubleArray,
        Y: DoubleArray,
        N: Int,
        destim: DoubleArray
    ): DoubleArray {
        val H = Array2DRowRealMatrix(N - 1, 2)
        val b = Array2DRowRealMatrix(N - 1, 1)

        for (i in 0 until N - 1) {
            H.setEntry(i, 0, 2.0 * X[i + 1])
            H.setEntry(i, 1, 2.0 * Y[i + 1])
            val bi = X[i + 1].pow(2) + Y[i + 1].pow(2) - destim[i + 1].pow(2) + destim[0].pow(2)
            b.setEntry(i, 0, bi)
        }

        val Vard = DoubleArray(N) { destim[it].pow(4) }
        val S = Array2DRowRealMatrix(N - 1, N - 1)
        for (i in 0 until N - 1) {
            for (j in 0 until N - 1) {
                S.setEntry(
                    i, j, if (i == j) Vard[0] + Vard[i + 1] else Vard[0]
                )
            }
        }

        val Ht = H.transpose()
        val Sinv = try {
            LUDecomposition(S).solver.inverse
        } catch (e: SingularMatrixException) {
            throw RuntimeException("Matrix S is singular and cannot be inverted", e)
        }

        val bVector = b.getColumnVector(0) // b'yi RealVector'e dönüştür
        val M1 = Ht.multiply(Sinv).multiply(H)
        val M1inv = try {
            LUDecomposition(M1).solver.inverse
        } catch (e: SingularMatrixException) {
            throw RuntimeException("Matrix M1 is singular and cannot be inverted", e)
        }
        val v1 = Ht.multiply(Sinv).operate(bVector) // RealVector ile çalış
        val xEstVec = M1inv.operate(v1)

        val x = xEstVec.getEntry(0).coerceIn(0.1, 99.9)
        val y = xEstVec.getEntry(1).coerceIn(0.1, 99.9)

        return doubleArrayOf(x, y)
    }



    /**
     * MATLAB'teki least_square_algorithm fonksiyonuna denk
     */
    fun calculateXzeroLS(
        X: DoubleArray,
        Y: DoubleArray,
        N: Int,
        destim: DoubleArray
    ): DoubleArray {
        val H = Array2DRowRealMatrix(N - 1, 2)
        val b = Array2DRowRealMatrix(N - 1, 1)

        for (i in 0 until (N - 1)) {
            H.setEntry(i, 0, 2.0 * X[i + 1])
            H.setEntry(i, 1, 2.0 * Y[i + 1])
            val bi = X[i + 1].pow(2) + Y[i + 1].pow(2) -
                    destim[i + 1].pow(2) + destim[0].pow(2)
            b.setEntry(i, 0, bi)
        }

        val Ht = H.transpose()
        val HtH = Ht.multiply(H)
        val invHtH = LUDecomposition(HtH).solver.inverse
        val HtB = Ht.multiply(b)
        val xZeroMat = invHtH.multiply(HtB)

        // Elde edilen (x, y)
        val x = xZeroMat.getEntry(0, 0).coerceIn(0.1, 99.9)
        val y = xZeroMat.getEntry(1, 0).coerceIn(0.1, 99.9)
        return doubleArrayOf(x, y)
    }

    /**
     * MATLAB'teki "calcNewFormulaXestim" (yeni formül, Weighted LS)
     * bir nevi "K" parametresi için
     */
    fun calcNewFormulaXestim(
        Pr: DoubleArray,
        X: DoubleArray,
        Y: DoubleArray
    ): DoubleArray {
        val N = Pr.size
        val H = Array2DRowRealMatrix(N - 1, 3)
        val b = Array2DRowRealMatrix(N - 1, 1)

        for (i in 0 until (N - 1)) {
            val factor = 10.0.pow((Pr[i + 1] - Pr[0]) / (5 * nreal))
            H.setEntry(i, 0, 2.0 * factor * X[i + 1] - 2.0 * X[0])
            H.setEntry(i, 1, 2.0 * factor * Y[i + 1] - 2.0 * Y[0])
            H.setEntry(i, 2, 1 - factor)

            val valB = factor * (X[i + 1].pow(2) + Y[i + 1].pow(2))
            - (X[0].pow(2) + Y[0].pow(2))
            b.setEntry(i, 0, valB)
        }

        val Ht = H.transpose()
        val HtH = Ht.multiply(H)
        val invHtH = LUDecomposition(HtH).solver.inverse
        val HtB = Ht.multiply(b)
        val xMat = invHtH.multiply(HtB)

        val x = xMat.getEntry(0, 0).coerceIn(0.1, 99.9)
        val y = xMat.getEntry(1, 0).coerceIn(0.1, 99.9)
        return doubleArrayOf(x, y)
    }

    /**
     * MATLAB'teki "least_square_tracking_with_C" (ERLAK)
     * En önemli düzeltme: ksi[i] = (destim[i+1] - destim[i]) - (destimx[i+1] - destimx[i])
     */

    fun leastSquareTrackingWithC(
        X: DoubleArray,
        Y: DoubleArray,
        N: Int,
        xestim: DoubleArray,
        destim: DoubleArray,
        triallst: Int
    ): DoubleArray {
        val xEstimation = xestim.copyOf() // xestim güncellenebilir, orijinali koruyoruz

        repeat(triallst) {
            // 1. Mevcut mesafe tahminlerini (destimx) hesapla
            val destimx = DoubleArray(N) { i ->
                sqrt((X[i] - xEstimation[0]).pow(2) + (Y[i] - xEstimation[1]).pow(2))
            }

            // 2. Ksi hesaplama (DoubleArray olarak oluşturuluyor)
            val ksi = DoubleArray(N - 1) { i ->
                (destim[i + 1] - destim[i]) - (destimx[i + 1] - destimx[i])
            }

            // DoubleArray -> Array2DRowRealMatrix (N-1 x 1 boyutlu matris)
            val ksiMatrix = Array2DRowRealMatrix(N - 1, 1)
            for (i in ksi.indices) {
                ksiMatrix.setEntry(i, 0, ksi[i]) // Her değeri matrisin 0. sütununa ekliyoruz
            }


            // 3. cosalfa ve sinalfa hesaplama
            val cosalfa = DoubleArray(N) { i -> (xEstimation[0] - X[i]) / destimx[i] }
            val sinalfa = DoubleArray(N) { i -> (xEstimation[1] - Y[i]) / destimx[i] }

            // 4. Htrack matrisini oluşturma
            val Htrack = Array2DRowRealMatrix(N - 1, 2)
            for (i in 0 until N - 1) {
                Htrack.setEntry(i, 0, cosalfa[i + 1] - cosalfa[i])
                Htrack.setEntry(i, 1, sinalfa[i + 1] - sinalfa[i])
            }

            // 5. C matrisi oluşturma
            val Vard = DoubleArray(N) { destim[it].pow(2) }
            val C = Array2DRowRealMatrix(N - 1, N - 1)
            for (i in 0 until N - 1) {
                for (j in 0 until N - 1) {
                    C.setEntry(
                        i, j,
                        when {
                            i == j -> Vard[i] + Vard[i + 1]
                            i == j - 1 -> -Vard[i + 1]
                            i == j + 1 -> -Vard[j + 1]
                            else -> 0.0
                        }
                    )
                }
            }

            // 6. teta hesaplama
            val Ht = Htrack.transpose()
            val Cinv = try {
                LUDecomposition(C).solver.inverse
            } catch (e: SingularMatrixException) {
                throw RuntimeException("Matrix C is singular and cannot be inverted", e)
            }

            val teta = try {
                val left = Ht.multiply(Cinv).multiply(Htrack)
                val leftInv = LUDecomposition(left).solver.inverse
                val right = Ht.multiply(Cinv).operate(ArrayRealVector(ksi))
                leftInv.operate(right)
            } catch (e: SingularMatrixException) {
                throw RuntimeException("Matrix inversion failed in teta calculation", e)
            }

            // 7. xdelta ve ydelta hesaplama
            val tetaArray = teta.toArray()
            val xdelta = tetaArray[0]
            val ydelta = tetaArray[1]


            // 8. xestim güncelleme
            xEstimation[0] = (xEstimation[0] + xdelta).coerceIn(0.1, 99.9)
            xEstimation[1] = (xEstimation[1] + ydelta).coerceIn(0.1, 99.9)

            // 9. Yeni mesafe tahmini (destim) güncelleme
            for (i in 0 until N) {
                destim[i] = destimx[i] +
                        ((xEstimation[0] - X[i]) / destimx[i]) * xdelta +
                        ((xEstimation[1] - Y[i]) / destimx[i]) * ydelta
            }
        }

        return xEstimation
    }



    /*fun leastSquareTrackingWithC(
        X: DoubleArray,
        Y: DoubleArray,
        N: Int,
        xestim: DoubleArray,
        destim: DoubleArray,
        triallst: Int
    ): DoubleArray {
        repeat(triallst) {
            // 1) destimx
            val destimx = DoubleArray(N)
            for (i in 0 until N) {
                val d = sqrt((X[i] - xestim[0]).pow(2) + (Y[i] - xestim[1]).pow(2))
                destimx[i] = if (d < 1e-4) 1e-4 else d
            }

            // 2) ksi
            // DİKKAT: MATLAB'teki formülle aynı
            val ksi = DoubleArray(N - 1)
            for (i in 0 until (N - 1)) {
                ksi[i] = (destim[i + 1] - destim[i]) - (destimx[i + 1] - destimx[i])
            }

            // 3) cosalfa, sinalfa
            val cosalfa = DoubleArray(N)
            val sinalfa = DoubleArray(N)
            for (i in 0 until N) {
                cosalfa[i] = (xestim[0] - X[i]) / destimx[i]
                sinalfa[i] = (xestim[1] - Y[i]) / destimx[i]
            }

            // 4) Htrack
            val Htrack = Array(N - 1) { DoubleArray(2) }
            for (i in 0 until (N - 1)) {
                Htrack[i][0] = cosalfa[i + 1] - cosalfa[i]
                Htrack[i][1] = sinalfa[i + 1] - sinalfa[i]
            }

            // 5) C matrisi
            val Vard = DoubleArray(N) { destim[it].pow(2) }
            val C = Array(N - 1) { DoubleArray(N - 1) { 0.0 } }
            for (i in 0 until (N - 1)) {
                for (j in 0 until (N - 1)) {
                    when {
                        i == j     -> C[i][j] = Vard[i] + Vard[i + 1]
                        i == j - 1 -> C[i][j] = -Vard[i + 1]
                        i == j + 1 -> C[i][j] = -Vard[j + 1]
                        else       -> C[i][j] = 0.0
                    }
                }
            }

            // 6) teta = inv(Htrack'^inv(C)*Htrack)* [ Htrack'^inv(C)*ksi ]
            val HtrackMatrix = Array2DRowRealMatrix(Htrack)
            val CMatrix = Array2DRowRealMatrix(C)
            val ksiVector = ArrayRealVector(ksi)

            val Ht = HtrackMatrix.transpose()
            val Cinv = try {
                LUDecomposition(CMatrix).solver.inverse
            } catch (ex: SingularMatrixException) {
                Log.e("leastSquareTrackingWithC", "CMatrix singular, skip iteration", ex)
                return xestim
            }

            val leftPart = Ht.multiply(Cinv).multiply(HtrackMatrix)
            val leftInv = try {
                LUDecomposition(leftPart).solver.inverse
            } catch (ex: SingularMatrixException) {
                Log.e("leastSquareTrackingWithC", "leftPart singular, skip iteration", ex)
                return xestim
            }

            val rightPart = Ht.multiply(Cinv).operate(ksiVector)
            val teta = leftInv.operate(rightPart)

            val xdelta = teta.getEntry(0)
            val ydelta = teta.getEntry(1)

            // 7) xestim güncelle
            xestim[0] += xdelta
            xestim[1] += ydelta
            xestim[0] = xestim[0].coerceIn(0.1, 99.9)
            xestim[1] = xestim[1].coerceIn(0.1, 99.9)

            // 8) destim güncelle
            for (i in 0 until N) {
                destim[i] = destimx[i] +
                        ((xestim[0] - X[i]) / destimx[i]) * xdelta +
                        ((xestim[1] - Y[i]) / destimx[i]) * ydelta
            }
        }
        return xestim
    }*/

    data class HistogramResult(val counts: IntArray, val centers: DoubleArray)

    fun getHistogram(data: DoubleArray, measuredCounts: Int): HistogramResult {
        val counts = IntArray(measuredCounts)
        val centers = DoubleArray(measuredCounts)
        val binWidth = 100.0 / measuredCounts
        for (value in data) {
            val idx = (value / binWidth).toInt()
            if (idx in counts.indices) {
                counts[idx]++
            } else if (idx >= measuredCounts) {
                counts[measuredCounts - 1]++
            }
        }
        for (i in counts.indices) {
            centers[i] = i * binWidth + binWidth / 2
        }
        return HistogramResult(counts, centers)
    }

    fun getCdf(nelement: IntArray, measuredCounts: Int): DoubleArray {
        val cdf = DoubleArray(nelement.size)
        var cumulative = 0.0
        for (i in nelement.indices) {
            cumulative += nelement[i]
            cdf[i] = cumulative / measuredCounts.toDouble()
        }
        return cdf
    }

    data class CenterCDFDataClass(val centersZero: DoubleArray,val cdfZero: DoubleArray,
                val centersHyp:  DoubleArray, val cdfHyp:  DoubleArray,
                val centersLst:  DoubleArray, val cdfLst:  DoubleArray){}

    /*fun plotCdfChart(
        centersZero: DoubleArray, cdfZero: DoubleArray,
        centersHyp:  DoubleArray, cdfHyp:  DoubleArray,
        centersLst:  DoubleArray, cdfLst:  DoubleArray
    ) {
        val entriesZero = centersZero.mapIndexed { idx, cx ->
            Entry(cx.toFloat(), cdfZero[idx].toFloat())
        }
        val entriesHyp = centersHyp.mapIndexed { idx, cx ->
            Entry(cx.toFloat(), cdfHyp[idx].toFloat())
        }
        val entriesLst = centersLst.mapIndexed { idx, cx ->
            Entry(cx.toFloat(), cdfLst[idx].toFloat())
        }

        val dataSetZero = LineDataSet(entriesZero, "LS").apply {
            color = Color.BLUE
            lineWidth = 2f
        }
        val dataSetHyp = LineDataSet(entriesHyp, "WLS").apply {
            color = Color.GREEN
            lineWidth = 2f
        }
        val dataSetLst = LineDataSet(entriesLst, "ERLAK").apply {
            color = Color.RED
            lineWidth = 2f
        }

        val lineData = LineData(dataSetZero, dataSetHyp, dataSetLst)
        chart.data = lineData
        chart.description.text = "MS3 - CDF Plot"
        chart.invalidate()
    }*/
}
