package com.wifianalyzer.wifianalyzerproject.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wifianalyzer.wifianalyzerproject.R
import com.wifianalyzer.wifianalyzerproject.data.RssiSignalData

class AroundWifiResultsListAdapter(var context: Context, var result : List<RssiSignalData>)
    : RecyclerView.Adapter<AroundWifiResultsListAdapter.CardViewObjHolder>()  {

    class CardViewObjHolder(view : View) : RecyclerView.ViewHolder(view){
        var textViewSSID: TextView = view.findViewById(R.id.textViewAroundWifiDBSSID)
        var textViewBSSID: TextView = view.findViewById(R.id.textViewAroundWifiDBBSSID)
        var textViewLocation: TextView = view.findViewById(R.id.textViewAroundWifiDBLocation)
        var textViewLevel: TextView = view.findViewById(R.id.textViewAroundWifiDBLevel)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AroundWifiResultsListAdapter.CardViewObjHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.card_around_wifi_db_result,parent,false)
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


    }


}