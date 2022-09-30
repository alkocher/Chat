package app.kochergin.chat.ui

import app.kochergin.chat.databinding.ItemMessageBinding
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

fun messageAdapterDelegate(
    onClick: (MessageItem) -> Unit
) = adapterDelegateViewBinding<MessageItem, ChatItem, ItemMessageBinding>(
    { layoutInflater, parent -> ItemMessageBinding.inflate(layoutInflater, parent, false) }
) {
    binding.root.onClick = onClick

    bind {
        binding.root.bind(item)
    }
}