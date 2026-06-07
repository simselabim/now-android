package com.now.core.model

import java.util.UUID

object MockData {
    private val maya = UserProfile(
        id = UUID.randomUUID().toString(),
        name = "Maya",
        age = 29,
        distance = "420 m",
        plan = Plan.Coffee,
        intent = Intent.Date,
        occupation = "Creative",
        languages = listOf("English", "Russian"),
        interests = listOf("Films", "Coffee", "Surfing", "Food", "Travel"),
        sharedInterests = listOf("Films", "Coffee", "Travel"),
        prompt = "I know the best breakfast places and too much about films."
    )

    private val ren = UserProfile(
        id = UUID.randomUUID().toString(),
        name = "Ren",
        age = 31,
        distance = "760 m",
        plan = Plan.Walk,
        intent = Intent.Friendly,
        occupation = "Product",
        languages = listOf("English", "Indonesian"),
        interests = listOf("Design", "Walking", "Music", "Startups"),
        sharedInterests = listOf("Music"),
        prompt = "A good first meeting is simple, public, and easy to leave."
    )

    private val ana = UserProfile(
        id = UUID.randomUUID().toString(),
        name = "Ana",
        age = 27,
        distance = "1.2 km",
        plan = Plan.Dinner,
        intent = Intent.Romantic,
        occupation = "Hospitality",
        languages = listOf("English", "Spanish"),
        interests = listOf("Food", "Dancing", "Beach", "Photography"),
        sharedInterests = listOf("Food", "Beach"),
        prompt = "Dinner, honest conversation, no pressure."
    )

    val mapPoints = listOf(
        MapPoint(UUID.randomUUID().toString(), maya, -8.6504, 115.1387, MapPointState.Unseen, true),
        MapPoint(UUID.randomUUID().toString(), ren, -8.6521, 115.1358, MapPointState.Unseen, false),
        MapPoint(UUID.randomUUID().toString(), ana, -8.6488, 115.1411, MapPointState.Unseen, false)
    )

    val history = listOf(
        HistoryItem(UUID.randomUUID().toString(), "Noah", "Walk yesterday", "Met")
    )
}
