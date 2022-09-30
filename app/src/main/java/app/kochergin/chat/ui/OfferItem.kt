package app.kochergin.chat.ui

import kotlin.random.Random

data class OfferItem(
    override val id: Long = Random(1000).nextLong(),
    val title: String = "Daniel gets your offer for",
    val price: String = "€1 498,00",
    val description: String = "To complete the deal, Daniel must accept your offer, until that you can discuss the details here"
) : ChatItem(id)