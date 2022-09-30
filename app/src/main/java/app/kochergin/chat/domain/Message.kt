package app.kochergin.chat.domain

data class Message(
    val id: Long,
    val text: String,
    val time: String,
    val read: Boolean,
    val member: Member
)

sealed interface Member {
    object Sender : Member
    object Recipient : Member
}