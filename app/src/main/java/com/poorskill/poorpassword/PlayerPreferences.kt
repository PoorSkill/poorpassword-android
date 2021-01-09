package com.poorskill.poorpassword

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.preference.PreferenceManager
import java.util.*


abstract class PlayerPreferences {

    companion object {
        private fun getPrefsEditor(context: Context): Editor {
            return getSharedPreferences(context).edit()
        }

        private fun getSharedPreferences(context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }

        internal fun setLowerCaseIncludedPreferences(value: Boolean, context: Context) {
            getPrefsEditor(context).putBoolean(
                context.getString(R.string.prefIsLowerCaseInCharsKey),
                value
            ).apply()
        }

        internal fun isLowerCaseIncludedPreferences(context: Context): Boolean {
            return getSharedPreferences(context).getBoolean(
                context.getString(R.string.prefIsLowerCaseInCharsKey),
                true
            )
        }

        internal fun setUpperCaseIncludedPreferences(value: Boolean, context: Context) {
            getPrefsEditor(context).putBoolean(
                context.getString(R.string.prefIsUpperCaseInCharsKey),
                value
            ).apply()
        }

        internal fun isUpperCaseIncludedPreferences(context: Context): Boolean {
            return getSharedPreferences(context).getBoolean(
                context.getString(R.string.prefIsUpperCaseInCharsKey),
                true
            )
        }

        internal fun setNumberIncludedPreferences(value: Boolean, context: Context) {
            getPrefsEditor(context).putBoolean(
                context.getString(R.string.prefIsNumberInCharsKey),
                value
            ).apply()
        }

        internal fun isNumberIncludedPreferences(context: Context): Boolean {
            return getSharedPreferences(context).getBoolean(
                context.getString(R.string.prefIsNumberInCharsKey),
                true
            )
        }

        internal fun setSymbolIncludedPreferences(value: Boolean, context: Context) {
            getPrefsEditor(context).putBoolean(
                context.getString(R.string.prefIsIncludeCharsKey),
                value
            ).apply()
        }

        internal fun isSymbolsIncludedPreferences(context: Context): Boolean {
            return getSharedPreferences(context).getBoolean(
                context.getString(R.string.prefIsIncludeCharsKey),
                true
            )
        }

        internal fun setSpaceIncludedPreferences(value: Boolean, context: Context) {
            getPrefsEditor(context).putBoolean(
                context.getString(R.string.prefIsSpaceInCharsKey),
                value
            ).apply()
        }

        internal fun isSpaceIncludedPreferences(context: Context): Boolean {
            return getSharedPreferences(context).getBoolean(
                context.getString(R.string.prefIsSpaceInCharsKey),
                true
            )
        }

        internal fun setDistinctPreferences(value: Boolean, context: Context) {
            getPrefsEditor(context).putBoolean(
                context.getString(R.string.prefIsDistinctCharsKey),
                value
            ).apply()
        }

        internal fun isDistinctPreferences(context: Context): Boolean {
            return getSharedPreferences(context).getBoolean(
                context.getString(R.string.prefIsDistinctCharsKey),
                true
            )
        }

        internal fun setExcludePreferences(value: Boolean, context: Context) {
            getPrefsEditor(context).putBoolean(
                context.getString(R.string.prefIsExcludeCharsKey),
                value
            ).apply()
        }

        internal fun isExcludePreferences(context: Context): Boolean {
            return getSharedPreferences(context).getBoolean(
                context.getString(R.string.prefIsExcludeCharsKey),
                true
            )
        }

        internal fun setLocalIncludeCharsPreferences(value: String, context: Context) {
            getPrefsEditor(context).putString(
                context.getString(R.string.prefLocalIncludeCharsKey),
                value
            ).apply()
        }

        internal fun getLocalIncludeCharsPreferences(context: Context): String? {
            return getSharedPreferences(context).getString(
                context.getString(R.string.prefLocalIncludeCharsKey),
                "!\\\"#\$%&'()*+,-./:;<=>?@[]^_`{|}~"
            )
        }

        internal fun setLocalExcludeCharsPreferences(value: String, context: Context) {
            getPrefsEditor(context).putString(
                context.getString(R.string.prefLocalExcludeCharsKey),
                value
            ).apply()
        }

        internal fun getLocalSetExcludePreferences(context: Context): String? {
            return getSharedPreferences(context).getString(
                context.getString(R.string.prefLocalExcludeCharsKey),
                ""
            )
        }

        internal fun setPasswordCountPreferences(value: Int, context: Context) {
            getPrefsEditor(context).putInt(context.getString(R.string.prefPasswordCountKey), value)
                .apply()
        }

        internal fun getSetPasswords(context: Context): Int {
            return getSharedPreferences(context).getInt(
                context.getString(R.string.prefPasswordCountKey),
                1
            )
        }

        internal fun setPasswordLengthPreferences(value: Int, context: Context) {
            getPrefsEditor(context).putInt(context.getString(R.string.prefPasswordLengthKey), value)
                .apply()
        }

        internal fun getSetLength(context: Context): Int {
            return getSharedPreferences(context).getInt(
                context.getString(R.string.prefPasswordLengthKey),
                12
            )
        }

        internal fun setThemePrefs(value: Int, context: Context) {
            getPrefsEditor(context).putInt(context.getString(R.string.prefAppThemeKey), value)
                .apply()
        }

        internal fun getThemePrefs(context: Context): Int {
            return getSharedPreferences(context).getInt(
                context.getString(R.string.prefAppThemeKey),
                R.style.LightThemeBase
            )
        }

        internal fun getCustomDefaultIncludePrefs(context: Context): String? {
            return getSharedPreferences(context).getString(
                context.getString(R.string.prefCustomDefaultIncludeCharsKey),
                context.getString(R.string.defaultIncludeChars)
            )
        }

        internal fun getCustomDefaultExcludePrefs(context: Context): String? {
            return getSharedPreferences(context).getString(
                context.getString(R.string.prefCustomDefaultExcludeCharsKey),
                ""
            )
        }

        internal fun isUsingCustomIncludeCharInputs(context: Context): Boolean {
            return getSharedPreferences(context).getBoolean(
                context.getString(R.string.prefUseCustomIncludeCharKey),
                true
            )
        }

        internal fun isUsingCustomExcludeCharInputs(context: Context): Boolean {
            return getSharedPreferences(context).getBoolean(
                context.getString(R.string.prefUseCustomExcludeCharKey),
                true
            )
        }

        internal fun isAutoGeneratePasswordByChange(context: Context): Boolean {
            return getSharedPreferences(context).getBoolean(
                context.getString(R.string.prefAutoGeneratePasswordByChangeKey),
                true
            )
        }


        internal fun getMinBitForVeryStrong(context: Context): Int {
            return getSharedPreferences(context).getString(
                context.getString(R.string.prefMinBitForStrongKey),
                "127"
            ).toString().toInt()
        }

        internal fun updateLanguage(context: Context) {
            val languageCode = getSharedPreferences(context).getString(
                context.getString(R.string.languageKey),
                context.getString(R.string.englishKey)
            )
            setLocale(context, languageCode.toString())
        }

        private fun setLocale(context: Context, languageCode: String) {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val resources = context.resources
            val config = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
        }

        internal fun isLimitCountAndLength(context: Context): Boolean {
            return getSharedPreferences(context).getBoolean(
                context.getString(R.string.prefLimitCountAndLengthKey),
                true
            )
        }

    }
}