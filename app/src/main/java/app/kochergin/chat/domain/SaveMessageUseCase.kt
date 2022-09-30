package app.kochergin.chat.domain

import app.kochergin.chat.data.MessageRepository

class SaveMessageUseCase(private val messageRepository: MessageRepository) {
    suspend operator fun invoke(text: String) {
        return messageRepository.save(
            Message(
                id = System.currentTimeMillis(),
                text = text,
                time = "12:28",
                read = false,
                member = Member.Sender
            )
        )
    }
}