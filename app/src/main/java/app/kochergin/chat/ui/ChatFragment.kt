package app.kochergin.chat.ui

import android.graphics.Rect
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import app.kochergin.chat.R
import app.kochergin.chat.databinding.FragmentChatBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ChatFragment : Fragment(R.layout.fragment_chat) {

    companion object {
        fun newInstance() = ChatFragment()
    }

    private val blur = Blur()
    private val rect = Rect()
    private lateinit var viewModel: ChatViewModel
    private lateinit var binding: FragmentChatBinding
    private val chatAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ChatAdapter(
            onMessageLongClick = viewModel::onMessageLongClick
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.onBackPressed()
                }
            }
        )
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        binding = FragmentChatBinding.bind(view)
        binding.recycler.adapter = chatAdapter
        (binding.recycler.layoutManager as LinearLayoutManager).stackFromEnd = true

        with(binding.messageEditor) {
            buttonCancel.setOnClickListener { viewModel.onEditCancelClick() }
            buttonSave.setOnClickListener { viewModel.onEditSaveClick(editText.text?.toString() ?: "") }
            buttonOpenGallery.setOnClickListener { viewModel.onOpenGalleryClick() }
            editText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    viewModel.onSendMessageClick(editText.text.toString())
                    editText.text?.clear()
                    true
                } else {
                    false
                }
            }
        }
        binding.contextMenu.buttonEdit.root.setOnClickListener { viewModel.onEditMessageClick() }

        viewModel.messageState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                chatAdapter.submitList(state) {
                    binding.recycler.scrollToPosition(state.size - 1)
                }
            }
            .launchIn(lifecycleScope)

        viewModel.contextMenuState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    ChatViewModel.ContextMenuState.Invisible -> {
                        TransitionManager.beginDelayedTransition(binding.root)
                        binding.contextMenu.root.isVisible = false
                        binding.contextMenu.imageBlur.isVisible = false
                        binding.contextMenu.messageView.isVisible = false
                    }
                    is ChatViewModel.ContextMenuState.Visible -> {
                        blur.onAttach(requireContext())
                        requireView().getGlobalVisibleRect(rect)
                        binding.contextMenu.imageBlur.setImageBitmap(
                            blur.getBlurredBackgroundOf(
                                view = requireActivity().window.decorView,
                                rectOnScreen = rect,
                                blurRadius = 25F
                            )
                        )
                        blur.onDetached()
                        TransitionManager.beginDelayedTransition(binding.root)
                        binding.contextMenu.root.isVisible = true
                        binding.contextMenu.imageBlur.isVisible = true
                        binding.contextMenu.messageView.bind(state.messageItem)
                        binding.contextMenu.messageView.isVisible = true
                        binding.contextMenu.buttonSaveTemplate.textName.text = "Save as a template"
                        binding.contextMenu.buttonSaveTemplate.imageIcon.setImageResource(R.drawable.ic_note_add)
                        binding.contextMenu.buttonCopy.textName.text = "Copy"
                        binding.contextMenu.buttonCopy.imageIcon.setImageResource(R.drawable.ic_copy)
                        binding.contextMenu.buttonEdit.textName.text = "Edit"
                        binding.contextMenu.buttonEdit.imageIcon.setImageResource(R.drawable.ic_edit_pen)
                        binding.contextMenu.buttonDelete.textName.text = "Delete"
                        binding.contextMenu.buttonDelete.imageIcon.setImageResource(R.drawable.ic_trash_can)
                        binding.contextMenu.buttonDelete.divider.isVisible = false
                        viewModel.onEventHandled()
                    }
                }
            }
            .launchIn(lifecycleScope)

        viewModel.messageEditorState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is ChatViewModel.MessageEditorState.Edit -> {
                        with(binding.messageEditor) {
                            buttonCancel.isVisible = true
                            buttonSave.isVisible = true
                            textEditTitle.isVisible = true
                            textMessageToEdit.isVisible = true
                            divider.isVisible = true
                            textMessageToEdit.text = state.messageItem.text
                            editText.inputType = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
                            editText.maxLines = 5
                        }
                    }
                    ChatViewModel.MessageEditorState.Normal -> {
                        with(binding.messageEditor) {
                            buttonCancel.isVisible = false
                            buttonSave.isVisible = false
                            textEditTitle.isVisible = false
                            textMessageToEdit.isVisible = false
                            divider.isVisible = false
                            editText.text?.clear()
                            editText.inputType = EditorInfo.TYPE_CLASS_TEXT
                            editText.maxLines = 1
                        }
                    }
                }
            }
            .launchIn(lifecycleScope)

        viewModel.navigationEvent
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { event ->
                when (event) {
                    ChatViewModel.NavigationEvent.CloseApp -> requireActivity().finish()
                }
            }
            .launchIn(lifecycleScope)
    }
}