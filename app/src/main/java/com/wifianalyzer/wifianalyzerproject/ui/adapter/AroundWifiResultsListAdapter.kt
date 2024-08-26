package com.wifianalyzer.wifianalyzerproject.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.wifianalyzer.wifianalyzerproject.R
import com.wifianalyzer.wifianalyzerproject.data.DevicesData
import com.wifianalyzer.wifianalyzerproject.data.RssiSignalData
import com.wifianalyzer.wifianalyzerproject.viewmodel.AroundWifiResultsListViewModel

class AroundWifiResultsListAdapter(
    var mContext: Context,
    var result: List<RssiSignalData>,
    var viewModel: AroundWifiResultsListViewModel,
    private val lifecycleOwner: LifecycleOwner

)
    : RecyclerView.Adapter<AroundWifiResultsListAdapter.CardViewObjHolder>()  {


        private lateinit var deviceData : DevicesData

    class CardViewObjHolder(view : View) : RecyclerView.ViewHolder(view){
        var textViewSSID: TextView = view.findViewById(R.id.textViewAroundWifiDBSSID)
        var textViewBSSID: TextView = view.findViewById(R.id.textViewAroundWifiDBBSSID)
        var textViewLocation: TextView = view.findViewById(R.id.textViewAroundWifiDBLocation)
        var textViewLevel: TextView = view.findViewById(R.id.textViewAroundWifiDBLevel)
        var textViewAccelerometer: TextView = view.findViewById(R.id.textViewAroundWifiDBAccelerometer)
        var textViewGyroscope: TextView = view.findViewById(R.id.textViewAroundWifiDBGyroscope)
        var textViewWifiStandart: TextView = view.findViewById(R.id.textViewAroundWifiResultDBWifiStandart)
        var textView802Support: TextView = view.findViewById(R.id.textViewAroundWifiResultsDB802MCSupport)
        var imageButtonEditWifiResult: ImageButton = view.findViewById(R.id.imageButtonEditWifiResult)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AroundWifiResultsListAdapter.CardViewObjHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.card_around_wifi_db_result,parent,false)
        return AroundWifiResultsListAdapter.CardViewObjHolder(view)
    }

    override fun getItemCount(): Int {
        return result.size
    }


    override fun onBindViewHolder(
        holder: AroundWifiResultsListAdapter.CardViewObjHolder,
        position: Int
    ) {

        val newList = result.sortedByDescending { it.rssi }
        val pos = newList[position]

        val ssid = pos.ssid
        val bssid = pos.bssid
        val level = pos.rssi
        val location = pos.location
        val accelerometer = pos.sensorAccelerometer
        val gyroscope = pos.sensorGyroscope
        val wifiStandart = pos.wifiStandart
        val mcResponder_802_11 = pos.mcResponder_802_11

        //Log.e("aroundwifiresultslistadapter: ", ssid!!)

        holder.textViewSSID.text = "SSID: $ssid"
        holder.textViewBSSID.text = "BSSID: $bssid"
        holder.textViewLevel.text = "Level: $level dBm"
        holder.textViewLocation.text = "Location: $location"
        holder.textViewAccelerometer.text = "$accelerometer"
        holder.textViewGyroscope.text = "$gyroscope"
        holder.textViewWifiStandart.text = "Wifi Standart: $wifiStandart"
        holder.textView802Support.text = "80211mcResponder: $mcResponder_802_11"

        val objData = pos

        holder.imageButtonEditWifiResult.setOnClickListener {
            showAlertDialog(objData)
        }



    }


    private fun showAlertDialog(objData: RssiSignalData) {

        val builder = AlertDialog.Builder(mContext)
        val view = LayoutInflater.from(mContext).inflate(R.layout.alert_edit_device, null)
        builder.setView(view)

        val dialog = builder.create()

        val ssidEditText = view.findViewById<EditText>(R.id.editTextEditDeviceSSID)
        val bssidEditText = view.findViewById<EditText>(R.id.editTextEditDeviceBSSID)
        val nicknameEditText = view.findViewById<EditText>(R.id.editTextEditDeviceNickname)
        val btnAdd = view.findViewById<Button>(R.id.buttonAlertEditDeviceAdd)
        val btnCancel = view.findViewById<Button>(R.id.buttonAlertEditDeviceCancel)
        val btnDelete = view.findViewById<Button>(R.id.buttonAlertEditDeviceDelete)
        val clProgressBar = view.findViewById<ConstraintLayout>(R.id.CLprogressBarAlertEditDevice)
        val clAlertEditDevice = view.findViewById<ConstraintLayout>(R.id.constraintLayoutAlertEditDevice)

        //        ssidEditText.setText(rssiData.ssid)
//        bssidEditText.setText(rssiData.bssid)

      //  Log.e("alert içi", "viewmodel öncesi")
     //   Log.e("obj data: ", objData.toString())


       // Log.e("viewmodel bssid", objData.bssid!!)

        clAlertEditDevice.visibility = View.GONE
        clProgressBar.visibility = View.VISIBLE

        viewModel.getDeviceData(objData.userkey!!,objData.bssid!!)
        viewModel.deviceData.observe(lifecycleOwner, Observer {

            deviceData = it
         //   Log.e("d data -->>>" , deviceData.toString())
            if (deviceData.nodeKey == null){  // sorgu sonucunda ilgili device daha önce kaydedilmemişse

                ssidEditText.setText(objData.ssid)
                bssidEditText.setText(objData.bssid)
                nicknameEditText.text.clear()
                btnAdd.setText(mContext.getString(R.string.add))
                btnAdd.setOnClickListener {

                    val textEdittextNickname = nicknameEditText.text.toString()

                 //   Log.e("viewmodel içindekilier", "btnadd rssi data $objData")

                    if (textEdittextNickname.isEmpty()){
                        Toast.makeText(mContext,mContext.getString(R.string.cannot_be_nickname_empty),Toast.LENGTH_SHORT).show()
                    }else {
                        val device = DevicesData(
                            objData.userkey!!,
                            System.currentTimeMillis(),
                            "0",
                            0,
                            objData.ssid!!,
                            objData.bssid!!,
                            textEdittextNickname
                        )

                    //    Log.e("viewmodel içindekilier", "btnAdd")
                        viewModel.insertDevice(device)
                        Toast.makeText(
                            mContext,
                            mContext.getString(R.string.device_added),
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                    }

                }
            }else {
                /// CİHAZ NICKNAME BİLGİSİ KAYITLI OLDUĞU ZAMAN ÇALIŞAN KISIM

              //  Log.e("deviceDataaaa", deviceData.toString())

                btnDelete.visibility = View.VISIBLE
                nicknameEditText.setText(deviceData.nickname)
                ssidEditText.setText(deviceData.ssid)
                bssidEditText.setText(deviceData.bssid)
                //  Log.e("viewmodel içindekilier", "else kısmında vm içi ve it: $it")

                btnAdd.setText(mContext.getString(R.string.update))

                btnAdd.setOnClickListener {
                    if (nicknameEditText.text.toString().isEmpty()) {
                        Toast.makeText(
                            mContext,
                            mContext.getString(R.string.cannot_be_nickname_empty),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val update = mapOf(
                            "nickname" to nicknameEditText.text.toString(),
                        )
                        viewModel.updateDeviceNickname(objData.userkey!!, deviceData.nodeKey!!, update)
                        Toast.makeText(
                            mContext,
                            mContext.getString(R.string.device_updated),
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                    }
                }


                btnDelete.setOnClickListener {

                    Snackbar.make(
                        btnDelete,
                        mContext.getString(R.string.are_you_sure_to_delete),
                        Snackbar.LENGTH_SHORT
                    )
                        .setAction(mContext.getString(R.string.delete)) {
                            val delete = mapOf(
                                "deleteState" to "1",
                                "deleteTime" to System.currentTimeMillis()
                            )
                            viewModel.deleteDevice(objData.userkey!!, deviceData.nodeKey!!, delete)
                            Toast.makeText(
                                mContext,
                                mContext.getString(R.string.device_deleted),
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                        }.show()

                }


            }



        })


        /*viewModel.getIsDeviceSavedState(objData.userkey!!, objData.bssid!!)
        viewModel.deviceFoundData.observe(lifecycleOwner, Observer {


            nodeKey = it
            Log.e("viewmodel içindekilier", "nodekey -> $nodeKey ")

            if (nodeKey == "null"){ /// CİHAZ NICKNAME BİLGİSİ KAYDEDİLMEMİŞ İSE ÇALIŞAN KISIM
                Log.e("viewmodel içindekilier", "nodekey null")

                ssidEditText.setText(objData.ssid)
                bssidEditText.setText(objData.bssid)

               // btnAdd.setText(mContext.getString(R.string.add))
                btnAdd.setOnClickListener {

                    val textEdittextNickname = nicknameEditText.text.toString()

                    Log.e("viewmodel içindekilier", "btnadd rssi data $objData")

                    if (textEdittextNickname.isEmpty()){
                        Toast.makeText(mContext,mContext.getString(R.string.cannot_be_nickname_empty),Toast.LENGTH_SHORT).show()
                    }else {
                        val device = DevicesData(
                            objData.userkey!!,
                            System.currentTimeMillis(),
                            "0",
                            0,
                            objData.ssid!!,
                            objData.bssid!!,
                            textEdittextNickname
                        )

                        Log.e("viewmodel içindekilier", "btnAdd")
                        viewModel.insertDevice(device)
                        Toast.makeText(
                            mContext,
                            mContext.getString(R.string.device_added),
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                    }
                }

            }else{
                /// CİHAZ NICKNAME BİLGİSİ KAYITLI OLDUĞU ZAMAN ÇALIŞAN KISIM
                btnDelete.visibility = View.VISIBLE
                viewModel.getDeviceData(objData.userkey!!,nodeKey)
                viewModel.deviceData.observe(lifecycleOwner, Observer {
                    nicknameEditText.setText(it.nickname)
                    ssidEditText.setText(it.ssid)
                    bssidEditText.setText(it.bssid)
                    //  Log.e("viewmodel içindekilier", "else kısmında vm içi ve it: $it")

                    btnAdd.setText(mContext.getString(R.string.update))

                        btnAdd.setOnClickListener {
                            if (nicknameEditText.text.toString().isEmpty()){
                                Toast.makeText(mContext,mContext.getString(R.string.cannot_be_nickname_empty),Toast.LENGTH_SHORT).show()
                            }else {
                                val update = mapOf(
                                    "nickname" to nicknameEditText.text.toString(),
                                )
                                viewModel.updateDeviceNickname(objData.userkey!!, nodeKey, update)
                                Toast.makeText(
                                    mContext,
                                    mContext.getString(R.string.device_updated),
                                    Toast.LENGTH_SHORT
                                ).show()
                                dialog.dismiss()
                            }
                        }


                    btnDelete.setOnClickListener{

                        Snackbar.make(btnDelete,mContext.getString(R.string.are_you_sure_to_delete),Snackbar.LENGTH_SHORT)
                            .setAction(mContext.getString(R.string.delete)){
                                val delete = mapOf(
                                    "deleteState" to "1",
                                    "deleteTime" to System.currentTimeMillis()
                                )
                                viewModel.deleteDevice(objData.userkey!!,nodeKey, delete)
                                Toast.makeText(mContext,mContext.getString(R.string.device_deleted),Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }.show()

                    }
                })

            }

        })*/

            dialog.setOnDismissListener {
              //  Log.e("alert kapandı", objData.toString())
                ssidEditText.text.clear()
                bssidEditText.text.clear()
                nicknameEditText.text.clear()

            }


        //ssidEditText.setText(rssiData.ssid)

            btnDelete.visibility = View.GONE
      /*  btnDelete.setOnClickListener {
            Toast.makeText(mContext,"DELETE BUTONUN", Toast.LENGTH_SHORT).show()
        }*/

        btnCancel.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(mContext,mContext.getString(R.string.alert_cancel), Toast.LENGTH_SHORT).show()
        }

        clAlertEditDevice.visibility = View.VISIBLE
        clProgressBar.visibility = View.GONE

        dialog.show()



    }



}