package app.kochergin.chat.ui

import app.kochergin.chat.databinding.ItemDateBinding
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

fun dateAdapterDelegate() = adapterDelegateViewBinding<DateItem, ChatItem, ItemDateBinding>(
    { layoutInflater, parent -> ItemDateBinding.inflate(layoutInflater, parent, false) }
) {
    bind {
        binding.textDate.text = item.date
    }
}