package com.wifianalyzer.wifianalyzerproject.ui.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
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
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.wifianalyzer.wifianalyzerproject.R
import com.wifianalyzer.wifianalyzerproject.data.RssiSignalData
import com.wifianalyzer.wifianalyzerproject.ui.adapter.AroundWifiInformationAdapter
import com.wifianalyzer.wifianalyzerproject.databinding.ActivityAroundWifiInformationBinding
import com.wifianalyzer.wifianalyzerproject.viewmodel.AroundWifiInformationViewModel
import java.util.Timer
import java.util.TimerTask

class AroundWifiInformation : AppCompatActivity() {

    private val viewModel: AroundWifiInformationViewModel by viewModels()
    private lateinit var binding: ActivityAroundWifiInformationBinding
    private lateinit var wifiManager: WifiManager
    private lateinit var locationManager: LocationManager
    private lateinit var wifiScanReceiver: BroadcastReceiver
    private lateinit var adapter: AroundWifiInformationAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userKey: String

    private var period = 0
    private var duration = 0
    private var interval = 0
    private var location = ""
    private var isStartedScan = 0 // 0 hiç başlatılmamış / durdurulmuş halde, 1 başlatılmış
    var timer = Timer()
    var unixtimestamp : Long = System.currentTimeMillis()
    private  var rssiSignalList: List<RssiSignalData> = mutableListOf()
    private  var rssiSignalUnixTsList: List<Long> = mutableListOf()

    companion object {
        const val PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAroundWifiInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE)
        userKey = sharedPreferences.getString("userKey", "0")!!

        binding.progressBarAroundWifi.visibility = View.GONE
        binding.buttonStartScan.setOnClickListener {
            if (isStartedScan == 0) showCustomAlertDialog()

            if (isStartedScan == 1){
                isStartedScan = 0
                timer.cancel()
                timer = Timer()
                binding.buttonStartScan.setText(getString(R.string.start_scan))
                Toast.makeText(applicationContext,getString(R.string.scan_finished),Toast.LENGTH_SHORT).show()
                period = 0
            }
        }
        binding.imageViewBackButton.setOnClickListener { finish() }

        wifiScanReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val success = intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) ?: false
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
        Log.e("kjdfsd",userKey)


      /*  viewModel.getRssiUnixtsListData(userKey)
        viewModel.rssiUnixtsList.observe(this, Observer {
            rssiSignalUnixTsList = it
            Log.e("rssiSignalUnixTsList  in scope -> ", rssiSignalUnixTsList.toString())

        })

        viewModel.getRssiListData(rssiSignalUnixTsList,userKey)
        viewModel.rssiList.observe(this,Observer{
            rssiSignalList = it
            Log.e("rssiSignalList in scope -> ", rssiSignalList.toString())
        })

        Log.e("rssiSignalUnixTsList -> ", rssiSignalUnixTsList.toString())
        Log.e("rssiSignalList -> ", rssiSignalList.toString())

*/
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(wifiScanReceiver)
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION)
        } else {
            manageWifiScan()

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            manageWifiScan()
        }
    }

    private fun manageWifiScan() {
        val handler = Handler(Looper.getMainLooper())
        //unixtimestamp = System.currentTimeMillis()

        val task = object : TimerTask() {
            override fun run() {
                handler.post {
                    period--
                    if (period <= 0 ) {
                        //unixtimestamp = System.currentTimeMillis()
                        timer.cancel()
                        Toast.makeText(applicationContext,getString(R.string.scan_finished),Toast.LENGTH_SHORT).show()
                        binding.buttonStartScan.setText(getString(R.string.start_scan))
                    } else {
                        startWifiScan()

                    }
                }
            }
        }

        timer.schedule(task, 0, (interval * 1000).toLong())
    }

    private fun startWifiScan() {
        binding.progressBarAroundWifi.visibility = View.VISIBLE
        val success = wifiManager.startScan()
        if (!success) {
            scanFailure()
        }
    }

    private fun scanSuccess() {
        val results = wifiManager.scanResults
        displayScanResults(results)
    }

    private fun scanFailure() {
        val results = wifiManager.scanResults
        displayScanResults(results)
    }

    private fun displayScanResults(results: List<ScanResult>) {
        var rssiObjList = RssiSignalData()
        //val ssid = results.get(results.indexOf("SSID"))
        for (it in results){
            rssiObjList =
                RssiSignalData(
                    it.SSID,
                    it.BSSID,
                    it.level,
                    location,
                    System.currentTimeMillis(),
                    userKey
                )
            viewModel.insertRssiSignal(rssiObjList,userKey, System.currentTimeMillis())
        }

        Log.e("rssi verileri: ", rssiObjList.toString())

        Log.e("sayısal veriler: ", "period: $period || interval: $interval || duration: $duration")

        binding.recyclerViewAroundWifi.setHasFixedSize(true)
        binding.recyclerViewAroundWifi.layoutManager = LinearLayoutManager(this)
        adapter = AroundWifiInformationAdapter(this, results)
        binding.recyclerViewAroundWifi.adapter = adapter

    }

    private fun showCustomAlertDialog() {
        val dialogView = layoutInflater.inflate(R.layout.alert_assign_around_wifi_scan_data, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val etDuration = dialogView.findViewById<android.widget.EditText>(R.id.etDuration)
        val etInterval = dialogView.findViewById<android.widget.EditText>(R.id.etInterval)
        val etLocation = dialogView.findViewById<android.widget.EditText>(R.id.etLocation)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnOk = dialogView.findViewById<Button>(R.id.btnOk)

        btnCancel.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Main Menuye Dönülüyor", Toast.LENGTH_SHORT).show()
        }

        btnOk.setOnClickListener {
            duration = etDuration.text.toString().toInt()
            interval = etInterval.text.toString().toInt()
            location = etLocation.text.toString()

            period = ((duration * 60) / interval)
            period++
            checkLocationPermission()
            dialog.dismiss()
            binding.buttonStartScan.setText(getString(R.string.stop_scan))
            isStartedScan = 1
            unixtimestamp = System.currentTimeMillis()
        }

        dialog.show()
    }

    private fun insertData(obj: RssiSignalData, currentTimestamp: Long) {
        Log.e("klfgjl", "klsfld")
        val db = FirebaseDatabase.getInstance().getReference("rssiSignals").child(currentTimestamp.toString())
        db.push().setValue(obj).addOnFailureListener { it ->
            Log.e("hata: ", it.toString())
        }
    }
}