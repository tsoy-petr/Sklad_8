package com.example.sklad_8.data.network.responses

class InfoAtPackegs(
    val result: List<ResultsInfoAtPackegs>?,
    val errors: ErrorResponseData
)

class ResultsInfoAtPackegs(
    val portionsId: String,
    val portions: List<Portion>
)

class Portion(
    val numberPortion: Int,
    val dataType: String
)

class ErrorResponseData(
    val exist: Boolean,
    val title: String
)
