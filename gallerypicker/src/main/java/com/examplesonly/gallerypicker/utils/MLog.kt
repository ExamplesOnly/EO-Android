package com.examplesonly.gallerypicker.utils

import android.util.Log

object MLog {
    var canLog = true
    fun e(tag: String, message: String?) {
        if (canLog) Log.e(tag, message)
    }

    fun d(tag: String, message: String?) {
        if (canLog) Log.d(tag, message)
    }

    fun v(tag: String, message: String?) {
        if (canLog) Log.v(tag, message)
    }
}