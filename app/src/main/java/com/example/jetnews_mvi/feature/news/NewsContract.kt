package com.example.jetnews_mvi.feature.news

import com.example.jetnews_mvi.model.News
import com.example.jetnews_mvi.mvi.MviIntent
import com.example.jetnews_mvi.mvi.MviSideEffect
import com.example.jetnews_mvi.mvi.MviViewState

sealed class NewsIntent : MviIntent {
    object LoadNews : NewsIntent()
    data class BookmarkNews(val newsId: String) : NewsIntent()
    data class SelectNews(val newsId: String) : NewsIntent()
}

sealed class NewsViewState : MviViewState {
    object Empty : NewsViewState()
    object Loading : NewsViewState()
    data class Error(val message: String?) : NewsViewState()
    data class Success(
        val news: List<News> = emptyList(),
        val bookmarkedNewsIds: Set<String> = emptySet()
    ) : NewsViewState()
}

sealed class NewsSideEffect : MviSideEffect {
    data class NavigateToDetail(val newsId: String) : NewsSideEffect()
    data class ShowError(val message: String) : NewsSideEffect()
    data class ShowBookmarkMessage(val message: String) : NewsSideEffect()
}
