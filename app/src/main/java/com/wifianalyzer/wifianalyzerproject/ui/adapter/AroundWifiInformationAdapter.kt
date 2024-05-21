package com.wifianalyzer.wifianalyzerproject.ui.adapter

import android.content.Context
import android.net.wifi.ScanResult
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.wifianalyzer.wifianalyzerproject.R

class AroundWifiInformationAdapter(var context: Context, var result : List<ScanResult>)
    : RecyclerView.Adapter<AroundWifiInformationAdapter.CardViewObjHolder>(){


        class CardViewObjHolder(view : View) : RecyclerView.ViewHolder(view){
            var textViewSSID:TextView = view.findViewById(R.id.textViewAroundWifiSSID)
            var textViewBSSID:TextView = view.findViewById(R.id.textViewAroundWifiBSSID)
            var textViewFrequency:TextView = view.findViewById(R.id.textViewAroundWifiFrequency)
            var textViewLevel:TextView = view.findViewById(R.id.textViewAroundWifiLevel)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewObjHolder {
        val view =LayoutInflater.from(context).inflate(R.layout.card_around_wifi_information,parent,false)
        return CardViewObjHolder(view)
    }

    override fun getItemCount(): Int {
        return result.size
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onBindViewHolder(holder: CardViewObjHolder, position: Int) {
        val newList = result.sortedByDescending { it.level }
        val pos = newList[position]

        val ssid = pos.wifiSsid
        val bssid = pos.BSSID
        val level = pos.level
        val freq = pos.frequency

        holder.textViewSSID.text = "SSID: $ssid"
        holder.textViewBSSID.text = "BSSID: $bssid"
        holder.textViewLevel.text = "Level: $level dBm"
        holder.textViewFrequency.text = "Frequency: $freq mHz"


    }


}