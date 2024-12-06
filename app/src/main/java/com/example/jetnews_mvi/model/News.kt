package com.example.jetnews_mvi.model

data class News(
    val id: String,
    val title: String,
    val subtitle: String,
    val url: String,
    val publication: Publication,
    val metadata: Metadata,
    val paragraphs: List<Paragraph>,
    val imageUrl: String
)

data class Publication(
    val name: String,
    val publishDate: String
)

data class Metadata(
    val author: Author,
    val date: String,
    val readTimeMinutes: Int
)

data class Author(
    val name: String,
    val url: String
)

data class Paragraph(
    val type: ParagraphType,
    val text: String,
    val markups: List<Markup> = emptyList()
)

enum class ParagraphType {
    Title, Caption, Header, Subhead, Text, CodeBlock, Quote
}

data class Markup(
    val type: MarkupType,
    val start: Int,
    val end: Int,
    val href: String? = null
)

enum class MarkupType {
    Link, Code, Italic, Bold
}
