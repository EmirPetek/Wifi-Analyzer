package com.wifianalyzer.wifianalyzerproject
import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.wifianalyzer.wifianalyzerproject.databinding.ActivityMainBinding
import com.wifianalyzer.wifianalyzerproject.ui.AroundWifiInformation
import com.wifianalyzer.wifianalyzerproject.ui.CurrentWifiInformation

class MainActivity : AppCompatActivity() {

    private lateinit var wifiManager: WifiManager
    private lateinit var locationManager: LocationManager
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

   //     checkLocationPermission()

        bindButtons()

        setContentView(binding.root)

    }

    private fun bindButtons(){

        binding.buttonGetCurrentWifiInfo.setOnClickListener { replaceActivity(CurrentWifiInformation()) }
        binding.buttonGetScanResultData.setOnClickListener { replaceActivity(AroundWifiInformation()) }

    }

/*


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
        val wifiScanReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val success = intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) ?: false
                if (success) {
                    scanSuccess()
                } else {
                    scanFailure()
                }
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
        displayScanResults(results)
        getCurrentConnectionInfo()
    }

    private fun scanFailure() {
        val results = wifiManager.scanResults
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        displayScanResults(results)
        getCurrentConnectionInfo()
    }

    private fun displayScanResults(results: List<ScanResult>) {
        val stringBuilder = StringBuilder()
        for (result in results) {
            stringBuilder.append("SSID: ${result.SSID}\n")
            stringBuilder.append("BSSID: ${result.BSSID}\n")
            stringBuilder.append("Capabilities: ${result.capabilities}\n")
            stringBuilder.append("Frequency: ${result.frequency} MHz\n")
            stringBuilder.append("Level: ${result.level} dBm\n")
            stringBuilder.append("\n")
        }
        binding.buttonGetScanResultData.setOnClickListener {
            replaceActivity(AroundWifiInformation())
           // binding.textViewScanResult.text = stringBuilder.toString()
        }

    }

    fun getCurrentConnectionInfo() {
        val wifiInfo: WifiInfo = wifiManager.connectionInfo
        val stringBuilder = StringBuilder()
        stringBuilder.append("SSID: ${wifiInfo.ssid}\n")
        stringBuilder.append("BSSID: ${wifiInfo.bssid}\n")
        stringBuilder.append("Link Speed: ${wifiInfo.linkSpeed} Mbps\n")
        stringBuilder.append("RSSI: ${wifiInfo.rssi} dBm\n")
        stringBuilder.append("IP Address: ${wifiInfo.ipAddress}\n")

        binding.buttonGetCurrentWifiInfo.setOnClickListener {
            //replaceActivity(a())

        //  binding.textViewCurrentInfo.text = stringBuilder.toString()
        }
    }
*/
    private fun replaceActivity(activity: Activity){
        startActivity(Intent(this,activity::class.java))
    }

}
