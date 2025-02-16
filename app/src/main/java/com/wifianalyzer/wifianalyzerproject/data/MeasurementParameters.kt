package com.wifianalyzer.wifianalyzerproject.data

data class MeasurementParameters(
        val rssi: String,
        val accelerometerX: String,
        val accelerometerY: String,
        val accelerometerZ: String,
        val gyroscopeX: String,
        val gyroscopeY: String,
        val gyroscopeZ: String,
        val deviceLocationX: String,
        val deviceLocationY: String,
        val deviceLocationZ: String
)
