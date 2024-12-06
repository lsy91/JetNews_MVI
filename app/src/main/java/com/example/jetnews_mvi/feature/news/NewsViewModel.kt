package com.example.jetnews_mvi.feature.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetnews_mvi.data.repository.NewsRepository
import com.example.jetnews_mvi.mvi.MviModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel(), MviModel<NewsIntent, NewsViewState, NewsSideEffect> {

    private val _intents = MutableSharedFlow<NewsIntent>()
    
    private val _viewState = MutableStateFlow<NewsViewState>(NewsViewState.Empty)
    override val viewState: StateFlow<NewsViewState> = _viewState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<NewsSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        processIntents()
    }

    override fun processIntent(intent: NewsIntent) {
        viewModelScope.launch {
            _intents.emit(intent)
        }
    }

    private fun processIntents() {
        viewModelScope.launch {
            _intents.collect { intent ->
                when (intent) {
                    is NewsIntent.LoadNews -> loadNews()
                    is NewsIntent.BookmarkNews -> bookmarkNews(intent.newsId)
                    is NewsIntent.SelectNews -> navigateToDetail(intent.newsId)
                }
            }
        }
    }

    private fun loadNews() {
        viewModelScope.launch {
            _viewState.update { NewsViewState.Loading }
            
            newsRepository.getNews()
                .catch { e ->
                    _viewState.update { NewsViewState.Error(e.message) }
                    _sideEffect.emit(NewsSideEffect.ShowError(e.message ?: "Unknown error"))
                }
                .collect { news ->
                    _viewState.update { NewsViewState.Success(news = news) }
                }
        }
    }

    private fun bookmarkNews(newsId: String) {
        viewModelScope.launch {
            try {
                val isBookmarked = newsRepository.toggleBookmark(newsId)
                
                _viewState.update { state ->
                    if (state is NewsViewState.Success) {
                        val currentBookmarks = state.bookmarkedNewsIds.toMutableSet()
                        if (isBookmarked) {
                            currentBookmarks.add(newsId)
                        } else {
                            currentBookmarks.remove(newsId)
                        }
                        state.copy(bookmarkedNewsIds = currentBookmarks)
                    } else state
                }
                
                val message = if (isBookmarked) "Article bookmarked" else "Bookmark removed"
                _sideEffect.emit(NewsSideEffect.ShowBookmarkMessage(message))
            } catch (e: Exception) {
                _sideEffect.emit(NewsSideEffect.ShowError(e.message ?: "Failed to bookmark"))
            }
        }
    }

    private fun navigateToDetail(newsId: String) {
        viewModelScope.launch {
            _sideEffect.emit(NewsSideEffect.NavigateToDetail(newsId))
        }
    }
}
