package com.wifianalyzer.wifianalyzerproject.ui.adapter

import android.content.Context
import android.net.wifi.ScanResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wifianalyzer.wifianalyzerproject.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AroundWifiResultsDateAdapter(var context: Context, var result : List<Long>)
    : RecyclerView.Adapter<AroundWifiResultsDateAdapter.CardViewObjHolder>() {

    class CardViewObjHolder(view : View) : RecyclerView.ViewHolder(view){
        var textViewAroundWifiResultDbUnixtime: TextView =view.findViewById(R.id.textViewAroundWifiResultDbUnixtime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewObjHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_saved_data_information,parent,false)
        return AroundWifiResultsDateAdapter.CardViewObjHolder(view)
    }

    override fun getItemCount(): Int {
        return result.size
    }

    override fun onBindViewHolder(holder: CardViewObjHolder, position: Int) {
        var pos = convertUnixTimestampToDate(result[position])

        holder.textViewAroundWifiResultDbUnixtime.text = pos
    }


    fun convertUnixTimestampToDate(unixTimestamp: Long): String {
        // Unix timestamp'i milisaniye cinsine dönüştür
        val date = Date(unixTimestamp )

        // Tarih formatını belirle
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS", Locale.getDefault())

        // Tarihi belirtilen formata göre biçimlendir
        return dateFormat.format(date)
    }

}