package com.poorskill.poorpassword

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

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