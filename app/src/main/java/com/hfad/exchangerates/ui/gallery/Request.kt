package com.hfad.exchangerates.ui.gallery

import com.hfad.exchangerates.ui.home.Currency

//handles the api requests
class Request(
    var result: String,
    var time_last_update_utc: String,
    var time_next_update_utc: String,
    var base_code: String,
    var rates: Currency
)