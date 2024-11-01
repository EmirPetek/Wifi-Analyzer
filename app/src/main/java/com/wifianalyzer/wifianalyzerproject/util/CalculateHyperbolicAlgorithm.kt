package com.wifianalyzer.wifianalyzerproject.util
import org.apache.commons.math3.linear.*
import kotlin.math.pow


class CalculateHyperbolicAlgorithm {

    fun hyperbolicAlgorithm(X: DoubleArray, Y: DoubleArray, N: Int, destim: DoubleArray): DoubleArray {
        val H = Array(N - 1) { DoubleArray(2) }
        val b = DoubleArray(N - 1)

        // H ve b matrislerini dolduruyoruz
        for (i in 0 until N - 1) {
            H[i][0] = 2 * X[i + 1]
            H[i][1] = 2 * Y[i + 1]
            b[i] = X[i + 1].pow(2) + Y[i + 1].pow(2) - destim[i + 1].pow(2) + destim[0].pow(2)
        }

        val Vard = DoubleArray(N) { destim[it].pow(4) }
        val S = Array(N - 1) { DoubleArray(N - 1) }

        // S matrisini dolduruyoruz
        for (i in 0 until N - 1) {
            for (j in 0 until N - 1) {
                S[i][j] = if (i == j) Vard[0] + Vard[i + 1] else Vard[0]
            }
        }

        val HMatrix = MatrixUtils.createRealMatrix(H)
        val SMatrix = MatrixUtils.createRealMatrix(S)
        val bMatrix = MatrixUtils.createColumnRealMatrix(b) // RealVector yerine RealMatrix

        // LU decomposition method to find the inverse of the matrix
        val SMatrixInverse = LUDecomposition(SMatrix).solver.inverse
        val xestimMatrix = HMatrix.transpose().multiply(SMatrixInverse).multiply(HMatrix)
            .let { LUDecomposition(it).solver.inverse }
            .multiply(HMatrix.transpose().multiply(SMatrixInverse).multiply(bMatrix)) // Çarpma işlemi matris ile yapılır

        val xestimArray = xestimMatrix.getColumn(0) // Sonuç matrisin ilk sütunu

        // Koordinat sınırları kontrol ediliyor
        xestimArray[0] = xestimArray[0].coerceIn(0.1, 99.9)
        xestimArray[1] = xestimArray[1].coerceIn(0.1, 99.9)

        return xestimArray
    }

    // Tek bir Double değerini kullanarak tahmini konum hesaplayan fonksiyon
    fun hyperbolicAlgorithmSingle(X: Double, Y: Double, N: Int, destim: Double): DoubleArray {
        // Access point konumları ve uzaklıkları içeren diziler
        val XArray = doubleArrayOf(0.0, X) // X dizisine başlangıç noktası ekleniyor
        val YArray = doubleArrayOf(0.0, Y) // Y dizisine başlangıç noktası ekleniyor
        val destimArray = doubleArrayOf(0.0, destim) // destim dizisine başlangıç noktası ekleniyor

        // Tek bir access point olduğu için N değeri 2
        return hyperbolicAlgorithm(XArray, YArray, N, destimArray)
    }

}