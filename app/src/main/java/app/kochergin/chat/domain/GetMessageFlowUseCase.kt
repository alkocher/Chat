package app.kochergin.chat.domain

import app.kochergin.chat.data.MessageRepository

class GetMessageFlowUseCase(private val messageRepository: MessageRepository) {
    operator fun invoke() = messageRepository.messages
}