package com.poorskill.poorpassword.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.poorskill.poorpassword.ui.settings.PlayerPreferences

/**
 * BaseActivity - Parent of all Activities
 */
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlayerPreferences.updateLanguage(this)
        setTheme(PlayerPreferences.getThemePrefs(this))
    }
}