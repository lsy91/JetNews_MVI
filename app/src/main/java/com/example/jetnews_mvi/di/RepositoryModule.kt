package com.example.jetnews_mvi.di

import com.example.jetnews_mvi.data.repository.InterestsRepository
import com.example.jetnews_mvi.data.repository.InterestsRepositoryImpl
import com.example.jetnews_mvi.data.repository.NewsRepository
import com.example.jetnews_mvi.data.repository.NewsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        newsRepositoryImpl: NewsRepositoryImpl
    ): NewsRepository

    @Binds
    @Singleton
    abstract fun bindInterestsRepository(
        interestsRepositoryImpl: InterestsRepositoryImpl
    ): InterestsRepository
}
