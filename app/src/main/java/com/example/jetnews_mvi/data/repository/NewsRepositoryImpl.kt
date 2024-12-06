package com.example.jetnews_mvi.data.repository

import com.example.jetnews_mvi.model.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepositoryImpl @Inject constructor() : NewsRepository {
    private val bookmarkedNews = MutableStateFlow<Set<String>>(emptySet())
    private val newsFlow = MutableStateFlow<List<News>>(sampleNews)

    override fun getNews(): Flow<List<News>> = newsFlow

    override suspend fun toggleBookmark(newsId: String): Boolean {
        val currentBookmarks = bookmarkedNews.value
        val newBookmarks = if (newsId in currentBookmarks) {
            currentBookmarks - newsId
        } else {
            currentBookmarks + newsId
        }
        bookmarkedNews.value = newBookmarks
        return newsId in newBookmarks
    }

    override fun getBookmarkedNewsIds(): Flow<Set<String>> = bookmarkedNews

    companion object {
        private val sampleNews = listOf(
            News(
                id = "1",
                title = "Android Jetpack Compose",
                subtitle = "Building Android UI with Jetpack Compose",
                url = "https://developer.android.com/jetpack/compose",
                publication = Publication(
                    name = "Android Developers",
                    publishDate = "2023-10-15"
                ),
                metadata = Metadata(
                    author = Author(
                        name = "Android Team",
                        url = "https://developer.android.com"
                    ),
                    date = "2023-10-15",
                    readTimeMinutes = 5
                ),
                paragraphs = listOf(
                    Paragraph(
                        type = ParagraphType.Text,
                        text = "Jetpack Compose is Android's modern toolkit for building native UI."
                    )
                ),
                imageUrl = "https://developer.android.com/images/hero-compose-desktop.png"
            )
        )
    }
}
