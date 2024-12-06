package com.example.jetnews_mvi.model

data class Interest(
    val id: String,
    val name: String,
    val description: String? = null,
    val isSelected: Boolean = false
)

data class InterestSection(
    val id: String,
    val title: String,
    val interests: List<Interest>
)
