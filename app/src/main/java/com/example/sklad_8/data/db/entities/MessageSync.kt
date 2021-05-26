package com.example.sklad_8.data.db.entities

data class MessageSync(
    val title: String,
    val type: TypeMessage
)

enum class TypeMessage{
    SUCCES, ERROR, LOADING
}
