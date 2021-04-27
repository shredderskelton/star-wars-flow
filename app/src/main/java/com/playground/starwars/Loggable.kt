package com.playground.starwars

import android.util.Log

interface Loggable

fun Loggable.logDebug(block: () -> String) {
    Log.d(this.javaClass.simpleName, block())
}