package com.lahsuak.smartgallary.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.lahsuak.smartgallary.R
import com.lahsuak.smartgallary.databinding.ActivityMainBinding
import com.lahsuak.smartgallary.util.AppConstants

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    companion object {
        var sortOrder = AppConstants.SORT_DATE_ADDED
        var spanCount = AppConstants.DEFAULT_SPAN_COUNT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getSharePrefData()
        //this is for transparent status bar and navigation bar
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars =
            true
        //set navigation graph
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun getSharePrefData() {
        getSharedPreferences(
            AppConstants.SharedPrefConstants.GALLERY_SORT_PREF_KEY,
            Context.MODE_PRIVATE
        )
            .apply {
                spanCount =
                    getInt(
                        AppConstants.SharedPrefConstants.SPAN_COUNT_KEY,
                        AppConstants.DEFAULT_SPAN_COUNT
                    )
                sortOrder = getString(
                    AppConstants.SharedPrefConstants.SORT_KEY,
                    AppConstants.SORT_DATE_ADDED
                ) ?: AppConstants.SORT_DATE_ADDED
            }
    }

    private fun savedSharedPrefData() {
        getSharedPreferences(
            AppConstants.SharedPrefConstants.GALLERY_SORT_PREF_KEY,
            Context.MODE_PRIVATE
        )
            .edit().apply {
                putInt(AppConstants.SharedPrefConstants.SPAN_COUNT_KEY, spanCount)
                putString(AppConstants.SharedPrefConstants.SORT_KEY, sortOrder)
                apply()
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        savedSharedPrefData()
    }
}