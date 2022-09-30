package app.kochergin.chat.ui

import android.content.Context
import android.text.StaticLayout
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import app.kochergin.chat.R
import app.kochergin.chat.databinding.ViewMessageBinding
import app.kochergin.chat.domain.Member

class MessageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val binding: ViewMessageBinding
    private var item: MessageItem? = null
    private val textMessagePaddingStart: Int
    private val textMessagePaddingEnd: Int
    private val textMessageFullWidth: Int
    var onClick: ((MessageItem) -> Unit)? = null

    init {
        binding = ViewMessageBinding.inflate(LayoutInflater.from(context), this)
        binding.root.setOnLongClickListener {
            item?.let { item -> onClick?.invoke(item) }
            true
        }
        binding.textMessage.maxWidth =
            context.resources.displayMetrics.widthPixels - (context.resources.displayMetrics.widthPixels / 5)
        textMessagePaddingStart = binding.textMessage.paddingStart
        textMessagePaddingEnd = binding.textMessage.paddingEnd
        textMessageFullWidth = binding.textMessage.maxWidth - textMessagePaddingStart - textMessagePaddingEnd
    }

    fun bind(item: MessageItem) {
        this.item = item
        binding.textMessage.updatePadding(right = resources.getDimensionPixelOffset(R.dimen.space16))
        binding.textMessage.text = item.text
        binding.textTime.text = item.time
        binding.root.updatePadding(top = context.resources.getDimensionPixelSize(item.topMargin))
        binding.constraintLayout.updateLayoutParams<LayoutParams> {
            when (item.member) {
                Member.Recipient -> {
                    gravity = Gravity.START
                    binding.textMessage.setBackgroundResource(R.drawable.rect_round_message_recepient)
                    binding.imageTongueEnd.isVisible = false
                    binding.imageTongueStart.isVisible = true
                    binding.imageRead.isVisible = false
                    binding.textTime.updatePadding(right = resources.getDimensionPixelOffset(R.dimen.space8))
                }
                Member.Sender -> {
                    gravity = Gravity.END
                    binding.textMessage.setBackgroundResource(R.drawable.rect_round_message_sender)
                    binding.imageTongueEnd.isVisible = true
                    binding.imageTongueStart.isVisible = false
                    binding.imageRead.isVisible = item.read
                    if (item.read) {
                        binding.textTime.updatePadding(right = resources.getDimensionPixelOffset(R.dimen.space4))
                    } else {
                        binding.textTime.updatePadding(right = resources.getDimensionPixelOffset(R.dimen.space8))
                    }
                }
            }
        }

        binding.textMessage.doOnPreDraw {
            val staticLayout = StaticLayout.Builder.obtain(
                item.text,
                0,
                item.text.length,
                binding.textMessage.paint,
                textMessageFullWidth
            ).build()
            val timeTextFullWidth =
                binding.textTime.measuredWidth + binding.textTime.marginStart + binding.textTime.marginEnd
            val imageReadFullWidth =
                binding.imageRead.measuredWidth + binding.imageRead.marginStart + binding.imageRead.marginEnd
            val limitWidth = textMessageFullWidth - timeTextFullWidth - imageReadFullWidth
            val messageLastLineWidth =
                staticLayout.getLineWidth(staticLayout.lineCount - 1) + timeTextFullWidth + imageReadFullWidth
            val newPadding = textMessagePaddingEnd + timeTextFullWidth + imageReadFullWidth

            if (messageLastLineWidth >= limitWidth) {
                binding.textMessage.text = binding.textMessage.text.toString().plus("\n")
            } else if (staticLayout.lineCount == 1) {
                binding.textMessage.updatePadding(right = newPadding)
            }
        }
    }
}