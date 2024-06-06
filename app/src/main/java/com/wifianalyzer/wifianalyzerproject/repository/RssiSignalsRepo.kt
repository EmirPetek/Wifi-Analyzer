package com.wifianalyzer.wifianalyzerproject.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wifianalyzer.wifianalyzerproject.data.RssiSignalData
import com.wifianalyzer.wifianalyzerproject.repository.`interface`.rssiSignalRepoFunctions


class RssiSignalsRepo : rssiSignalRepoFunctions{
    var rssiSignalList : MutableLiveData<List<RssiSignalData>> = MutableLiveData<List<RssiSignalData>>()  //= _rssiSignalList
    //private lateinit var _rssiSignalList : MutableLiveData<List<RssiSignalData>>

    var rssiSignalUnixtsList: MutableLiveData<List<Long>> = MutableLiveData<List<Long>>() // Initialize directly
   // private var _rssiSignalUnixtsList : MutableLiveData<List<Long>>


    val dbRefRssiSignal = FirebaseDatabase.getInstance().getReference("rssiSignals")

     override fun insertRssiSignal(obj: RssiSignalData, userkey: String, unixtimestamp: Long){
        val db = dbRefRssiSignal.child(userkey).child(unixtimestamp.toString())
        db.push().setValue(obj).addOnFailureListener { it ->
            Log.e("hata: ", it.toString())
        }
    }

    override fun getRssiUnixtsListData(userkey: String){
        Log.e("userkey", userkey)
        dbRefRssiSignal
            .child(userkey)
            .addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataList = ArrayList<Long>()
                if (snapshot.exists()){
//                    for (i in snapshot.children) {
//                            var obj = RssiSignalDB(i.key!!.toLong(),i.value.toString())
//                            dataList.add(obj)
//                    }
                    for (i in snapshot.children){
                        dataList.add(i.key!!.toLong())
                    }
                }
                rssiSignalUnixtsList.value = dataList
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun getRssiList(unixtimestamp: Long, userkey: String){
        val dataList: ArrayList<RssiSignalData> = arrayListOf()
        val database = FirebaseDatabase.getInstance()
        val rssiSignalRef = database.getReference("rssiSignals")


        rssiSignalRef.child(userkey).child(unixtimestamp.toString()).get()
                .addOnSuccessListener { snapshot: DataSnapshot ->
                    if (snapshot.exists()) {
                        for (dataSnapshot in snapshot.children) {
                            val rssiSignalData = dataSnapshot.getValue(RssiSignalData::class.java)!!
                            dataList.add(rssiSignalData)
                        }
                        rssiSignalList.value = dataList

                    } else {
                        Log.e("snapshot state: ", "snapshot does not exist")
                    }
                }
     //   }*/

       /* val dataList : ArrayList<RssiSignalData> = arrayListOf()
        val size = unixtimestamp.size
        var i = 0
        while (i < size){
           // Log.e("sizeden azaltılalar -> ", unixtimestamp[i].toString())
            val currentUnixData = unixtimestamp[i].toString()
            dbRefRssiSignal
                .child(userkey)
                .child(currentUnixData)
                .get()
                .addOnSuccessListener {
                    if (it.exists()){
                        val dataMap = it.value as HashMap<*, *>

                        for ((key, value) in dataMap) {
                            val dataObject = value as Map<*, RssiSignalData> // İç içe veriyi Map'e dönüştür
                            // val signal = dataObject.values // bu kısım rssiSignalData classından bir örneği temsil ediyor
                            //  Log.e("i state $i || current unix -> $currentUnixData", "signal: $signal")
                            val signal = dataObject.values
                            val obj = it.getValue(RssiSignalData::class.java)!!
                            dataList.add(obj)
                            Log.e("signal", signal.toString())
                            Log.e("obj", obj.toString())

                        }

                    //    Log.e("snapshot state: ","snapshot var")
                   //     Log.e("datalist : ", dataList.toString())

                       // Log.e("it içi", it.value.toString())

                    }else{
                        Log.e("snapshot state: ", "snapshot does not exist")
                    }
                }
                    i++
        }

        rssiSignalList.value = dataList

*/
        //  for (unixelement in unixtimestamp){
           // Log.e("unixelement -> ", unixelement.toString())

                /*.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.e("children count ", snapshot.childrenCount.toString())
                        Log.e("children  ", snapshot.children.toString())
                        Log.e("snapshot value -> ", snapshot.toString())
                        if (snapshot.exists()){
                            for (i in snapshot.children){
                                val obj = i.getValue(RssiSignalData::class.java)
                                dataList.add(obj!!)
                                Log.e("i: ****", i.toString())
                            }
                        }else{
                            Log.e("ikinci func", "snapshot does not exist")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })*/

      //  }


    }
}