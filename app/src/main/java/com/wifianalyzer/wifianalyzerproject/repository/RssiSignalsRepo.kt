package com.wifianalyzer.wifianalyzerproject.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.wifianalyzer.wifianalyzerproject.data.RssiSignal

class RssiSignalsRepo {

    val rssiSignalList : MutableLiveData<List<RssiSignal>> = MutableLiveData()
    val dbRefRssiSignal = FirebaseDatabase.getInstance().getReference("rssiSignals")

    fun insertRssiSignal(obj:RssiSignal,timestamp:Long){
        val db = dbRefRssiSignal.child(timestamp.toString())
        db.push().setValue(obj).addOnFailureListener { it ->
            Log.e("hata: ", it.toString())
        }
    }
}