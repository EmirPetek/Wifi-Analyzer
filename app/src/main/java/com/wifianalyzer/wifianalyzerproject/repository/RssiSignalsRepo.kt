package com.wifianalyzer.wifianalyzerproject.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.FirebaseDatabase
import com.wifianalyzer.wifianalyzerproject.data.RssiSignalData

class RssiSignalsRepo {

    val rssiSignalList : MutableLiveData<List<RssiSignalData>> = MutableLiveData()
    val dbRefRssiSignal = FirebaseDatabase.getInstance().getReference("rssiSignals")

    fun insertRssiSignal(obj: RssiSignalData, userkey: String, currentTimestamp: Long){
        val db = dbRefRssiSignal.child(userkey).child(currentTimestamp.toString())
        db.push().setValue(obj).addOnFailureListener { it ->
            Log.e("hata: ", it.toString())
        }
    }
}