package com.wifianalyzer.wifianalyzerproject
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.wifianalyzer.wifianalyzerproject.databinding.ActivityMainBinding
import com.wifianalyzer.wifianalyzerproject.repository.sensor.rtt.RTTHandler
import com.wifianalyzer.wifianalyzerproject.ui.activityDeprecated.AroundWifiInformation
import java.security.SecureRandom

class MainActivity : AppCompatActivity() {

    private lateinit var wifiManager: WifiManager
    private lateinit var locationManager: LocationManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: Editor
    private lateinit var wifiRTTManagerWrapper: RTTHandler


    companion object {
        private const val PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        checkOrSaveUser()

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

   //     checkLocationPermission()

        //bindButtons()





        setContentView(binding.root)

    }

    /*private fun bindButtons(){

        binding.buttonGetCurrentWifiInfo.setOnClickListener { replaceActivity(CurrentWifiInformation())  }
        binding.buttonGetScanResultData.setOnClickListener { replaceActivity(AroundWifiInformation())    }
        binding.buttonGetData.setOnClickListener{ replaceActivity(AroundWifiResultsDate()) }
    }*/
    private fun replaceActivity(activity: Activity){
        startActivity(Intent(this,activity::class.java))
    }

    private fun checkOrSaveUser(){
        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val registerState: String = sharedPreferences.getString("registerState", "0")!!
        val userKeyData: String = sharedPreferences.getString("userKey", "0")!!

        Log.e("dslkfjlsk",userKeyData)

        if (registerState == "0") {
            registerUser()
        }
    }

    fun registerUser() {
        val sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        val userKey = generateRandomKey()
        editor.putString("userKey", userKey)
        editor.putString("registerState", "1")
        editor.commit()
    }

    fun generateRandomKey(): String {
        val characterSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val secureRandom = SecureRandom()

        // Random key will be created with 64 character
        val randomKeyBuilder = StringBuilder()
        for (i in 0..32) {
            val randomIndex = secureRandom.nextInt(characterSet.length)
            val randomChar = characterSet[randomIndex]
            randomKeyBuilder.append(randomChar)
        }
        return randomKeyBuilder.toString()
    }

    private fun showCustomAlertDialog() {
        // Özel alert dialog layout'unu inflate edin
        val dialogView = layoutInflater.inflate(R.layout.alert_assign_around_wifi_scan_data, null)

        // AlertDialog Builder kullanarak dialog oluşturun
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Dialog üzerindeki butonları ve edittext'leri bulun
        val etDuration = dialogView.findViewById<android.widget.EditText>(R.id.etDuration)
        val etInterval = dialogView.findViewById<android.widget.EditText>(R.id.etInterval)
        val etLocation = dialogView.findViewById<android.widget.EditText>(R.id.etLocation)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnOk = dialogView.findViewById<Button>(R.id.btnOk)

        // Cancel butonuna tıklandığında yapılacak işlemler
        btnCancel.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Main Menuye Dönülüyor", Toast.LENGTH_SHORT).show()
        }

        // Ok butonuna tıklandığında yapılacak işlemler
        btnOk.setOnClickListener {
            val duration = etDuration.text.toString()
            val interval = etInterval.text.toString()
            val location = etLocation.text.toString()

            // Log the data before sending the Intent
            Log.d("MainActivity", "Duration: $duration, Interval: $interval, Location: $location")

            // Intent ile verileri AroundWifiInformation aktivitesine gönder
            val intent = Intent(this, AroundWifiInformation::class.java).apply {
                putExtra("DURATION", duration)
                putExtra("INTERVAL", interval)
                putExtra("LOCATION", location)
            }
            startActivity(intent)

            dialog.dismiss()
        }

        // Dialog'u göster
        dialog.show()
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


}
