package com.example.githubusers.presentation.debug

import kotlin.random.Random

object DebugSampleData {
    private val languages = listOf("Kotlin", "Java", "Swift", "Go", "Python", "Rust")
    private val topics = listOf("analytics", "auth", "compose", "networking", "testing", "tooling")

    fun generate(countPerLanguage: Int = 60): List<DebugSampleItem> {
        val random = Random(42)
        return buildList {
            languages.forEach { language ->
                repeat(countPerLanguage) { index ->
                    val topic = topics[index % topics.size]
                    val stars = random.nextInt(0, 5_000)
                    add(
                        DebugSampleItem(
                            id = "$language-$index",
                            title = "$language $topic sample #$index",
                            language = language,
                            description = "Sample repository showcasing $topic in $language",
                            stars = stars
                        )
                    )
                }
            }
        }
    }
}
