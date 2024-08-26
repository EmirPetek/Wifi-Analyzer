package com.wifianalyzer.wifianalyzerproject.repository.sensor.rtt

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.rtt.RangingRequest
import android.net.wifi.rtt.RangingResult
import android.net.wifi.rtt.RangingResultCallback
import android.net.wifi.rtt.WifiRttManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import java.util.concurrent.Executor

class RTTHandler(val mContext: Context, val deviceList: ArrayList<ScanResult>) {


    @RequiresApi(Build.VERSION_CODES.P)
    val rttManager : WifiRttManager = mContext.getSystemService(Context.WIFI_RTT_RANGING_SERVICE) as WifiRttManager
    @RequiresApi(Build.VERSION_CODES.P)
    val isAvaliable = mContext.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_RTT)
    @RequiresApi(Build.VERSION_CODES.P)
    fun getDistance(callback: (Double?) -> Unit){
        if (!rttManager.isAvailable){
            Log.e("WiFiRTT", "Cihaz Wi-Fi RTT'yi desteklemiyor")

            callback(null)
            return
        }else{
            Log.e("WiFiRTT", "Cihaz Wi-Fi RTT desteği sağlamaktadır.")

        }
    }


   /* @RequiresApi(Build.VERSION_CODES.P)
    private val wifiRttManager: WifiRttManager? =
        getSystemService(mContext, WifiRttManager::class.java)

    @RequiresApi(Build.VERSION_CODES.P)
    fun getDistance(callback: (Double?) -> Unit) {
        if (wifiRttManager == null) {
            Log.e("WiFiRTT", "Cihaz Wi-Fi RTT'yi desteklemiyor")

            callback(null)
            return
        }

        val wifiManager = mContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val scanResults = wifiManager.scanResults
        val rttCapableAPs = scanResults.filter { it.is80211mcResponder }

        if (rttCapableAPs.isEmpty()) {
            callback(null)
            return
        }
        Log.e("WiFiRTT", "RTT Destekleyen AP Sayısı: ${rttCapableAPs.size}")



        val accessPoints = rttCapableAPs.take(1) // İlk AP'yi al
        val rangingRequest = RangingRequest.Builder()
            .addAccessPoints(accessPoints)
            .build()

        if (ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        wifiRttManager.startRanging(rangingRequest, mContext.mainExecutor, object : RangingResultCallback() {
            override fun onRangingResults(results: List<RangingResult>) {
                if (results.isNotEmpty()) {
                    val distance = results[0].distanceMm / 1000.0 // İlk sonuçtan mesafeyi metre cinsinden al
                    callback(distance)
                } else {
                    callback(null)
                }
            }

            override fun onRangingFailure(code: Int) {
                Log.e("WiFiRTT", "Ranging failed with code: $code")
                callback(null)
            }
        })
    }*/

}