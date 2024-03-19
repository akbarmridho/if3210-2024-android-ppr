package com.informatika.bondoman.ui.twibbon

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.informatika.bondoman.R

class TwibbonFragment : Fragment() {

    companion object {
        fun newInstance() = TwibbonFragment()
    }

    private val viewModel: TwibbonViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_twibbon, container, false)
    }
}