package com.wifianalyzer.wifianalyzerproject.ui.fragment.formulaTest

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.wifianalyzer.wifianalyzerproject.databinding.FragmentFormulaTestBinding
import com.wifianalyzer.wifianalyzerproject.formulaTest.FormulTestleri
import com.wifianalyzer.wifianalyzerproject.ui.fragment.formulaTest.adapter.FormulaTestFragmentAdapter

class FormulaTestFragment : Fragment() {

    private lateinit var binding: FragmentFormulaTestBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFormulaTestBinding.inflate(inflater,container,false)

        binding.buttonTest.setOnClickListener {

            // ap koordinatları
            val Xo = doubleArrayOf(5.80, 8.50, 14.10, 18.15, 27.55, 32.20, 41.15, 50.05, 45.95, 61.80)
            val Yo = doubleArrayOf(0.35, 12.00, 2.55, 11.70, 3.30, 10.40, 0.35, 0.35, 12.00, 0.75)

            // ölçüm noktaları
            val pointx = doubleArrayOf(50.10, 5.80, 28.80, 42.60, 12.00, 26.10, 32.70)
            val pointy = doubleArrayOf(5.50, 10.80, 6.20, 5.95, 10.10, 8.65, 1.45)
            setupLineChartWithShapes(binding.lineChart,Xo, Yo, pointx, pointy)

            Log.e("Action","Buton tıklandı")
            FormulTestleri(requireContext().applicationContext,binding.recyclerViewGraph).main()

        }

        return binding.root
    }

    fun setupLineChartWithShapes(
        lineChart: LineChart,
        Xo: DoubleArray,
        Yo: DoubleArray,
        pointx: DoubleArray,
        pointy: DoubleArray
    ) {
        // AP Koordinatlarını Entry formatına dönüştür (Kareler)
        val apEntries = mutableListOf<Entry>()
        for (i in Xo.indices+1) {
            apEntries.add(Entry(Xo[i].toFloat(), Yo[i].toFloat()))
        }

        val apDataSet = LineDataSet(apEntries, "AP Koordinatları").apply {
            color = android.graphics.Color.BLUE
            valueTextColor = android.graphics.Color.BLACK
            lineWidth = 0f // Çizgi yok
            setDrawCircles(true)
            circleRadius = 5f
            setCircleColor(android.graphics.Color.BLUE)
            circleHoleColor = android.graphics.Color.BLUE // Çemberin içi dolu
            setDrawValues(false)
        }

        // Ölçüm Noktalarını Entry formatına dönüştür (Üçgenler)
        val pointEntries = mutableListOf<Entry>()
        for (i in pointx.indices) {
            pointEntries.add(Entry(pointx[i].toFloat(), pointy[i].toFloat()))
            Log.e("Point Entry", "Eklenen Nokta: (${pointx[i]}, ${pointy[i]})")
        }

        val pointDataSet = LineDataSet(pointEntries, "Ölçüm Noktaları").apply {
            color = android.graphics.Color.RED
            valueTextColor = android.graphics.Color.BLACK
            lineWidth = 0f // Çizgi yok
            setDrawCircles(true)
            circleRadius = 8f
            setCircleColor(android.graphics.Color.RED)
            circleHoleColor = android.graphics.Color.RED // Çemberin içi dolu
            setDrawValues(false)
        }

        // Veri setlerini birleştir
        val dataSets = mutableListOf<ILineDataSet>(apDataSet, pointDataSet)

        // Verileri grafiğe ata
        val lineData = LineData(dataSets)
        lineChart.data = lineData



        // Grafik özelliklerini ayarla
        lineChart.apply {
            description.text = "AP Koordinatları ve Ölçüm Noktaları"
            setTouchEnabled(true)
            setPinchZoom(true)
            xAxis.labelRotationAngle = -45f
            axisRight.isEnabled = false


            notifyDataSetChanged()
            invalidate() // Grafiği güncelle
        }
    }

}