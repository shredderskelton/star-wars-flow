package com.playground.starwars.ui

import kotlinx.coroutines.delay

interface Simulator {
    suspend fun delay()
}

private const val DELAY = 500L

private suspend fun simulateDelay() {
    delay(DELAY)
}

class SimulatorImpl() : Simulator {
    override suspend fun delay() {
        simulateDelay()
    }
}

object SimulatorDummy : Simulator {
    override suspend fun delay() = Unit
}