package com.wifianalyzer.wifianalyzerproject.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class RssiSignalDB(
    val unixtimestamp: Long,
    val nodeKey:String
){

}
