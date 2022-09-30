package app.kochergin.chat.data

import app.kochergin.chat.domain.Member
import app.kochergin.chat.domain.Message
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MessageRepository {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    suspend fun save(message: Message, edit: Boolean = false) {
        saveInternal(message)
        if (!edit) sendFakeMessageAfterDelay(message)
    }

    private suspend fun sendFakeMessageAfterDelay(message: Message) {
        delay(2000)
        saveInternal(message.copy(read = true))
        delay(2000)
        saveInternal(
            message.copy(
                id = System.currentTimeMillis(),
                text = PLACEHOLDER_MESSAGE,
                member = Member.Recipient
            )
        )
    }

    private fun saveInternal(message: Message) {
        _messages.update { current ->
            val new = current.toMutableList()
            val msgIndex = new.indexOfFirst { it.id == message.id }
            if (msgIndex != -1) {
                new[msgIndex] = message
            } else {
                new.add(message)
            }
            new
        }
    }

    private companion object {
        const val PLACEHOLDER_MESSAGE =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
    }
}