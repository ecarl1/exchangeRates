package com.hfad.exchangerates.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.hfad.exchangerates.databinding.FragmentSlideshowBinding
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import java.text.DecimalFormat

class ConvertorFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!

    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var editTextAmount: EditText
    private lateinit var buttonConvert: Button
    private lateinit var textViewResult: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val currencyViewModel = ViewModelProvider(this).get(CurrencyViewModel::class.java)

        spinnerFrom = binding.spinnerFrom
        spinnerTo = binding.spinnerTo
        editTextAmount = binding.editTextAmount
        buttonConvert = binding.buttonConvert
        textViewResult = binding.textViewResult


        //array of currencies
        val currencies = arrayOf("USD", "AUD", "CNY", "GBP", "CAD", "NZD")

        //configuring the adaptor
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter

        //on click for the conversion button
        buttonConvert.setOnClickListener {
            convertCurrency()
        }

        currencyViewModel.text.observe(viewLifecycleOwner) {
            textViewResult.text = it
        }

        return root
    }

    //method to convert currency
    private fun convertCurrency() {
        //getting proper type for the amount
        val amount = editTextAmount.text.toString().toDoubleOrNull()
        if (amount != null) {
            val fromCurrency = spinnerFrom.selectedItem.toString()
            val toCurrency = spinnerTo.selectedItem.toString()

            //retrieving from the api
            val currencyLink = "https://open.er-api.com/v6/latest/$fromCurrency"
            Thread {
                val url = URL(currencyLink)
                val connection = url.openConnection() as HttpsURLConnection

                if (connection.responseCode == 200) {
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val request = Gson().fromJson(inputStreamReader, Request::class.java)

                    //getting the conversion rate and converting the currency
                    val conversionRate = getConversionRate(request, toCurrency)
                    val result = amount * conversionRate

                    activity?.runOnUiThread {
                        val decimalFormat = DecimalFormat("#,##0.00")
                        textViewResult.text = "${decimalFormat.format(amount)} $fromCurrency = ${decimalFormat.format(result)} $toCurrency"
                    }

                    inputStreamReader.close()
                    inputSystem.close()
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Failed to retrieve exchange rates", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        } else {
            Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
        }
    }

    //gets the conversion rate
    private fun getConversionRate(request: Request, currency: String): Double {
        return when (currency) {
            "USD" -> request.rates.USD
            "AUD" -> request.rates.AUD
            "CNY" -> request.rates.CNY
            "GBP" -> request.rates.GBP
            "CAD" -> request.rates.CAD
            "NZD" -> request.rates.NZD
            else -> 1.0
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
