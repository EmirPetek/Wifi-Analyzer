package com.wifianalyzer.wifianalyzerproject.data.sensor

import java.io.Serializable

data class IEEE_802(
    val distanceMm: Int
) : Serializable{
    constructor() : this(0)
}
