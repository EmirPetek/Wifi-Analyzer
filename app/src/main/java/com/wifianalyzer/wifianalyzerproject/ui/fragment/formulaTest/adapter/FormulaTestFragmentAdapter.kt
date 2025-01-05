package com.wifianalyzer.wifianalyzerproject.ui.fragment.formulaTest.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.wifianalyzer.wifianalyzerproject.R
import com.wifianalyzer.wifianalyzerproject.formulaTest.FormulTestleri

class FormulaTestFragmentAdapter(
    val mContext:Context,
    val centerCDF: ArrayList<FormulTestleri.CenterCDFDataClass>
): RecyclerView.Adapter<FormulaTestFragmentAdapter.CardHolder>() {

    inner class CardHolder(view: View) : RecyclerView.ViewHolder(view){
        val lineChart: LineChart = view.findViewById(R.id.lineChart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.card_graph_measuring,parent,false)
        return CardHolder(view)
    }

    override fun getItemCount(): Int {
        return centerCDF.size
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        for (i in centerCDF){
            drawGraph(holder,i,itemCount)
        }
    }

    fun drawGraph(holder: CardHolder, data: FormulTestleri.CenterCDFDataClass, itemCount: Int,){
        val entriesZero = data.centersZero.mapIndexed { idx, cx ->
            Entry(cx.toFloat(), data.cdfZero[idx].toFloat())
        }
        val entriesHyp = data.centersHyp.mapIndexed { idx, cx ->
            Entry(cx.toFloat(), data.cdfHyp[idx].toFloat())
        }
        val entriesLst = data.centersLst.mapIndexed { idx, cx ->
            Entry(cx.toFloat(), data.cdfLst[idx].toFloat())
        }

        val dataSetZero = LineDataSet(entriesZero, "LS").apply {
            color = Color.BLUE
            lineWidth = 2f
        }
        val dataSetHyp = LineDataSet(entriesHyp, "WLS").apply {
            color = Color.GREEN
            lineWidth = 2f
        }
        val dataSetLst = LineDataSet(entriesLst, "ERLAK").apply {
            color = Color.RED
            lineWidth = 2f
        }

        val lineData = LineData(dataSetZero, dataSetHyp, dataSetLst)
        holder.lineChart.data = lineData
        holder.lineChart.description.text = "MS$itemCount - CDF Plot"
        holder.lineChart.invalidate()
    }




}