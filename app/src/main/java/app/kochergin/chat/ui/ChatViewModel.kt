package app.kochergin.chat.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kochergin.chat.R
import app.kochergin.chat.ServiceLocator
import app.kochergin.chat.domain.EditMessageByIdUseCase
import app.kochergin.chat.domain.GetMessageFlowUseCase
import app.kochergin.chat.domain.Member
import app.kochergin.chat.domain.Message
import app.kochergin.chat.domain.SaveMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ChatViewModel(
    private val saveMessageUseCase: SaveMessageUseCase = ServiceLocator.saveMessageUseCase,
    private val getMessageFlowUseCase: GetMessageFlowUseCase = ServiceLocator.getMessageFlowUseCase,
    private val editMessageByIdUseCase: EditMessageByIdUseCase = ServiceLocator.editMessageByIdUseCase
) : ViewModel() {
    private val mutateContextMenuState = MutableStateFlow<ContextMenuState?>(null)
    private val mutateMessageEditorState = MutableStateFlow<MessageEditorState>(MessageEditorState.Normal)
    private val mutateNavigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val contextMenuState = mutateContextMenuState.asStateFlow().filterNotNull()
    val messageEditorState = mutateMessageEditorState.asStateFlow()
    val navigationEvent = mutateNavigationEvent.asStateFlow().filterNotNull()
    val messageState = getMessageFlowUseCase().map { list ->
        buildList {
            add(DateItem())
            add(OfferItem())
            var prev: Message? = null
            list.forEach { message ->
                val topMargin = if (prev?.member == message.member) R.dimen.space4 else R.dimen.space14
                prev = message
                add(message.item(topMargin))
            }
        }
    }

    fun onMessageLongClick(item: MessageItem) {
        if (item.member == Member.Recipient) return
        mutateContextMenuState.value = ContextMenuState.Visible(item)
    }

    fun onEventHandled() {
        mutateNavigationEvent.value = null
    }

    fun onEditCancelClick() {
        mutateMessageEditorState.value = MessageEditorState.Normal
    }

    fun onOpenGalleryClick() {
        // TODO Handle click
    }

    fun onEditSaveClick(text: String) {
        if (text.isBlank()) {
            mutateMessageEditorState.value = MessageEditorState.Normal
            return
        }
        val messageItem = (mutateMessageEditorState.value as? MessageEditorState.Edit)?.messageItem ?: return
        mutateMessageEditorState.value = MessageEditorState.Normal
        viewModelScope.launch { editMessageByIdUseCase(messageItem.id, text) }
    }

    fun onEditMessageClick() {
        val messageItem = (mutateContextMenuState.value as? ContextMenuState.Visible)?.messageItem ?: return
        mutateContextMenuState.value = ContextMenuState.Invisible
        mutateMessageEditorState.value = MessageEditorState.Edit(messageItem)
    }

    fun onSendMessageClick(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch { saveMessageUseCase(text) }
    }

    fun onBackPressed() {
        if (mutateContextMenuState.value is ContextMenuState.Visible) {
            mutateContextMenuState.value = ContextMenuState.Invisible
        } else {
            mutateNavigationEvent.value = NavigationEvent.CloseApp
        }
    }

    sealed interface ContextMenuState {
        object Invisible : ContextMenuState
        data class Visible(
            val messageItem: MessageItem
        ) : ContextMenuState
    }

    sealed interface MessageEditorState {
        object Normal : MessageEditorState
        data class Edit(
            val messageItem: MessageItem
        ) : MessageEditorState
    }

    sealed interface NavigationEvent {
        object CloseApp : NavigationEvent
    }
}