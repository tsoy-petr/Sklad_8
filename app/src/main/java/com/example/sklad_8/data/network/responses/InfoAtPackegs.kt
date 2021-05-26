package com.example.sklad_8.data.network.responses

import com.example.sklad_8.data.repositores.data.Errors

class InfoAtPackegs(
    val result: List<ResultsInfoAtPackegs>?,
    val errors: Errors
)

class ResultsInfoAtPackegs(
    val portionsId: String,
    val portions: List<Portion>
)

class Portion(
    val numberPortion: Int,
    val dataType: String
)

