package com.playground.starwars.ui

import com.playground.starwars.ui.person.handler
import kotlinx.coroutines.*

interface CoroutineViewModel {
    val dispatcherProvider: DispatcherProvider

    fun CoroutineScope.launchCoroutine(block: suspend CoroutineScope.() -> Unit): Job =
        launch(context = handler + dispatcherProvider.main(), block = block)
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