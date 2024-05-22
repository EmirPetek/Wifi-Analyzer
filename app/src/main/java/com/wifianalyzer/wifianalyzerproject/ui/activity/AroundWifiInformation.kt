package com.wifianalyzer.wifianalyzerproject.ui.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.wifianalyzer.wifianalyzerproject.data.RssiSignal
import com.wifianalyzer.wifianalyzerproject.ui.adapter.AroundWifiInformationAdapter
import com.wifianalyzer.wifianalyzerproject.databinding.ActivityAroundWifiInformationBinding
import com.wifianalyzer.wifianalyzerproject.viewmodel.AroundWifiInformationViewModel
import java.util.Timer
import java.util.TimerTask

class AroundWifiInformation : AppCompatActivity() {

    private val viewModel : AroundWifiInformationViewModel by viewModels()

    private lateinit var binding: ActivityAroundWifiInformationBinding
    private lateinit var wifiManager: WifiManager
    private lateinit var locationManager: LocationManager
    private lateinit var adapter: AroundWifiInformationAdapter
    val currentTimestamp = System.currentTimeMillis()


    companion object {
        const val PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAroundWifiInformationBinding.inflate(layoutInflater)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        binding.progressBarAroundWifi.visibility = View.GONE

        checkLocationPermission()

        binding.imageViewBackButton.setOnClickListener { finish() }

        setContentView(binding.root)
    }


    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION
            )
        } else {
            manageWifiScan()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    manageWifiScan()
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

    private fun manageWifiScan() {
        val timer = Timer()
        val handler = Handler(Looper.getMainLooper())

        val task = object : TimerTask() {
            override fun run() {
                handler.post {
                    startWifiScan()
                }
            }
        }

        // Timer'ı her 5 saniyede bir çalışacak şekilde ayarla
        timer.schedule(task, 0, 5000)

        //startWifiScan()
    }

    private fun startWifiScan() {

        binding.progressBarAroundWifi.visibility = View.VISIBLE

        val wifiScanReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val success =
                    intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) ?: false
                if (success) {
                    scanSuccess()
                } else {
                    scanFailure()
                }
                binding.progressBarAroundWifi.visibility = View.GONE
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
    }

    private fun scanFailure() {
        val results = wifiManager.scanResults
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        displayScanResults(results)
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
            val obj = RssiSignal(
                result.SSID,
                result.BSSID,
                result.level,
                null,
                System.currentTimeMillis(),
                null
            )
            viewModel.insertRssiSignal(obj,currentTimestamp)
        //insertData(obj,currentTimestamp)


        }
        binding.recyclerViewAroundWifi.setHasFixedSize(true)
        binding.recyclerViewAroundWifi.layoutManager = LinearLayoutManager(this)
        adapter = AroundWifiInformationAdapter(this, results)
        binding.recyclerViewAroundWifi.adapter = adapter

    }

    private fun insertData(obj: RssiSignal, currentTimestamp: Long) {
        Log.e("klfgjl", "klsfld")
        val db = FirebaseDatabase.getInstance().getReference("rssiSignals").child(currentTimestamp.toString())
        db.push().setValue(obj).addOnFailureListener { it ->
            Log.e("hata: ", it.toString())
        }
    }
}