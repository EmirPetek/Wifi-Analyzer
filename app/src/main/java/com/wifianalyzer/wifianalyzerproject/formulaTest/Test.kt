package com.wifianalyzer.wifianalyzerproject.formulaTest

import java.io.File

class Test{
    fun main() {
        // Area dimensions
        val Xo = doubleArrayOf(5.80, 8.50, 14.10, 18.15, 27.55, 32.20, 41.15, 50.05, 45.95, 61.80)
        val Yo = doubleArrayOf(0.35, 12.00, 2.55, 11.70, 3.30, 10.40, 0.35, 0.35, 12.00, 0.75)

        val pointx = doubleArrayOf(50.10, 5.80, 28.80, 42.60, 12.00, 26.10, 32.70)
        val pointy = doubleArrayOf(5.50, 10.80, 6.20, 5.95, 10.10, 8.65, 1.45)

        val AP_number = 10
        val measured_points = 7
        val measured_counts = 1000

        val accessPointNames  =
            arrayListOf(
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



        val Prs = Array(measured_points) { arrayOfNulls<List<Int>>(AP_number) }

        for (i in 1..measured_points){
            for (j in 1..AP_number){
                val filePath = "com.wifianalyzer.wifianalyzerproject.util.RssiData.Results/N$i/files$j.txt"
                // Dosyanın varlığını kontrol et
                val file = File(filePath)
                if (file.exists()) {
                    // Dosyayı oku ve sayıları listeye çevir
                    val valuesRead = file.readLines().mapNotNull { it.toIntOrNull() }
                    Prs[i - 1][j - 1] = valuesRead  // Değerleri kaydet
                }
            }

            for (i in Prs.indices) {
                for (j in Prs[i].indices) {
                    println("Prs[$i][$j]: ${Prs[i][j]}")
                }
            }
        }



        /*
                val Mvalues = Array(measured_points) { Array(AP_number) { IntArray(measured_counts) } }

                // Fill Mvalues with Prs data
                for (j in 0 until measured_points) {
                    for (k in 0 until measured_counts) {
                        for (l in 0 until AP_number) {
                            Mvalues[j][l][k] = if (Prs.getOrNull(j)?.getOrNull(l) != null && k < Prs[j][l].size) Prs[j][l][k] else 0
                        }
                    }
                }*/
    }

}