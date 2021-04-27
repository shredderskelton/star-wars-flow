package com.playground.starwars.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

fun <T> Flow<T>.share(scope: CoroutineScope) =
    shareIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(),
        replay = 1
    )

