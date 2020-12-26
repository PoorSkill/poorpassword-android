package com.poorskill.poorpassword

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat


class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    class SettingsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }


        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            when (key) {
                getString(R.string.themeKey) -> changeTheme(
                    sharedPreferences.getString(key, getString(R.string.lightThemeKey))
                        .toString()
                )
                getString(R.string.languageKey) -> {
                    changeLanguage()
                }
            }
        }

        private fun changeLanguage() {
            PlayerPreferences.updateLanguage(requireContext())
            val intent = activity?.intent
            this.activity?.finish()
            startActivity(intent)
        }

        private fun changeTheme(themeName: String) {
            var themeId = R.style.LightThemeBase
            when (themeName) {
                getString(R.string.lightThemeKey) -> themeId = R.style.LightThemeBase
                getString(R.string.darkThemeKey) -> themeId = R.style.DarkThemeBase
            }
            PlayerPreferences.setThemePrefs(themeId, requireContext())
            requireContext().setTheme(themeId)
            activity?.recreate()
        }


        override fun onResume() {
            super.onResume()
            preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
            super.onPause()
        }

    }
}