package com.wifianalyzer.wifianalyzerproject.data

import java.io.Serial
import java.io.Serializable

data class DevicesData(
    val userkey:String,
    val deviceAddTime:Long,
    val deleteState:String? = null,
    val deleteTime:Long? = null,
    val ssid:String,
    val bssid:String,
    val nickname:String? = null,
    var nodeKey:String? = null
) : Serializable{

    constructor() : this("",0,"",0,"","","")

}
