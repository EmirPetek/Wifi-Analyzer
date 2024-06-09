package com.wifianalyzer.wifianalyzerproject.ui.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.wifianalyzer.wifianalyzerproject.R
import com.wifianalyzer.wifianalyzerproject.ui.activity.AroundWifiResultsList
import com.wifianalyzer.wifianalyzerproject.viewmodel.AroundWifiResultsDateViewModel
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

            val intent = Intent(context,AroundWifiResultsList::class.java)
           intent.putExtra("unixtimestamp",result[position].toString())
           context.startActivity(intent)
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