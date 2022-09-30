package app.kochergin.chat.ui

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import kotlin.jvm.internal.Intrinsics

class ChatAdapter(
    onMessageLongClick: (MessageItem) -> Unit
) : AsyncListDifferDelegationAdapter<ChatItem>(DiffCallback) {

    init {
        delegatesManager
            .addDelegate(messageAdapterDelegate(onMessageLongClick))
            .addDelegate(dateAdapterDelegate())
            .addDelegate(offerAdapterDelegate())
    }

    fun submitList(items: List<ChatItem>, commitCallback: Runnable) {
        differ.submitList(items, commitCallback)
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<ChatItem>() {
        override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return Intrinsics.areEqual(oldItem, newItem)
        }
    }
}