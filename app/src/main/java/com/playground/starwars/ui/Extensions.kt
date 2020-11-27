package com.playground.starwars.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

public fun <T> Flow<T>.share(scope: CoroutineScope) =
    shareIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(0, 0),
        replay = 1
    )