package com.example.jetnews_mvi.data.repository

import com.example.jetnews_mvi.model.InterestSection
import kotlinx.coroutines.flow.Flow

interface InterestsRepository {
    fun getInterests(): Flow<List<InterestSection>>
    suspend fun toggleInterestSelection(interestId: String): Boolean
    fun getFollowedInterestIds(): Flow<Set<String>>
}
