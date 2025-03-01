package com.wifianalyzer.wifianalyzerproject.data

import com.google.firebase.database.IgnoreExtraProperties
import com.wifianalyzer.wifianalyzerproject.data.sensor.SensorAccelerometer
import com.wifianalyzer.wifianalyzerproject.data.sensor.SensorGyroscope
import java.io.File
import java.io.Serializable

@IgnoreExtraProperties
data class RssiSignalData(
    val ssid:String? = null,
    val bssid:String? = null,
    val rssi:Int? = null,
    val location:String? = null,
    val timestamp:Long? = null,
    val userkey:String? = null,
    val sensorAccelerometer: SensorAccelerometer? = null,
    val sensorGyroscope: SensorGyroscope? = null,
    val wifiStandart: String? = null,
    val mcResponder_802_11: String? = null,
    val folderName : String? = null,
    val directory : File? = null,
    val deviceLocation: DeviceLocation? = null,
    val frequency: Int? = null,
    val channelWidth: Int? = null,
    val centerFreq0: Int? = null,
    val centerFreq1: Int? = null,
    val numberOfPeople: Int? = null
    ) : Serializable {


}