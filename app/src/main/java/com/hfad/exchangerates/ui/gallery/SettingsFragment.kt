package com.hfad.exchangerates.ui.gallery

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hfad.exchangerates.R
import com.hfad.exchangerates.databinding.FragmentGalleryBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var appCompatActivity: AppCompatActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appCompatActivity = context as AppCompatActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val settingsViewModel =
            ViewModelProvider(this)[SettingsViewModel::class.java]



        //binding for the different color buttons

        val buttonWhite = binding.buttonWhite
        val buttonRed = binding.buttonRed
        val buttonGreen = binding.buttonGreen
        val buttonBlue = binding.buttonBlue

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

        // Set click listeners for theme buttons
        buttonWhite.setOnClickListener { setAppTheme(AppTheme.WHITE) }
        buttonRed.setOnClickListener { setAppTheme(AppTheme.RED) }
        buttonGreen.setOnClickListener { setAppTheme(AppTheme.GREEN) }
        buttonBlue.setOnClickListener { setAppTheme(AppTheme.BLUE) }

        //Restores the selected theme
        val savedTheme = sharedPreferences.getString("selected_theme", "")
        if (!savedTheme.isNullOrEmpty()) {
            setAppTheme(AppTheme.valueOf(savedTheme), false)
        } else {
            //Default theme is WHITE
            setAppTheme(AppTheme.WHITE, false)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //sets the theme of the background
    private fun setAppTheme(appTheme: AppTheme, saveTheme: Boolean = true) {
        when (appTheme) {
            AppTheme.WHITE -> {
                appCompatActivity.window.setBackgroundDrawableResource(R.color.white_background)
                if (saveTheme) {
                    sharedPreferences.edit().putString("selected_theme", "WHITE").apply()
                }
            }
            AppTheme.RED -> {
                appCompatActivity.window.setBackgroundDrawableResource(R.color.red_background)
                if (saveTheme) {
                    sharedPreferences.edit().putString("selected_theme", "RED").apply()
                }
            }
            AppTheme.GREEN -> {
                appCompatActivity.window.setBackgroundDrawableResource(R.color.green_background)
                if (saveTheme) {
                    sharedPreferences.edit().putString("selected_theme", "GREEN").apply()
                }
            }
            AppTheme.BLUE -> {
                appCompatActivity.window.setBackgroundDrawableResource(R.color.blue_background)
                if (saveTheme) {
                    sharedPreferences.edit().putString("selected_theme", "BLUE").apply()
                }
            }
        }
    }

    private enum class AppTheme {
        WHITE, RED, GREEN, BLUE
    }
}
