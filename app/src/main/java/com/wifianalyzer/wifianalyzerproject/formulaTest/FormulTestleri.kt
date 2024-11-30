package com.wifianalyzer.wifianalyzerproject.formulaTest

import android.content.Context
import android.util.Log
import java.io.File

class FormulTestleri(val mContext:Context) {

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



        // dosyalardan verileri oku ve Prs dizisine aktar
        val Prs = Array(measured_points) { arrayOfNulls<List<Int>>(AP_number) }
        for (i in 1..measured_points) {
            for (j in 1..AP_number) {
                val filePath = "Results/N$i/${accessPointNames.get(j-1)}.txt"
                try {
                    // Assets içinden dosyayı aç
                    mContext.assets.open(filePath).use { inputStream ->
                        val valuesRead = inputStream.bufferedReader().readLines().mapNotNull { it.toIntOrNull() }
                        Prs[i-1][j-1] = valuesRead
                        //Log.e("State", "Dosya okundu: i,j: $i, $j $filePath")
                    }
                } catch (e: Exception) {
                    //Log.e("State", "Dosya okunamadı: i,j: $i, $j $filePath", e)
                }
            }
        }

        processData(measured_points,AP_number,measured_counts,Prs)

        // alınan verilerin loglanması
        /*for (i in Prs.indices){
            for (j in Prs[i].indices) Log.e("PRS: ", "Prs[$i][$j]: ${Prs[i][j]}")
        }*/



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

    fun processData(
        measuredPoints: Int,
        apNumber: Int,
        measuredCounts: Int,
        prsArray: Array<Array<List<Int>?>> // 2D array of nullable lists
    ) {
        Log.e("prsarray", prsArray.toString())
        Log.e("prsarray size", prsArray.size.toString())
       /* for (i in 0..prsArray.size){
            Log.e("prsArray i ", prsArray[i].toString())
            Log.e("prsArray i size", prsArray[i].size.toString())
            for (j in 0..prsArray[i].size){
                Log.e("prsArray j", prsArray[j].toString())
                Log.e("prsArray j size", prsArray[j].size.toString())
                for (k in 0..prsArray[j].size){
                    Log.e("prsArray k", prsArray[k].toString())
                    Log.e("prsArray k size", prsArray[k].size.toString())
                }
            }
        }*/

        // 2. 3D Dizi (Mvalues) oluşturma
        val mValues = Array(measuredPoints) {
            Array(apNumber) {
                IntArray(measuredCounts) { 0 }
            }
        }

        // 3. Mvalues dizisini doldurma
        var sp = 0
        var ep = measuredCounts -1

        //try {
        var counter = 0
        for (j in 1.. measuredPoints) {
                var k = 0
                for (i in sp..ep) {
                    for (l in 0 until apNumber) {
                        Log.e("J I L sayıları: ", "j: $j , i:$i , l:$l")

                        mValues[j][l][k] = prsArray[i][l]!!.first() // İlk değeri al
                        counter++
                    }
                    k++
                }
                sp += measuredCounts
                ep += measuredCounts
            }
        Log.e("counter: ", counter.toString())

            // Sonuçları yazdırma
            for (j in mValues.indices) {
                for (l in mValues[j].indices) {
                    Log.e("processData","Mvalues[$j][$l]: ${mValues[j][l].joinToString()}")
                }
            }
       /* }catch (e: Exception){
            Log.e("Exception",e.toString())
        }*/

    }


}