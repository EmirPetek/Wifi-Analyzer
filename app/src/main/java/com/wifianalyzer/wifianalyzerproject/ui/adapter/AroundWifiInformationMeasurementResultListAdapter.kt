package com.wifianalyzer.wifianalyzerproject.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wifianalyzer.wifianalyzerproject.R
import com.wifianalyzer.wifianalyzerproject.data.MeasurementParameters

class AroundWifiInformationMeasurementResultListAdapter
    (private val measurementList: List<MeasurementParameters>) :
RecyclerView.Adapter<AroundWifiInformationMeasurementResultListAdapter.MeasurementViewHolder>() {


    class MeasurementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewRssi: TextView = itemView.findViewById(R.id.textViewCardMeasurementRssi)
        val textViewAccelerometerX: TextView = itemView.findViewById(R.id.textViewCardMeasurementAccelerometerX)
        val textViewAccelerometerY: TextView = itemView.findViewById(R.id.textViewCardMeasurementAccelerometerY)
        val textViewAccelerometerZ: TextView = itemView.findViewById(R.id.textViewCardMeasurementAccelerometerZ)
        val textViewGyroscopeX: TextView = itemView.findViewById(R.id.textViewCardMeasurementGyroscopeX)
        val textViewGyroscopeY: TextView = itemView.findViewById(R.id.textViewCardMeasurementGyroscopeY)
        val textViewGyroscopeZ: TextView = itemView.findViewById(R.id.textViewCardMeasurementGyroscopeZ)
        val textViewDeviceLocationX: TextView = itemView.findViewById(R.id.textViewCardMeasurementDeviceLocationX)
        val textViewDeviceLocationY: TextView = itemView.findViewById(R.id.textViewCardMeasurementDeviceLocationY)
        val textViewDeviceLocationZ: TextView = itemView.findViewById(R.id.textViewCardMeasurementDeviceLocationZ)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_measurement_result, parent, false)
        return MeasurementViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeasurementViewHolder, position: Int) {
        val measurement = measurementList[position]
        holder.textViewRssi.text = measurement.rssi
        holder.textViewAccelerometerX.text = measurement.accelerometerX
        holder.textViewAccelerometerY.text = measurement.accelerometerY
        holder.textViewAccelerometerZ.text = measurement.accelerometerZ
        holder.textViewGyroscopeX.text = measurement.gyroscopeX
        holder.textViewGyroscopeY.text = measurement.gyroscopeY
        holder.textViewGyroscopeZ.text = measurement.gyroscopeZ
        holder.textViewDeviceLocationX.text = measurement.deviceLocationX
        holder.textViewDeviceLocationY.text = measurement.deviceLocationY
        holder.textViewDeviceLocationZ.text = measurement.deviceLocationZ
    }

    override fun getItemCount(): Int = measurementList.size
}