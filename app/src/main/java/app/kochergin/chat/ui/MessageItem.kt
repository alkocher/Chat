package app.kochergin.chat.ui

import androidx.annotation.DimenRes
import app.kochergin.chat.domain.Member
import app.kochergin.chat.domain.Message

data class MessageItem(
    override val id: Long,
    val text: String,
    val time: String,
    val read: Boolean,
    val member: Member,
    @DimenRes val topMargin: Int
) : ChatItem(id)

fun Message.item(@DimenRes topMargin: Int) = MessageItem(
    id = id,
    text = text,
    time = time,
    read = read,
    member = member,
    topMargin = topMargin
)