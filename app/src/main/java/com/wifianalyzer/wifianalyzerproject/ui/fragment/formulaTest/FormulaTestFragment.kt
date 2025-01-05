package com.wifianalyzer.wifianalyzerproject.ui.fragment.formulaTest

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            Log.e("Action","Buton tıklandı")
            FormulTestleri(requireContext().applicationContext,binding.recyclerViewGraph).main()
        }

        return binding.root
    }

}