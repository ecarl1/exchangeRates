package com.hfad.exchangerates.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.hfad.exchangerates.databinding.FragmentHomeBinding
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class CurrencyValueFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var spinner: Spinner
    private lateinit var textView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val currencyValueViewModel = ViewModelProvider(this).get(CurrencyValueViewModel::class.java)

        textView = binding.textHome
        currencyValueViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        spinner = binding.spinnerCurrencies
        spinner.onItemSelectedListener = this

        //array list of the currencies
        val currencies = arrayOf("USD", "AUD", "CNY", "GBP", "CAD", "NZD")

        //configuring the adapter
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        return root
    }

    //method to handle when an item/currency type is selected
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedCurrency = parent?.getItemAtPosition(position).toString()
        val currencyLink = getCurrencyLink(selectedCurrency)
        fetchCurrencyData(currencyLink)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Handle the case when nothing is selected
    }

    //method to retrieve the currency link
    private fun getCurrencyLink(currency: String): String {
        return when (currency) {
            "USD" -> "https://open.er-api.com/v6/latest/usd"
            "AUD" -> "https://open.er-api.com/v6/latest/aud"
            "CNY" -> "https://open.er-api.com/v6/latest/cny"
            "GBP" -> "https://open.er-api.com/v6/latest/gbp"
            "CAD" -> "https://open.er-api.com/v6/latest/cad"
            "NZD" -> "https://open.er-api.com/v6/latest/nzd"
            else -> ""
        }
    }

    //method to retrive info from the api
    private fun fetchCurrencyData(currencyLink: String) {
        Thread {
            val url = URL(currencyLink)
            val connection = url.openConnection() as HttpsURLConnection

            if (connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val request = Gson().fromJson(inputStreamReader, Request::class.java)
                updateUI(request)
                inputStreamReader.close()
                inputSystem.close()
            } else {
                println("Failed Connection")
            }
        }.start()
    }

    //method to update the on screen values for the currency values
    private fun updateUI(request: Request) {
        activity?.runOnUiThread {
            textView.text = String.format(
                "NZD: %.2f\nUSD: %.2f\nGBP: %.2f\nCNY: %.2f\nAUD: %.2f\nCAD: %.2f",
                request.rates.NZD,
                request.rates.USD,
                request.rates.GBP,
                request.rates.CNY,
                request.rates.AUD,
                request.rates.CAD
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
