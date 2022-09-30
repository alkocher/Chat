package app.kochergin.chat.domain

import app.kochergin.chat.data.MessageRepository

class EditMessageByIdUseCase(private val messageRepository: MessageRepository) {
    suspend operator fun invoke(id: Long, text: String) {
        val editedMessage = messageRepository.messages.value.find { it.id == id }?.copy(text = text) ?: return
        messageRepository.save(editedMessage, true)
    }
}