package com.informatika.bondoman.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.informatika.bondoman.R
import com.informatika.bondoman.databinding.ActivityMainBinding
import com.informatika.bondoman.databinding.FragmentSettingsBinding
import com.informatika.bondoman.view.activity.MainActivity
import timber.log.Timber

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment();
    }

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.fragment_settings, container, false);
        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.inflate(layoutInflater)

        binding.btnLogout.setOnClickListener {
            Timber.d("Logout clicked");
            (activity as MainActivity).logout();
        }
    }
}