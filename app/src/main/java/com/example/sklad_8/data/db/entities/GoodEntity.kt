package com.example.sklad_8.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "goods_table")
data class GoodEntity(
    @PrimaryKey
    @SerializedName("uuid")
    val id: String,
    @SerializedName("uuid_parent")
    val parent: String = "",
    @SerializedName("isGroup")
    val isGroup: Boolean = false,
    val name: String,
    @SerializedName("article_number")
    val vendorCode: String
)