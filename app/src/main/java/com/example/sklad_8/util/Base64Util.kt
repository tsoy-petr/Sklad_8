package com.example.sklad_8.util

import android.util.Base64

object Base64Util {

    fun String.toBase64AsByteArray(): ByteArray? {
        return try {
            Base64.decode(this, Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }

}