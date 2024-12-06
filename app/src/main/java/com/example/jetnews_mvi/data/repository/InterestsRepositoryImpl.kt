package com.example.jetnews_mvi.data.repository

import com.example.jetnews_mvi.model.Interest
import com.example.jetnews_mvi.model.InterestSection
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterestsRepositoryImpl @Inject constructor() : InterestsRepository {
    private val followedInterests = MutableStateFlow<Set<String>>(emptySet())
    private val interestsFlow = MutableStateFlow(sampleInterests)

    override fun getInterests(): Flow<List<InterestSection>> = interestsFlow

    override suspend fun toggleInterestSelection(interestId: String): Boolean {
        val currentFollowed = followedInterests.value
        val newFollowed = if (interestId in currentFollowed) {
            currentFollowed - interestId
        } else {
            currentFollowed + interestId
        }
        followedInterests.value = newFollowed

        // Update interests with new selection state
        interestsFlow.update { sections ->
            sections.map { section ->
                section.copy(
                    interests = section.interests.map { interest ->
                        if (interest.id == interestId) {
                            interest.copy(isSelected = interestId in newFollowed)
                        } else {
                            interest
                        }
                    }
                )
            }
        }

        return interestId in newFollowed
    }

    override fun getFollowedInterestIds(): Flow<Set<String>> = followedInterests

    companion object {
        private val sampleInterests = listOf(
            InterestSection(
                id = "topics",
                title = "Topics",
                interests = listOf(
                    Interest(
                        id = "android",
                        name = "Android",
                        description = "Latest Android news and updates"
                    ),
                    Interest(
                        id = "programming",
                        name = "Programming",
                        description = "Programming tips and best practices"
                    ),
                    Interest(
                        id = "technology",
                        name = "Technology",
                        description = "General technology news and trends"
                    )
                )
            ),
            InterestSection(
                id = "platforms",
                title = "Platforms",
                interests = listOf(
                    Interest(
                        id = "web",
                        name = "Web Development",
                        description = "Web development and frameworks"
                    ),
                    Interest(
                        id = "mobile",
                        name = "Mobile Development",
                        description = "Mobile app development across platforms"
                    ),
                    Interest(
                        id = "cloud",
                        name = "Cloud Computing",
                        description = "Cloud services and infrastructure"
                    )
                )
            )
        )
    }
}
