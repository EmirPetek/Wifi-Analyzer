package com.wifianalyzer.wifianalyzerproject.ui.fragment.formulaTest.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
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
    private val mContext: Context,
    private val centerCDF: ArrayList<FormulTestleri.CenterCDFDataClass>
) : RecyclerView.Adapter<FormulaTestFragmentAdapter.CardHolder>() {

    inner class CardHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lineChart: LineChart = view.findViewById(R.id.lineChart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.card_graph_measuring, parent, false)
        return CardHolder(view)
    }

    override fun getItemCount(): Int {
        return centerCDF.size
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {

        // Yalnızca o satıra (position) özgü centerCDF verisini alıyoruz
        val item = centerCDF[position]
        // position+1 => MS1..MS7 arası numaralandırma isterseniz
    }

    /**
     * Bir ölçüm noktasının (CenterCDFDataClass) verilerini ilgili satırdaki lineChart'a çizdirir.
     * @param msIndex opsiyonel, grafiğin açıklamasında (description) "MS{msIndex}" yazmak için.
     */
    private fun drawGraph(
        holder: CardHolder,
        data: FormulTestleri.CenterCDFDataClass,
        msIndex: Int
    ) {
        // 1) Verileri Entry listesine dönüştürelim
        val entriesZero = data.centersZero.mapIndexed { idx, cx ->
            Entry(cx.toFloat(), data.cdfZero[idx].toFloat())
        }
        val entriesHyp = data.centersHyp.mapIndexed { idx, cx ->
            Entry(cx.toFloat(), data.cdfHyp[idx].toFloat())
        }
        val entriesLst = data.centersLst.mapIndexed { idx, cx ->
            Entry(cx.toFloat(), data.cdfLst[idx].toFloat())
        }

        // 2) DataSet'leri oluştur
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

        // 3) LineData'ya ekle
        val lineData = LineData(dataSetZero, dataSetHyp, dataSetLst)

        holder.lineChart.data = lineData
        // Grafiğin açıklamasında hangi measurement point olduğunu gösterebilirsiniz
        holder.lineChart.description.text = "MS$msIndex - CDF Plot"
        holder.lineChart.invalidate()
    }
}
