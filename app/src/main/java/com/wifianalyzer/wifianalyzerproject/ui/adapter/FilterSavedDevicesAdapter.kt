package com.wifianalyzer.wifianalyzerproject.ui.adapter

import android.content.Context
import android.media.RouteListingPreference
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.wifianalyzer.wifianalyzerproject.R
import com.wifianalyzer.wifianalyzerproject.data.DevicesData
import com.wifianalyzer.wifianalyzerproject.data.RssiSignalData
import com.wifianalyzer.wifianalyzerproject.viewmodel.AroundWifiResultsListViewModel

class FilterSavedDevicesAdapter(
    var mContext: Context,
    var result: ArrayList<DevicesData>,
    var viewModel: AroundWifiResultsListViewModel,
    private val lifecycleOwner: LifecycleOwner
)
    : RecyclerView.Adapter<FilterSavedDevicesAdapter.CardViewObjHolder>() {
        private val selectedItems = SparseBooleanArray()

        var selectedList : ArrayList<DevicesData> = arrayListOf()

    inner class CardViewObjHolder(view: View) : RecyclerView.ViewHolder(view) {
        var checkBox : CheckBox = view.findViewById(R.id.checkBoxFilterData)

        fun bind(item: DevicesData, position: Int) {
            checkBox.text = item.nickname
            checkBox.isChecked = selectedItems.get(position, false)

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedItems.put(position, true)
                } else {
                    selectedItems.delete(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewObjHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_filter_data_checkbox,parent,false)
        return CardViewObjHolder(view)
    }

    override fun getItemCount(): Int {
        return result.size
    }

    override fun onBindViewHolder(holder: CardViewObjHolder, position: Int) {
        holder.bind(result[position], position)
    }

    fun getSelectedItems(): ArrayList<DevicesData> {
        val selected = arrayListOf<DevicesData>()
        for (i in 0 until selectedItems.size()) {
            val key = selectedItems.keyAt(i)
            if (selectedItems.get(key)) {
                selected.add(result[key])
            }
        }
        return selected
    }
}



