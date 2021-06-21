package com.example.sklad_8.ui.util

import android.app.Service
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.showKeyboard() {
    requestFocus()
    val manager = context.getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.showSoftInput(this, 0);
}

fun View.hideKeyboard() {
    clearFocus()
    val manager = context.getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(windowToken, 0)
    clearFocus()
}