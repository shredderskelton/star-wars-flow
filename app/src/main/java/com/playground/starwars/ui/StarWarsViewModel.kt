package com.playground.starwars.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playground.starwars.service.Loggable
import com.playground.starwars.service.logDebug
import com.playground.starwars.ui.person.handler
import kotlinx.coroutines.*

open class StarWarsViewModel(private val dispatcherProvider: DispatcherProvider) : ViewModel(),
    Loggable {

    fun launchCoroutine(block: suspend CoroutineScope.() -> Unit): Job =
        viewModelScope.launch(context = handler + dispatcherProvider.main(), block = block)
}

interface DispatcherProvider {
    fun main(): CoroutineDispatcher
    fun default(): CoroutineDispatcher
    fun io(): CoroutineDispatcher
    fun unconfined(): CoroutineDispatcher
}

class DefaultDispatcherProvider : DispatcherProvider {
    override fun main(): CoroutineDispatcher = Dispatchers.Main
    override fun default(): CoroutineDispatcher = Dispatchers.Default
    override fun io(): CoroutineDispatcher = Dispatchers.IO
    override fun unconfined(): CoroutineDispatcher = Dispatchers.Unconfined
}
