package com.wifianalyzer.wifianalyzerproject.ui.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.wifianalyzer.wifianalyzerproject.R
import com.wifianalyzer.wifianalyzerproject.util.CreateZipFile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AroundWifiResultsDateAdapter(
    var context: Context,
    var result: List<Long>,)
    : RecyclerView.Adapter<AroundWifiResultsDateAdapter.CardViewObjHolder>() {

    class CardViewObjHolder(view : View) : RecyclerView.ViewHolder(view){
        var textViewAroundWifiResultDbUnixtime: TextView =view.findViewById(R.id.textViewAroundWifiResultDbUnixtime)
        var cardViewSavedDataInfo: CardView = view.findViewById(R.id.cardViewSavedDataInfo)
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


        holder.cardViewSavedDataInfo.setOnClickListener {
            Log.e("UNIXTS DEĞERİ -> ", result[position].toString())

            val bundle = Bundle().apply {  putString("unixtimestamp",result[position].toString()) }
            Navigation.findNavController(it).navigate(R.id.action_aroundWifiResultsDate_to_aroundWifiInformationDeviceResult,bundle)

           /* val intent = Intent(context, AroundWifiResultsList::class.java)
           intent.putExtra("unixtimestamp",result[position].toString())
           context.startActivity(intent)*/
        }



    }


    fun convertUnixTimestampToDate(unixTimestamp: Long): String {
        // Unix timestamp'i milisaniye cinsine dönüştür
        val date = Date(unixTimestamp)

        // Tarih formatını belirle
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS", Locale.getDefault())

        // Tarihi belirtilen formata göre biçimlendir
        return dateFormat.format(date)
    }



}