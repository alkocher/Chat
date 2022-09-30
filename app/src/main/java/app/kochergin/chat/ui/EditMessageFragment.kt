package app.kochergin.chat.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import app.kochergin.chat.R
import app.kochergin.chat.databinding.FragmentEditMessageBinding

class EditMessageFragment : Fragment(R.layout.fragment_edit_message) {
    private lateinit var binding: FragmentEditMessageBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditMessageBinding.bind(view)
    }
}