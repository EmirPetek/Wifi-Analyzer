package com.wifianalyzer.wifianalyzerproject.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wifianalyzer.wifianalyzerproject.data.RssiSignalData
import com.wifianalyzer.wifianalyzerproject.repository.`interface`.rssiSignalRepoFunctions
import java.io.File
import java.io.FileWriter
import java.io.IOException


class RssiSignalsRepo : rssiSignalRepoFunctions{
    var rssiSignalList : MutableLiveData<List<RssiSignalData>> = MutableLiveData<List<RssiSignalData>>()  //= _rssiSignalList
    //private lateinit var _rssiSignalList : MutableLiveData<List<RssiSignalData>>

    var rssiSignalUnixtsList: MutableLiveData<List<Long>> = MutableLiveData<List<Long>>() // Initialize directly
   // private var _rssiSignalUnixtsList : MutableLiveData<List<Long>>


    val dbRefRssiSignal = FirebaseDatabase.getInstance().getReference("rssiSignals")

     fun insertRssiSignal(obj: RssiSignalData){
        /*val db = dbRefRssiSignal.child(userkey).child(unixtimestamp.toString())
        db.push().setValue(obj).addOnFailureListener { it ->
            Log.e("hata: ", it.toString())
        }*/

             val fileName = "${obj.ssid}&${obj.bssid!!.replace(":", "-")}.txt"
             val file = File(obj.directory, fileName)

             Log.e("file: ", file.toString())
             Log.e("fileExist: ", file.exists().toString())

             // Eğer dosya yoksa oluştur
             if (!file.exists()) {
                 val isFileCreated = file.createNewFile()
                 if (!isFileCreated) {
                     throw IOException("Dosya oluşturulamadı!")
                 }
             }


             val fileWriter = FileWriter(file, true)
             val data = "" +
                     "${obj.rssi}," +
                     "${obj.sensorAccelerometer!!.x},${obj.sensorAccelerometer.y},${obj.sensorAccelerometer.z}," +
                     "${obj.sensorGyroscope!!.x},${obj.sensorGyroscope.y},${obj.sensorGyroscope.z}," +
                     "${obj.deviceLocation!!.x},${obj.deviceLocation.y},${obj.deviceLocation.z},${obj.location}," +
                     "${obj.bssid},${obj.wifiStandart},${obj.mcResponder_802_11},${obj.frequency}," +
                     "${obj.channelWidth},${obj.centerFreq0},${obj.centerFreq1}"

             fileWriter.append(data + "\n")
             fileWriter.close()
             Log.e("save state: ", "saved")

    }

    fun listFolders(context: Context): List<String> {
        // 📂 Uygulamanın özel dosya dizinini al
        val directory = context.getExternalFilesDir(null)

        // Eğer dizin erişilebilir değilse veya yoksa boş liste döndür
        if (directory == null || !directory.exists()) {
            return emptyList()
        }
        Log.e("directory: ", directory.toString())

        // 📂 Dosyaları listele (sadece dosya isimlerini al)
        return directory.listFiles()?.map { it.name } ?: emptyList()
    }

    fun listTxtFiles(folderName: String, context: Context): List<String> {
        // 📂 Uygulamanın özel dosya dizinini al
        val parentDir = context.getExternalFilesDir(null)

        // Eğer dizin erişilebilir değilse veya yoksa boş liste döndür
        if (parentDir == null || !parentDir.exists()) {
            return listOf("Ana dizin bulunamadı!")
        }

        // Alt dizinin yolunu oluştur
        val targetDir = File(parentDir, folderName)

        // Eğer belirtilen alt dizin yoksa veya bir dizin değilse boş liste döndür
        if (!targetDir.exists() || !targetDir.isDirectory) {
            return listOf("Belirtilen alt dizin bulunamadı!")
        }

        Log.e("Target Directory", targetDir.absolutePath)

        // Sadece `.txt` uzantılı dosyaları filtreleyerek listele
        return targetDir.listFiles()
            ?.filter { it.isFile && it.extension == "txt" }
            ?.map { it.name }
            ?: listOf("Dizin boş veya .txt dosyası yok!")
    }


    fun readFromFile(context: Context, folderName: String, txtFileName: String): String {
        return try {
            // 📂 Uygulamanın özel dosya dizinini al
            val parentDir = context.getExternalFilesDir(null)

            // Eğer ana dizin yoksa hata mesajı döndür
            if (parentDir == null || !parentDir.exists()) {
                return "Ana dizin bulunamadı!"
            }

            // 📁 Alt dizini belirle
            val directory = File(parentDir, folderName)

            // Eğer belirtilen alt dizin yoksa veya klasör değilse hata mesajı döndür
            if (!directory.exists() || !directory.isDirectory) {
                return "Alt dizin bulunamadı!"
            }

            // 📄 Okunacak dosyayı belirle
            val file = File(directory, txtFileName)

            Log.e("File Path", file.absolutePath) // Log ile dosya yolunu kontrol edebilirsin

            // Eğer dosya varsa içeriğini oku, yoksa hata mesajı döndür
            if (file.exists()) {
                file.readText()
            } else {
                "Dosya bulunamadı!"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            "Dosya okunamadı!"
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
