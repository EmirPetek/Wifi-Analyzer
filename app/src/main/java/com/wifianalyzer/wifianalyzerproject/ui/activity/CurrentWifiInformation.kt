package com.wifianalyzer.wifianalyzerproject.ui.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wifianalyzer.wifianalyzerproject.databinding.ActivityCurrentWifiInformationBinding

class CurrentWifiInformation : AppCompatActivity() {
    private lateinit var binding: ActivityCurrentWifiInformationBinding
    private lateinit var wifiManager: WifiManager
    private lateinit var locationManager: LocationManager
    companion object {
        private const val PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrentWifiInformationBinding.inflate(layoutInflater)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        binding.progressBarCurrentWifi.visibility = View.GONE


        checkLocationPermission()
        binding.imageViewArrowBack.setOnClickListener { finish() }

        setContentView(binding.root)
    }


    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION
            )
        } else {
            startWifiScan()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startWifiScan()
                } else {
                    // Permission denied, handle as appropriate
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun startWifiScan() {

        binding.progressBarCurrentWifi.visibility = View.VISIBLE

        val wifiScanReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val success = intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) ?: false
                if (success) {
                    scanSuccess()
                } else {
                    scanFailure()
                }
                binding.progressBarCurrentWifi.visibility = View.GONE
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanReceiver, intentFilter)

        val success = wifiManager.startScan()
        if (!success) {
            // scan failure handling
            scanFailure()
        }
    }

    private fun scanSuccess() {
        val results = wifiManager.scanResults
        getCurrentConnectionInfo()
    }

    private fun scanFailure() {
        val results = wifiManager.scanResults
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        getCurrentConnectionInfo()
    }

    fun getCurrentConnectionInfo() {
        val wifiInfo: WifiInfo = wifiManager.connectionInfo
        val stringBuilder = StringBuilder()
        stringBuilder.append("SSID: ${wifiInfo.ssid}\n")
        stringBuilder.append("BSSID: ${wifiInfo.bssid}\n")
        stringBuilder.append("Link Speed: ${wifiInfo.linkSpeed} Mbps\n")
        stringBuilder.append("RSSI: ${wifiInfo.rssi} dBm\n")
        stringBuilder.append("IP Address: ${wifiInfo.ipAddress}\n")

        binding.textViewCurrentWifiInformation.text = stringBuilder.toString()

    }


}