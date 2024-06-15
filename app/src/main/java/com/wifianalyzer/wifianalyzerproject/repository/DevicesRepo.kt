package com.wifianalyzer.wifianalyzerproject.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wifianalyzer.wifianalyzerproject.data.DevicesData

class DevicesRepo {

    val deviceData : MutableLiveData<DevicesData> = MutableLiveData<DevicesData>()
    var deviceSaveKey : MutableLiveData<String> = MutableLiveData<String>()

    val dbRef = FirebaseDatabase.getInstance().getReference("devices")


    fun insertDevice(device: DevicesData){
        dbRef.child(device.userkey).push().setValue(device)
    }

    fun checkIsDeviceSaved(userkey: String, bssid:String) : String{

        var nodeKey : String = "null"

        dbRef
        .child(userkey)
        .addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (i in snapshot.children){
                      //  Log.e("dbde kayıtlı cihaz in repo class ", i.toString())
                        val data = i.getValue(DevicesData::class.java)!!
                        if (data.bssid == bssid) {
                                nodeKey = i.key!!
                                deviceSaveKey.value = i.key!!
                              //  Log.e("birinci metod", "if içi $nodeKey")

                            }
                        else{
                            deviceSaveKey.value = "null"
                        }
                    }
                }else{
                    deviceSaveKey.value = "null"
                   //Log.e("devicesrepo", "snapshot  yokkk briicni metod")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        return nodeKey

    }

    fun getDeviceData(userkey: String, nodeKey:String){
        dbRef
            .child(userkey)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var nesne = DevicesData()
                    if (snapshot.exists()){
                        for (i in snapshot.children){
                            val data = i.getValue(DevicesData::class.java)!!

                            if (data.deleteState == "0" && data.bssid == nodeKey){
                                nesne = data
                                data.nodeKey = i.key!!
                            }

                        }
                    }else{
                        Log.e("devicesrepo", "snapshot yokk getDeviceData func")
                        Log.e("devicesrepo", nodeKey.toString())
                    }
                    deviceData.value = nesne
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    fun updateDeviceNickname(userkey: String,nodeKey: String,device: Map<String,String>){
        dbRef.child(userkey).child(nodeKey).updateChildren(device)
    }

    fun deleteDevice(userkey: String, nodeKey: String, device: Map<String, Any>){
        dbRef.child(userkey).child(nodeKey).updateChildren(device)
    }
}