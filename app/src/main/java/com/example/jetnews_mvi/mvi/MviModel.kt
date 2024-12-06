package com.example.jetnews_mvi.mvi

import kotlinx.coroutines.flow.StateFlow

interface MviIntent
interface MviViewState
interface MviSideEffect
interface MviAction

interface MviModel<I : MviIntent, S : MviViewState, E : MviSideEffect> {
    fun processIntent(intent: I)
    val viewState: StateFlow<S>
}
