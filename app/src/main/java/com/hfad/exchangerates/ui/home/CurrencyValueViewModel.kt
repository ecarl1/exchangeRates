package com.hfad.exchangerates.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CurrencyValueViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Currency Values"
    }
    val text: LiveData<String> = _text
}