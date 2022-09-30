package app.kochergin.chat.ui

import app.kochergin.chat.databinding.ItemOfferBinding
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

fun offerAdapterDelegate() = adapterDelegateViewBinding<OfferItem, ChatItem, ItemOfferBinding>(
    { layoutInflater, parent -> ItemOfferBinding.inflate(layoutInflater, parent, false) }
) {
    bind {
        binding.textTitle.text = item.title
        binding.textPrice.text = item.price
        binding.textDesc.text = item.description
    }
}