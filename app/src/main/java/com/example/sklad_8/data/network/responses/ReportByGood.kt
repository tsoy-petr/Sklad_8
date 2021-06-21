package com.example.sklad_8.data.network.responses

import com.example.sklad_8.data.repositores.data.Errors
import com.example.sklad_8.data.repositores.data.ResultGoods

data class ReportByGood(
    val errors: Errors,
    val result: String
)