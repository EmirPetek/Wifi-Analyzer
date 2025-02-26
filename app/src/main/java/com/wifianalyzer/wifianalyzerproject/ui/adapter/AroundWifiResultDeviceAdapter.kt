package com.wifianalyzer.wifianalyzerproject.ui.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.wifianalyzer.wifianalyzerproject.R

class AroundWifiResultDeviceAdapter(
    var context: Context,
    var result: List<String>,
    val folderNameAsunixtimestamp: Long
)
: RecyclerView.Adapter<AroundWifiResultDeviceAdapter.CardViewObjHolder>() {

    class CardViewObjHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textViewCardDeviceResultName: TextView =
            view.findViewById(R.id.textViewCardDeviceResultName)
        var cardviewDeviceResultName: CardView = view.findViewById(R.id.cardviewDeviceResultName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewObjHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_device_result, parent, false)
        return CardViewObjHolder(view)
    }

    override fun getItemCount(): Int {
        return result.size
    }

    override fun onBindViewHolder(holder: CardViewObjHolder, position: Int) {

        holder.textViewCardDeviceResultName.text = result.get(position)

        holder.cardviewDeviceResultName.setOnClickListener {
            Log.e("txtFileName -> ", result[position].toString())
            Log.e("folderName -> ", folderNameAsunixtimestamp.toString())


             val bundle = Bundle().apply {
                 putString("folderName",folderNameAsunixtimestamp.toString())
                 putString("txtFileName",result.get(position))
             }
             Navigation.findNavController(it).navigate(R.id.action_aroundWifiInformationDeviceResult_to_aroundWifiInformationMeasurementResultList,bundle)

            /* val intent = Intent(context, AroundWifiResultsList::class.java)
            intent.putExtra("unixtimestamp",result[position].toString())
            context.startActivity(intent)*/
        }


    }

}