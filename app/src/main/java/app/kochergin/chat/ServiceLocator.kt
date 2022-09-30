package app.kochergin.chat

import app.kochergin.chat.data.MessageRepository
import app.kochergin.chat.domain.EditMessageByIdUseCase
import app.kochergin.chat.domain.GetMessageFlowUseCase
import app.kochergin.chat.domain.SaveMessageUseCase

object ServiceLocator {
    private val messageRepository = MessageRepository()
    val saveMessageUseCase = SaveMessageUseCase(messageRepository)
    val getMessageFlowUseCase = GetMessageFlowUseCase(messageRepository)
    val editMessageByIdUseCase = EditMessageByIdUseCase(messageRepository)
}