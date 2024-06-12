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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
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

    class CardViewObjHolder(view : View) : RecyclerView.ViewHolder(view){
        var textViewSSID: TextView = view.findViewById(R.id.textViewAroundWifiDBSSID)
        var textViewBSSID: TextView = view.findViewById(R.id.textViewAroundWifiDBBSSID)
        var textViewLocation: TextView = view.findViewById(R.id.textViewAroundWifiDBLocation)
        var textViewLevel: TextView = view.findViewById(R.id.textViewAroundWifiDBLevel)
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

        Log.e("aroundwifiresultslistadapter: ", ssid!!)

        holder.textViewSSID.text = "SSID: $ssid"
        holder.textViewBSSID.text = "BSSID: $bssid"
        holder.textViewLevel.text = "Level: $level dBm"
        holder.textViewLocation.text = "Location: $location"

        val objData = pos

        holder.imageButtonEditWifiResult.setOnClickListener {
            showAlertDialog(objData)
        }



    }


    fun showAlertDialog(objData: RssiSignalData) {

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


        ssidEditText.setText(objData.ssid)
        bssidEditText.setText(objData.bssid)

        viewModel.getIsDeviceSavedState(objData.userkey!!, objData.bssid!!)
        viewModel.deviceFoundData.observe(lifecycleOwner, Observer {
            var nodeKey = it
            Log.e("nodekey viewmodelin birinci içi", it.toString())
            if (nodeKey == null){

                val textEdittextNickname = nicknameEditText.text.toString()

                val device = DevicesData(
                    objData.userkey!!,
                    System.currentTimeMillis(),
                    "0",
                    null,
                    objData.ssid!!,
                    objData.bssid!!,
                    textEdittextNickname
                )

                btnAdd.setOnClickListener {
                    viewModel.insertDevice(device)
                    Toast.makeText(mContext,mContext.getString(R.string.device_added), Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }

            }else{
                viewModel.getDeviceData(objData.userkey,nodeKey)
                viewModel.deviceData.observe(lifecycleOwner, Observer {
                    nicknameEditText.setText(it.nickname)
                    bssidEditText.setText(it.bssid)
                    Log.e("ikinci viewmodel içinde", it.toString())
                })

            }

        })


        //ssidEditText.setText(objData.ssid)


        btnDelete.setOnClickListener {
            Toast.makeText(mContext,"DELETE BUTONUN", Toast.LENGTH_SHORT).show()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(mContext,mContext.getString(R.string.alert_cancel), Toast.LENGTH_SHORT).show()
        }

        dialog.show()



    }



}