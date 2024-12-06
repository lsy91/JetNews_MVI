package com.example.jetnews_mvi.feature.interests

import com.example.jetnews_mvi.model.Interest
import com.example.jetnews_mvi.model.InterestSection
import com.example.jetnews_mvi.mvi.MviIntent
import com.example.jetnews_mvi.mvi.MviSideEffect
import com.example.jetnews_mvi.mvi.MviViewState

sealed class InterestsIntent : MviIntent {
    object LoadInterests : InterestsIntent()
    data class ToggleInterestSelection(val interestId: String) : InterestsIntent()
}

sealed class InterestsViewState : MviViewState {
    object Empty : InterestsViewState()
    object Loading : InterestsViewState()
    data class Error(val message: String?) : InterestsViewState()
    data class Success(
        val interestSections: List<InterestSection> = emptyList(),
        val selectedInterests: Set<Interest> = emptySet()
    ) : InterestsViewState()
}

sealed class InterestsSideEffect : MviSideEffect {
    data class ShowError(val message: String) : InterestsSideEffect()
    data class ShowSelectionMessage(val message: String) : InterestsSideEffect()
}
