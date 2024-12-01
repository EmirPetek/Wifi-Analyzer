package com.wifianalyzer.wifianalyzerproject.formulaTest

import android.content.Context
import android.graphics.Color
import android.util.Log
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.LUDecomposition
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.RealVector
import java.lang.Math.log10
import java.lang.Math.sqrt
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate


class FormulTestleri(val mContext: Context, val lineChart: LineChart) {

    companion object {
        val Xo = doubleArrayOf(5.80, 8.50, 14.10, 18.15, 27.55, 32.20, 41.15, 50.05, 45.95, 61.80)
        val Yo = doubleArrayOf(0.35, 12.00, 2.55, 11.70, 3.30, 10.40, 0.35, 0.35, 12.00, 0.75)
        val P0 = -35.92
        val nreal = 2

        val pointx = doubleArrayOf(50.10, 5.80, 28.80, 42.60, 12.00, 26.10, 32.70)
        val pointy = doubleArrayOf(5.50, 10.80, 6.20, 5.95, 10.10, 8.65, 1.45)

    }

    fun main() {
        // Area dimensions

        val AP_number = 10
        val measured_points = 7
        val measured_counts = 1000

        val accessPointNames = arrayListOf(
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

        // Dosyalardan verileri oku ve Prs dizisine aktar
        val Prs = Array(measured_points) { arrayOfNulls<List<Int>>(AP_number) }
        for (i in 0 until measured_points) { // 0'dan başlatıyoruz| toplam: 7
            for (j in 0 until AP_number) { // 0'dan başlatıyoruz|  toplam: 10
                val filePath =
                    "1/Results/N${i + 1}/${accessPointNames[j]}.txt" // 1-based index correction
                try {
                    // Assets içinden dosyayı aç
                    mContext.assets.open(filePath).use { inputStream ->
                        val valuesRead =
                            inputStream.bufferedReader().readLines().mapNotNull { it.toIntOrNull() }
                        Prs[i][j] = valuesRead
                        //Log.e("State", "Dosya okundu: i=$i, j=$j, $filePath")
                    }
                } catch (e: Exception) {
                    Log.e("State", "Dosya okunamadı: i=$i, j=$j, $filePath", e)
                }
            }
        }

        processData(measured_points, AP_number, measured_counts, Prs)

        // Alınan verilerin loglanması
        for (i in Prs.indices) {
            for (j in Prs[i].indices) {
                //Log.e("PRS: ", "Prs[$i][$j]: ${Prs[i][j]}")
            }
        }
    }

    fun processData(
        measuredPoints: Int,
        apNumber: Int,
        measuredCounts: Int,
        prs: Array<Array<List<Int>?>> // 2D array of nullable lists
    ) {


        // 1. Boş değerleri 0 ile doldurma
//        val prsArray = prs.map { row ->
//            row.map { col ->
//                col ?: listOf(0) // Eğer null ise [0] atanır
//            }
//        }
        //val prsArray = cellToMat(prs)
        val rows = measuredPoints*measuredCounts
        val cols = apNumber
        //val prsArray: Array<Array<List<Int>>> = Array(rows) { Array(cols) { emptyList() } }
        val prsArray = Array(rows) { IntArray(cols) }


        var counter = 0
        var sp1 = 0
        var ep1 = measuredPoints
        var elementIndex = 0
        for (i in prs.indices){
            for (j in prs[i].indices){
                val list = prs[i][j]
                if (list != null) {
                    for (rssi in list){
                        elementIndex = counter % (measuredPoints*measuredCounts)
                        counter++
                        //Log.e("Element:", "$rssi  elementIndex: $elementIndex : i:$i j:$j")
                        prsArray[elementIndex][j] = rssi
                    }
                }
            }
        }

        //prsArrayde gezinmek
        for (i in prsArray.indices) {
            for (j in prsArray[i].indices) {
                //Log.e("prsArray: ","prsArray[$i][$j] = ${prsArray[i][j]}")
            }
        }

//        for (i in prsArray.indices){
//            //Log.e("i: ", i.toString())
//            for (j in prsArray[i].indices){
//                //Log.e("j: ", j.toString())
//                for (k in prs[j]) {
//                    Log.e("sayılar: ", "i:$i j:$j k:$k")
//                }
//            }
//        }

        /*for (i in prsArray.indices) {
            for (j in prsArray[i].indices) {
                //Log.e("PRS: ", "Prs[$i][$j]: ${prsArray[i][j]}")
            }
        }*/



        // 2. 3D Dizi (Mvalues) oluşturma
        val mValues = Array(measuredPoints) {
            Array(apNumber) {
                IntArray(measuredCounts) { 0 } // Her AP için 1000 değer
            }
        }

        // 3. Mvalues dizisini doldurma
        var sp = 0 // Başlangıç (0-based index)
        var ep = measuredCounts // Bitiş (ilk dilim)

// Verilerin kopyalanması işlemi
        for (j in 0 until measuredPoints) {
            var k = 0

            for (i in sp until ep) {
                for (l in 0 until apNumber) {
                        mValues[j][l][k] = prsArray[i][l]
                }
                k++
            }

            sp += measuredCounts // Yeni dilim başlangıcı
            ep += measuredCounts // Yeni dilim bitişi
        }

        //mValuesi ile ilgili şeyleri ekrana yazdır.
       /* Log.e("mp",measuredPoints.toString())
        Log.e("mc",measuredCounts.toString())
        Log.e("ap no",apNumber.toString())
        Log.e("","Measured Points (Outer Array) size: ${mValues.size}")
        for (pointIndex in mValues.indices) {
            Log.e("mValues", "Point $pointIndex:")
            for (apIndex in mValues[pointIndex].indices) {
                Log.e("mValues","  AP $apIndex: ${mValues[pointIndex][apIndex].joinToString(", ", prefix = "[", postfix = "]")}")
            }
        }*/


        loopBeginsHere(measuredPoints, measuredCounts, apNumber, mValues)


    }

    fun cellToMat(Prs: Array<Array<List<Int>?>>): Array<IntArray> {
        val numRows = Prs[0][0]?.size ?: 0 // Her hücredeki liste boyutunu alıyoruz (örneğin 1000)
        val numCols = Prs.size * Prs[0].size // Toplam sütun sayısı (measuredPoints * APNumber)

        val result = Array(numRows) { IntArray(numCols) }

        var colIndex = 0
        for (i in Prs.indices) {
            for (j in Prs[i].indices) {
                // Hücreyi alıp 1000 elemanlı listeyi matrise ekliyoruz
                Prs[i][j]?.let { values ->
                    for (k in values.indices) {
                        result[k][colIndex] = values[k]
                    }
                }
                colIndex++
            }
        }

        return result
    }
    fun processPrs(measuredPoints: Int, apNumber: Int, prs: Array<Array<List<Int>?>>): Array<Array<IntArray>> {
        // 1. Boş değerleri 0 ile doldur
        val prsArray = Array(measuredPoints) {
            Array(apNumber) {
                prs[it][it] ?: listOf(0)  // Eğer null ise [0] atanır
            }
        }

        // 2. 3D Dizi (mValues) oluşturma
        val mValues = Array(measuredPoints) {
            Array(apNumber) {
                IntArray(1000) { 0 } // Her AP için 1000 değer
            }
        }

        // 3. Mvalues dizisini doldurma
        var sp = 0
        var ep = 1000
        for (j in 0 until measuredPoints) {
            var i = sp
            while (i < minOf(ep, prsArray.size)) { // i'nin sınır kontrolü
                for (l in 0 until apNumber) {
                    // her bir AP için veriyi kopyala
                    val values = prsArray[i][l] ?: listOf(0) // boş ise [0] kullan
                    for (k in values.indices) {
                        mValues[j][l][k] = values[k] // Mvalues dizisine veriyi atıyoruz
                    }
                }
                i++
            }
            sp += 1000
            ep += 1000
        }

        return mValues // Döndürdüğümüz değer doğru türde olacak: Array<Array<IntArray>>
    }



    fun loopBeginsHere(
        measured_points: Int,
        measured_counts: Int,
        AP_number: Int,
        Mvalues: Array<Array<IntArray>>
    ) {
        var counter = 0
        var Kpoints = DoubleArray(measured_points)
        val Evalueshyp = Array(measured_points) { DoubleArray(measured_counts) }
        val Evalueszero = Array(measured_points) { DoubleArray(measured_counts) }
        val Evalueslst = Array(measured_points) { DoubleArray(measured_counts) }
        //val destim = DoubleArray(AP_number)

        for (fiteration in 0 until measured_points) {
            var Dhyp = DoubleArray(measured_counts)
            var Dzero = DoubleArray(measured_counts)
            var Dlst = DoubleArray(measured_counts)
            var Ktotal = DoubleArray(measured_counts)

            for (miteration in 0 until measured_counts) {
                var X = Xo
                var Y = Yo
                var N = X.size
                //Log.e("Sayılar: ", "X: ${X.size} Y:${Y.size} N:$N")

                val destim = DoubleArray(N)
                val Pr = DoubleArray(N)
                for (apiteration in 0 until N) {
                    Pr[apiteration] = Mvalues[fiteration][apiteration][miteration].toDouble()
                }

                for (i in 0 until N) {
                    destim[i] = 10.0.pow((P0 - Pr[i]) / (10 * nreal))
                }

                val xestimhyp = Array(2){DoubleArray(1)}

                val derrorhyp = sqrt((pointx[fiteration] - xestimhyp[0][0]).pow(2) + (pointy[fiteration] - xestimhyp[1][0]).pow(2))
                Dhyp[miteration] = derrorhyp

                // Least square algorithm
                val H = Array(AP_number - 1) { DoubleArray(3) }
                val b = Array(AP_number - 1) { DoubleArray(1) }

                for (i in 0 until AP_number - 1) {
                    H[i][0] = 2 * Xo[i + 1]
                    H[i][1] = 2 * Yo[i + 1]
                    b[i][0] = Xo[i + 1].pow(2) + Yo[i + 1].pow(2) - destim[i + 1].pow(2) + destim[0].pow(2)
                }

                var xzero = calculateXzeroXestim(X,Y,N,destim)
                // xzero dizisini kontrol et ve sınırla
                if (xzero[0] < 0) {
                    xzero[0] = 0.1
                }
                if (xzero[0] > 100) {
                    xzero[0] = 99.9
                }
                if (xzero[1] < 0) {
                    xzero[1] = 0.1
                }
                if (xzero[1] > 100) {
                    xzero[1] = 99.9
                }

                val dzero = sqrt((pointx[fiteration] - xzero[0]).pow(2) + (pointy[fiteration] - xzero[1]).pow(2))

                // New formula instead of least square
                for (i in 0 until N - 1) {
                    H[i][0] = 2 * (10.0.pow((Pr[i + 1] - Pr[i]) / (5 * nreal)) * Xo[i + 1]) - 2 * Xo[i]
                    H[i][1] = 2 * (10.0.pow((Pr[i + 1] - Pr[i]) / (5 * nreal)) * Yo[i + 1]) - 2 * Yo[i]
                    H[i][2] = 1 - 10.0.pow((Pr[i + 1] - Pr[i]) / (5 * nreal))
                    b[i][0] = 10.0.pow((Pr[i + 1] - Pr[i]) / (5 * nreal)) * (Xo[i + 1].pow(2) + Yo[i + 1].pow(2)) - (Xo[i].pow(2) + Yo[i].pow(2))
                }

                val xestim = calculateXzeroXestim(X,Y,N,destim)
                if (xestim[0] < 0) {
                    xestim[0] = 0.1
                }
                if (xestim[0] > 100) {
                    xestim[0] = 99.9
                }
                if (xestim[1] < 0) {
                    xestim[1] = 0.1
                }
                if (xestim[1] > 100) {
                    xestim[1] = 99.9
                }

                val dKestim = DoubleArray(AP_number)
                val K = DoubleArray(AP_number)

                for (i in 0 until AP_number) {
                    dKestim[i] = sqrt((Xo[i] - xestim[0]).pow(2) + (Yo[i] - xestim[1]).pow(2))
                    K[i] = Pr[i] + 10 * nreal * log10(dKestim[i])
                }

                val Kestim = K.average()
                Ktotal[miteration] = Kestim

                val destimlst = DoubleArray(AP_number)
                for (i in 0 until AP_number) {
                    destimlst[i] = 10.0.pow((Kestim - Pr[i]) / (10 * nreal))
                }

                // Least square tracking
                val xestimlst = leastSquareTrackingWithC(Xo, Yo, AP_number, xestim, destimlst, 10)
                val derrorlst = Math.sqrt((pointx[fiteration] - xestimlst[0]).pow(2) + (pointy[fiteration] - xestimlst[1]).pow(2))
                Dlst[miteration] = derrorlst

                Dhyp = Dhyp + derrorhyp
                Dzero = Dzero + dzero
                Dlst = Dlst + derrorlst

//                // Store errors
//                Evalueshyp[fiteration][miteration] = derrorhyp
//                Evalueszero[fiteration][miteration] = dzero
//                Evalueslst[fiteration][miteration] = derrorlst




            }

            val Ktt = Ktotal.average().takeIf { it.isNaN().not() } ?: 0.0
            Kpoints = Kpoints + Ktt

            Evalueshyp[fiteration] = Dhyp.copyOf()
            Evalueszero[fiteration] = Dzero.copyOf()
            Evalueslst[fiteration] = Dlst.copyOf()
            // Convert the arrays for plotting (histogram & CDF)
            val histZero = getHistogram(Evalueszero[fiteration], measured_counts)
            val histHyp = getHistogram(Evalueshyp[fiteration], measured_counts)
            val histLst = getHistogram(Evalueslst[fiteration], measured_counts)

            val cdfZero = getCdf(histZero.counts, measured_counts)
            val cdfHyp = getCdf(histHyp.counts, measured_counts)
            val cdfLst = getCdf(histLst.counts, measured_counts)

            // Plot using MPAndroidChart
            plotCdfChart(lineChart, histZero.centers, cdfZero, histHyp.centers, cdfHyp, histLst.centers, cdfLst)

            Log.e("Figure", fiteration.toString())

        }
    }

    fun hyperbolicAlgorithm(X: DoubleArray, Y: DoubleArray, N: Int, destim: DoubleArray): DoubleArray {
        // Matrix H ve vektör b
        val H = MatrixUtils.createRealMatrix(N - 1, 2)
        val b = ArrayRealVector(N - 1)

        for (i in 0 until N - 1) {
            H.setEntry(i, 0, 2 * X[i + 1])
            H.setEntry(i, 1, 2 * Y[i + 1])
            b.setEntry(i, X[i + 1].pow(2) + Y[i + 1].pow(2)
                    - destim[i + 1].pow(2) + destim[0].pow(2))
        }

        val Vard = ArrayRealVector(N)
        val S = MatrixUtils.createRealMatrix(N - 1, N - 1)

        // Vard vektörünü doldurma
        for (i in 0 until N) {
            Vard.setEntry(i, destim[i].pow(4))
        }

        // S matrisini hesaplama
        for (i in 0 until N - 1) {
            for (j in 0 until N - 1) {
                if (i == j) {
                    S.setEntry(i, j, Vard.getEntry(0) + Vard.getEntry(i + 1))
                } else {
                    S.setEntry(i, j, Vard.getEntry(0))
                }
            }
        }

        val HTranspose = H.transpose()
        val SInverse = LUDecomposition(S).solver.inverse
        val temp = HTranspose.multiply(SInverse).multiply(H)
        val xestim = LUDecomposition(temp).solver.solve(HTranspose.multiply(SInverse).operate(b))

        // Adjust xestim values
        val result = DoubleArray(2)
        result[0] = xestim.getEntry(0).coerceIn(0.1, 99.9)
        result[1] = xestim.getEntry(1).coerceIn(0.1, 99.9)

        return result
    }

    fun calculateXzeroXestim(X: DoubleArray, Y: DoubleArray, N: Int, destim: DoubleArray): DoubleArray {
        // H matrisini oluştur
        val H = MatrixUtils.createRealMatrix(N - 1, 2)

        // b vektörünü oluştur
        val b = ArrayRealVector(N - 1)

        // H ve b matrislerini doldur
        for (i in 0 until N - 1) {
            H.setEntry(i, 0, 2 * X[i + 1])
            H.setEntry(i, 1, 2 * Y[i + 1])
            b.setEntry(i, X[i + 1].pow(2) + Y[i + 1].pow(2) - destim[i + 1].pow(2) + destim[0].pow(2))
        }

        // (H.' * H) matrisinin tersini al
        val HTranspose = H.transpose()
        val HTH = HTranspose.multiply(H)
        val HTHInverse = LUDecomposition(HTH).solver.inverse

        // b vektörünü RealMatrix'e dönüştür
        val bMatrix = MatrixUtils.createRealMatrix(N - 1, 1)
        for (i in 0 until N - 1) {
            bMatrix.setEntry(i, 0, b.getEntry(i))
        }

        // xzero hesapla: (inv(H.' * H)) * (H.' * b)
        val temp = HTranspose.multiply(bMatrix)
        val xzeroMatrix = HTHInverse.multiply(temp)

        // Sonucu DoubleArray olarak döndür
        return DoubleArray(xzeroMatrix.rowDimension) { xzeroMatrix.getEntry(it, 0) }
    }


    fun leastSquareTrackingWithC(X: DoubleArray, Y: DoubleArray, N: Int, xestim: DoubleArray, destim: DoubleArray, triallst: Int): DoubleArray {
        repeat(triallst) {
            var x = xestim[0]
            var y = xestim[1]

            val destimx = DoubleArray(N)
            for (i in 0 until N) {
                destimx[i] = sqrt((X[i] - xestim[0]).pow(2) + (Y[i] - xestim[1]).pow(2))
            }

            val ksi = DoubleArray(N - 1)
            for (i in 0 until N - 1) {
                ksi[i] = (destim[i + 1] - destim[i]) - (destimx[i + 1] - destimx[i])
            }

            val cosalfa = DoubleArray(N)
            val sinalfa = DoubleArray(N)
            for (i in 0 until N) {
                cosalfa[i] = (xestim[0] - X[i]) / destimx[i]
                sinalfa[i] = (xestim[1] - Y[i]) / destimx[i]
            }

            val Htrack = Array(N - 1) { DoubleArray(2) }
            for (i in 0 until N - 1) {
                Htrack[i][0] = cosalfa[i + 1] - cosalfa[i]
                Htrack[i][1] = sinalfa[i + 1] - sinalfa[i]
            }

            val Vard = DoubleArray(N)
            for (i in 0 until N) {
                Vard[i] = destim[i].pow(2)
            }

            val C = Array(N - 1) { DoubleArray(N - 1) }
            for (i in 0 until N - 1) {
                for (j in 0 until N - 1) {
                    C[i][j] = when {
                        i == j -> Vard[i] + Vard[i + 1]
                        i == j - 1 -> -Vard[i + 1]
                        i == j + 1 -> -Vard[j + 1]
                        else -> 0.0
                    }
                }
            }

            val HtrackMatrix: RealMatrix = Array2DRowRealMatrix(Htrack)
            val CMatrix: RealMatrix = Array2DRowRealMatrix(C)
            val ksiVector: RealVector = ArrayRealVector(ksi)

            val HtrackTranspose = HtrackMatrix.transpose()
            val CInverse = LUDecomposition(CMatrix).solver.inverse

            // LUDecomposition kullanarak matrisin tersini almak
            val thetaMatrix = HtrackTranspose.multiply(CInverse)
                .multiply(HtrackMatrix)
                .multiply(LUDecomposition(HtrackTranspose.multiply(CInverse).multiply(HtrackMatrix)).solver.inverse)
                .multiply(HtrackTranspose)
                .multiply(CInverse)
                .multiply(MatrixUtils.createColumnRealMatrix(ksi))

            val xdelta = thetaMatrix.getEntry(0, 0)
            val ydelta = thetaMatrix.getEntry(1, 0)

            xestim[0] += xdelta
            xestim[1] += ydelta

            xestim[0] = max(0.1, min(99.9, xestim[0]))
            xestim[1] = max(0.1, min(99.9, xestim[1]))

            for (i in 0 until N) {
                destim[i] = destimx[i] + ((xestim[0] - X[i]) / destimx[i]) * xdelta + ((xestim[1] - Y[i]) / destimx[i]) * ydelta
            }
        }

        return xestim
    }

    fun getHistogram(data: DoubleArray, measuredCounts: Int): HistogramResult {
        val counts = IntArray(measuredCounts) // Store the counts for each bin
        val centers = DoubleArray(measuredCounts) // Store the centers of the bins
        // Your histogram calculation logic here...
        for (i in data.indices) {
            val index = (data[i] / 10).toInt() // Example binning logic
            if (index in counts.indices) {
                counts[index]++
            }
        }
        for (i in counts.indices) {
            centers[i] = (i * 10).toDouble() // Adjust to correct bin center calculation
        }
        return HistogramResult(counts, centers)
    }

    // CDF Calculation
    fun getCdf(nelement: IntArray, measuredCounts: Int): DoubleArray {
        val cdf = DoubleArray(nelement.size)
        cdf[0] = nelement[0].toDouble() / measuredCounts
        for (i in 1 until nelement.size) {
            cdf[i] = cdf[i - 1] + (nelement[i].toDouble() / measuredCounts)
        }
        return cdf
    }

    // Data class to hold histogram result
    data class HistogramResult(val counts: IntArray, val centers: DoubleArray)

    // Function to plot CDF chart using MPAndroidChart
    fun plotCdfChart(
        chart: LineChart,
        centersZero: DoubleArray, cdfZero: DoubleArray,
        centersHyp: DoubleArray, cdfHyp: DoubleArray,
        centersLst: DoubleArray, cdfLst: DoubleArray
    ) {
        val dataSetZero = LineDataSet(cdfZero.mapIndexed { index, value -> Entry(centersZero[index].toFloat(), value.toFloat()) }, "Zero")
        dataSetZero.color = Color.BLUE
        dataSetZero.lineWidth = 2f

        val dataSetHyp = LineDataSet(cdfHyp.mapIndexed { index, value -> Entry(centersHyp[index].toFloat(), value.toFloat()) }, "Hyp")
        dataSetHyp.color = Color.GREEN
        dataSetHyp.lineWidth = 2f

        val dataSetLst = LineDataSet(cdfLst.mapIndexed { index, value -> Entry(centersLst[index].toFloat(), value.toFloat()) }, "Lst")
        dataSetLst.color = Color.RED
        dataSetLst.lineWidth = 2f

        val data = LineData(dataSetZero, dataSetHyp, dataSetLst)
        chart.data = data
        chart.invalidate() // Refresh the chart
    }





}