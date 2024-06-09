package com.wifianalyzer.wifianalyzerproject.data

data class DevicesData(
    val userkey:String,
    val deviceAddTime:Long,
    val deleteState:String? = null,
    val deleteTime:Long? = null,
    val ssid:String,
    val bssid:String,
    val nickname:String? = null
){

    constructor() : this("",0,"",0,"","","")

}
