package com.wifianalyzer.wifianalyzerproject.ui.fragment

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wifianalyzer.wifianalyzerproject.R
import com.wifianalyzer.wifianalyzerproject.data.DeviceLocation
import com.wifianalyzer.wifianalyzerproject.data.RssiSignalData
import com.wifianalyzer.wifianalyzerproject.data.sensor.SensorAccelerometer
import com.wifianalyzer.wifianalyzerproject.data.sensor.SensorGyroscope
import com.wifianalyzer.wifianalyzerproject.databinding.FragmentAroundWifiInformationBinding
import com.wifianalyzer.wifianalyzerproject.repository.sensor.GetSensorData
import com.wifianalyzer.wifianalyzerproject.ui.activityDeprecated.AroundWifiInformation.Companion.PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION
import com.wifianalyzer.wifianalyzerproject.ui.adapter.AroundWifiInformationAdapter
import com.wifianalyzer.wifianalyzerproject.viewmodel.AroundWifiInformationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.util.Timer
import java.util.TimerTask

class AroundWifiInformation : Fragment() {

    private val viewModel: AroundWifiInformationViewModel by viewModels()
    private lateinit var binding: FragmentAroundWifiInformationBinding
    private lateinit var wifiManager: WifiManager
    private lateinit var locationManager: LocationManager
    private lateinit var wifiScanReceiver: BroadcastReceiver
    private lateinit var adapter: AroundWifiInformationAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userKey: String

    private var period = 0
    private var totalPeriod = 0
    private var duration = 0
    private var interval = 0
    private var location = ""
    private var isStartedScan = 0 // 0 hiç başlatılmamış / durdurulmuş halde, 1 başlatılmış
    var timer = Timer()
    var unixtimestamp : Long = System.currentTimeMillis()
    var folderName : String = ""
    lateinit var directory: File


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAroundWifiInformationBinding.inflate(inflater,container,false)

        wifiManager = requireContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        sharedPreferences = requireContext().getSharedPreferences("userInfo", MODE_PRIVATE)
        userKey = sharedPreferences.getString("userKey", "0")!!

        binding.progressBarAroundWifi.visibility = View.GONE
        binding.buttonStartScan.setOnClickListener {
            if (isStartedScan == 0) showCustomAlertDialog()

            if (isStartedScan == 1){
                isStartedScan = 0
                timer.cancel()
                timer = Timer()
                binding.buttonStartScan.setText(getString(R.string.start_scan))
                Toast.makeText(requireContext(),getString(R.string.scan_finished), Toast.LENGTH_SHORT).show()
                period = 0
            }
        }
        binding.imageViewBackButton.setOnClickListener { findNavController().popBackStack() }

        wifiScanReceiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.R)
            override fun onReceive(context: Context?, intent: Intent?) {
                val success = intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) ?: false
                if (success) {
                    scanSuccess()
                } else {
                    scanFailure()
                }
                //binding.progressBarAroundWifi.visibility = View.GONE
            }
        }

        /*val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(requireContext(),wifiScanReceiver, intentFilter,0)*/
        //Log.e("kjdfsd",userKey)


        return binding.root
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION)
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
            @RequiresApi(Build.VERSION_CODES.R)
            override fun run() {
                handler.post {
                    period--
                    bindPeriodTextView(period,totalPeriod)
                    unixtimestamp = System.currentTimeMillis()
                    //Log.e("line 140 period: ", period.toString())
                    if (period <= 0 ) {
                        unixtimestamp = System.currentTimeMillis()
                        timer.cancel()
                        Toast.makeText(requireContext(),getString(R.string.scan_finished),Toast.LENGTH_SHORT).show()
                        binding.buttonStartScan.setText(getString(R.string.start_scan))
                        //binding.progressBarAroundWifi.visibility = View.GONE
                    } else {
                        //binding.progressBarAroundWifi.visibility = View.VISIBLE
                        startWifiScan()

                    }
                }
            }
        }

        timer.schedule(task, 0, (interval * 1000).toLong())
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun startWifiScan() {
        binding.progressBarAroundWifi.visibility = View.VISIBLE
        val success = wifiManager.startScan()
        if (!success) {
            scanFailure()
        }else scanSuccess()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun scanSuccess() {
        val results = wifiManager.scanResults
        Log.e("scanState: ", "scanSuccess")
        displayScanResults(results)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun scanFailure() {
        val results = wifiManager.scanResults
        Log.e("scanState: ", "scanFailure")
        displayScanResults(results)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun displayScanResults(results: List<ScanResult>) {
        var rssiObjList = RssiSignalData()
        //val ssid = results.get(results.indexOf("SSID"))
        val getdata = GetSensorData(requireContext())
        // Sensör verilerini almak için biraz beklemelisiniz, çünkü sensör verileri hemen alınmaz.
        // Aksi takdirde ilk değerler boş olabilir.
        Handler(Looper.getMainLooper()).postDelayed({

            CoroutineScope(Dispatchers.IO).launch {
                val accelerometerDeferred = async {
                    val accelerometerData = getdata.getAccelerometerData()
                    SensorAccelerometer(accelerometerData[0], accelerometerData[1], accelerometerData[2])
                }

                val gyroscopeDeferred = async {
                    val gyroscopeData = getdata.getGyroscopeData()
                    SensorGyroscope(gyroscopeData[0], gyroscopeData[1], gyroscopeData[2])
                }

                // Tüm veriler geldikten sonra await ile sonuçları alıyoruz
                val accelerometerObj = accelerometerDeferred.await()
                val gyroscopeObj = gyroscopeDeferred.await()

                // Verilerin log'lanması
                //Log.e("AnotherClass", "Accelerometer - x: ${accelerometerObj.x}, y: ${accelerometerObj.y}, z: ${accelerometerObj.z}")
                //Log.e("AnotherClass", "Gyroscope - x: ${gyroscopeObj.x}, y: ${gyroscopeObj.y}, z: ${gyroscopeObj.z}")

                val deviceLocation = DeviceLocation(0.0f,0.0f,0.0f)
                // Gelen sonuçları veritabanına kaydetme işlemi
                for (it in results) {
                    rssiObjList = RssiSignalData(
                        it.SSID,
                        it.BSSID,
                        it.level,
                        location,
                        System.currentTimeMillis(),
                        userKey,
                        accelerometerObj,
                        gyroscopeObj,
                        determineStandart(it.wifiStandard),
                        it.is80211mcResponder.toString(),
                        folderName,
                        directory,
                        deviceLocation
                    )
                    //Log.e("veritabanı: ",  "dbye kaydedildi")

                    // Veritabanına ekleme işlemi yapılabilir
                    viewModel.insertRssiSignal(rssiObjList, userKey, unixtimestamp)
                }
            }

            binding.progressBarAroundWifi.visibility = View.GONE






            // Sensör dinlemeyi durdurun
            getdata.stopListening()
        }, 1000) // 1 saniye bekleyin (sensör verilerinin toplanması için)


        //Log.e("rssi verileri: ", rssiObjList.toString())

        //Log.e("sayısal veriler: ", "period: $period || interval: $interval || duration: $duration")

        binding.recyclerViewAroundWifi.setHasFixedSize(true)
        binding.recyclerViewAroundWifi.layoutManager = LinearLayoutManager(requireContext())
        adapter = AroundWifiInformationAdapter(requireContext(), results)
        binding.recyclerViewAroundWifi.adapter = adapter

    }

    fun determineStandart(standart: Int): String{
        when(standart){
            4 -> return "11n"
            5 -> return "11ac"
            6 -> return "11ax"
            7 -> return "11ad"
            8 -> return "11be"
        }
        return "11"
    }

    private fun showCustomAlertDialog() {
        val dialogView = layoutInflater.inflate(R.layout.alert_assign_around_wifi_scan_data, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        val etDuration = dialogView.findViewById<android.widget.EditText>(R.id.etDuration)
        val etInterval = dialogView.findViewById<android.widget.EditText>(R.id.etInterval)
        val etLocation = dialogView.findViewById<android.widget.EditText>(R.id.etLocation)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnOk = dialogView.findViewById<Button>(R.id.btnOk)

        btnCancel.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(requireContext(), "Main Menuye Dönülüyor", Toast.LENGTH_SHORT).show()
        }

        btnOk.setOnClickListener {
            duration = etDuration.text.toString().toInt()
            interval = etInterval.text.toString().toInt()
            location = etLocation.text.toString()

            // txt dizininde ölçümün yapıldığı zamanın dosya adı, txtler bu dosya içine kaydediecek.
            folderName = System.currentTimeMillis().toString()
            Log.e("directoryName =" , folderName)
            directory = File(requireContext().getExternalFilesDir(null), folderName)

            // Eğer klasör yoksa oluştur
            if (!directory.exists()) {
                directory.mkdirs()
            }

            period = ((duration * 60) / interval)
            totalPeriod = period
            bindPeriodTextView(0,totalPeriod)
            period++
            checkLocationPermission()
            dialog.dismiss()
            binding.buttonStartScan.setText(getString(R.string.stop_scan))
            isStartedScan = 1
            unixtimestamp = System.currentTimeMillis()
        }

        dialog.show()
    }

    fun bindPeriodTextView(currentPeriod: Int, totalPeriod:Int){
        binding.textViewCurrentPeriodNumeric.text = "$currentPeriod/$totalPeriod"
    }
}