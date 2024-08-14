package com.wifianalyzer.wifianalyzerproject.repository.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class SetSensorData(private val mContext: Context) {
    private val sensorManager = mContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    var accelerometerData: FloatArray = FloatArray(3)
        private set
    var gyroscopeData: FloatArray = FloatArray(3)
        private set

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    accelerometerData = event.values.clone()
                }
                Sensor.TYPE_GYROSCOPE -> {
                    gyroscopeData = event.values.clone()
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // İsteğe bağlı olarak sensör hassasiyeti değişikliklerini ele alabilirsiniz
        }
    }

    fun startListening() {
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(sensorEventListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stopListening() {
        sensorManager.unregisterListener(sensorEventListener)
    }
}
