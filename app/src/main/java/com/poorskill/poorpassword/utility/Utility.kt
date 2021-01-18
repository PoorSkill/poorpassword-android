package com.poorskill.poorpassword.utility

import android.content.Context
import android.graphics.Color
import com.poorskill.poorpassword.R
import com.poorskill.poorpassword.ui.settings.PlayerPreferences
import kotlin.math.log10

object Utility {

    @JvmStatic
    internal fun getPasswordEntropy(password: String, lowerCaseCharacter: String, upperCaseCharacter: String, numbers: String): Int {
        var n = 0
        var uniqueCharacters = 0

        for (i in password.indices) {
            if (i == password.indexOf(password[i])) {
                uniqueCharacters++
            }
        }

        var hasLowerCase = false
        var hasUpperCase = false
        var hasNumbers = false
        var otherCharacters = ""

        for (character in password) {
            if (!hasLowerCase && lowerCaseCharacter.contains(character)) {
                n += lowerCaseCharacter.length
                hasLowerCase = true
            } else if (!hasUpperCase && upperCaseCharacter.contains(character)) {
                n += upperCaseCharacter.length
                hasUpperCase = true
            } else if (!hasNumbers && numbers.contains(character)) {
                n += numbers.length
                hasNumbers = true
            } else if (!otherCharacters.contains(character)) {
                n++
                otherCharacters += character
            }
        }

        val passwordEntropy = (uniqueCharacters * (log10(n.toDouble()) / log10(2.0))).toInt()
        return if (passwordEntropy == 0 && password.isNotEmpty()) 1 else passwordEntropy
    }


    internal fun getPasswordStrengthAsString(entropy: Int, context: Context): String {
        val entropyInPercent =
                entropy * 100f / PlayerPreferences.getMinBitForVeryStrong(context)
        var strengthString = context.getString(R.string.strengthVeryWeak)
        when {
            entropyInPercent <= 25 -> {
                //Very Weak
                //Already Very Weak
            }
            entropyInPercent <= 25 -> {
                //Weak
                strengthString = context.getString(R.string.strengthWeak)
            }
            entropyInPercent <= 75 -> {
                //Reasonable
                strengthString = context.getString(R.string.strengthReasonable)
            }
            entropyInPercent < 100 -> {
                //Strong
                strengthString = context.getString(R.string.strengthStrong)
            }
            else -> {
                strengthString = context.getString(R.string.strengthVeryStrong)
                //Very Strong
            }
        }
        return strengthString
    }

    internal fun getPasswordStrengthAsColor(entropy: Int, context: Context): Int {
        val entropyInPercent =
                entropy * 100f / PlayerPreferences.getMinBitForVeryStrong(context)
        var passwordColor = Color.BLACK
        when {
            entropyInPercent <= 25 -> {
                //Very Weak
                //AlreadyBlack
            }
            entropyInPercent <= 25 -> {
                //Weak
                passwordColor = Color.rgb(255, 165, 0)
            }
            entropyInPercent <= 75 -> {
                //Reasonable
                passwordColor = Color.YELLOW
            }
            entropyInPercent < 100 -> {
                //Strong
                passwordColor = Color.GREEN
            }
            else -> {
                passwordColor = Color.MAGENTA
                //Very Strong
            }
        }
        return passwordColor
    }

    internal fun entropySecurityLevelAsPercent(
            entropy: Int,
            minSecurityAsHundredPercent: Int
    ): Int {
        return if (entropy > minSecurityAsHundredPercent) 100 else (entropy * 100f / minSecurityAsHundredPercent).toInt()
    }
}