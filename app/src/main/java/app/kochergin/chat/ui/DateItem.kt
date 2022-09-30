package app.kochergin.chat.ui

import kotlin.random.Random

data class DateItem(
    override val id: Long = Random(1000).nextLong(),
    val date: String = "Today"
) : ChatItem(id)