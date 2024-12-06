package com.example.jetnews_mvi.data.repository

import com.example.jetnews_mvi.model.News
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getNews(): Flow<List<News>>
    suspend fun toggleBookmark(newsId: String): Boolean
    fun getBookmarkedNewsIds(): Flow<Set<String>>
}
