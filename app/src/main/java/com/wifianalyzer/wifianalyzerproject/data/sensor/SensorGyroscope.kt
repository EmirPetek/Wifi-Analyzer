package com.wifianalyzer.wifianalyzerproject.data.sensor

import java.io.Serializable

data class SensorGyroscope(
    val x: Float,
    val y: Float,
    val z: Float
): Serializable{

    constructor() : this(0.0f,0.0f,0.0f)

}
