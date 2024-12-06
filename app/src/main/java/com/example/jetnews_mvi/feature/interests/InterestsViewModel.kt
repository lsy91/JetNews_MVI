package com.example.jetnews_mvi.feature.interests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetnews_mvi.data.repository.InterestsRepository
import com.example.jetnews_mvi.mvi.MviModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterestsViewModel @Inject constructor(
    private val interestsRepository: InterestsRepository
) : ViewModel(), MviModel<InterestsIntent, InterestsViewState, InterestsSideEffect> {

    private val _intents = MutableSharedFlow<InterestsIntent>()
    
    private val _viewState = MutableStateFlow<InterestsViewState>(InterestsViewState.Empty)
    override val viewState: StateFlow<InterestsViewState> = _viewState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<InterestsSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        processIntents()
    }

    override fun processIntent(intent: InterestsIntent) {
        viewModelScope.launch {
            _intents.emit(intent)
        }
    }

    private fun processIntents() {
        viewModelScope.launch {
            _intents.collect { intent ->
                when (intent) {
                    is InterestsIntent.LoadInterests -> loadInterests()
                    is InterestsIntent.ToggleInterestSelection -> toggleInterestSelection(intent.interestId)
                }
            }
        }
    }

    private fun loadInterests() {
        viewModelScope.launch {
            _viewState.update { InterestsViewState.Loading }
            
            interestsRepository.getInterests()
                .catch { e ->
                    _viewState.update { InterestsViewState.Error(e.message) }
                    _sideEffect.emit(InterestsSideEffect.ShowError(e.message ?: "Unknown error"))
                }
                .collect { interests ->
                    val selectedInterests = interests.flatMap { section -> 
                        section.interests.filter { it.isSelected }
                    }.toSet()
                    
                    _viewState.update { 
                        InterestsViewState.Success(
                            interestSections = interests,
                            selectedInterests = selectedInterests
                        )
                    }
                }
        }
    }

    private fun toggleInterestSelection(interestId: String) {
        viewModelScope.launch {
            try {
                val isSelected = interestsRepository.toggleInterestSelection(interestId)
                val message = if (isSelected) "Interest added" else "Interest removed"
                _sideEffect.emit(InterestsSideEffect.ShowSelectionMessage(message))
            } catch (e: Exception) {
                _sideEffect.emit(InterestsSideEffect.ShowError(e.message ?: "Failed to update interest"))
            }
        }
    }
}
