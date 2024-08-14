package com.wifianalyzer.wifianalyzerproject.repository.sensor

import android.content.Context

class GetSensorData(mContext:Context) {

    private val setSensorData = SetSensorData(mContext)

    init {
        // Sensör dinleyicisini başlat
        setSensorData.startListening()
    }

    fun getAccelerometerData(): FloatArray {
        return setSensorData.accelerometerData
    }

    fun getGyroscopeData(): FloatArray {
        return setSensorData.gyroscopeData
    }

    fun stopListening() {
        setSensorData.stopListening()
    }
}