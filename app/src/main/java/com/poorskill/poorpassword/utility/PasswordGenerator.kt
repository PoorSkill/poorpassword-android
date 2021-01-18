package com.poorskill.poorpassword.utility

import java.security.SecureRandom

class PasswordGenerator {

    companion object {

        internal fun getUsableCharacter(
            includeLowerCase: Boolean,
            includeUpperCase: Boolean,
            includeNumbers: Boolean,
            includeSpace: Boolean,
            includeCustomCharacter: Boolean,
            customIncludeCharacter: String,
            excludeCustomCharacter: Boolean,
            customExcludeCharacter: String,
            lowerCaseCharacters: String,
            upperCaseCharacters: String
        ): java.lang.StringBuilder {
            val usableCharacter = StringBuilder()
            if (includeLowerCase) {
                usableCharacter.append(lowerCaseCharacters)
            }
            if (includeUpperCase) {
                usableCharacter.append(upperCaseCharacters)
            }
            if (includeNumbers) {
                for (i in 0..9)
                    usableCharacter.append(i)
            }
            if (includeSpace) {
                usableCharacter.append(" ")
            }
            if (includeCustomCharacter && customIncludeCharacter.isNotBlank()) {
                usableCharacter.append(customIncludeCharacter)
            }

            if (excludeCustomCharacter && customExcludeCharacter.isNotEmpty()) {
                for (c: Char in customExcludeCharacter) {
                    var lastIndex = usableCharacter.lastIndexOf(c)
                    while (lastIndex > -1) {
                        usableCharacter.deleteCharAt(lastIndex)
                        lastIndex = usableCharacter.lastIndexOf(c)
                    }

                }
            }
            return usableCharacter
        }

        internal fun generatePasswords(
            amount: Int,
            length: Int,
            isDistinct: Boolean,
            usableCharacter: StringBuilder
        ): ArrayList<String> {
            val passwordList = ArrayList<String>()
            for (x in 0 until amount) {
                passwordList.add(generateNewPassword(length, isDistinct, usableCharacter))
            }
            return passwordList
        }

        private fun generateNewPassword(
            length: Int,
            isDistinct: Boolean,
            usableCharacter: StringBuilder
        ): String {
            if (length < 1) {
                return ""
            }
            val newPassword = StringBuilder()
            val tempUsableCharacter = StringBuilder(usableCharacter)
            for (x in 1..length) {
                if (isDistinct) {
                    newPassword.append(getRandomUsableDistinctCharacter(tempUsableCharacter))
                } else {
                    newPassword.append(getRandomUsableCharacter(usableCharacter))
                }
            }
            return newPassword.toString()
        }

        private fun getRandomUsableCharacter(usableCharacter: StringBuilder): Any {
            return usableCharacter[getSecureIntInRange(usableCharacter.length)]
        }

        private fun getRandomUsableDistinctCharacter(usableCharacter: StringBuilder): Char {
            val randomNumberInRange = getSecureIntInRange(usableCharacter.length)
            val randomCharacter = usableCharacter[randomNumberInRange]
            usableCharacter.deleteCharAt(randomNumberInRange)
            return randomCharacter
        }

        private fun getSecureIntInRange(range: Int): Int {
            return SecureRandom().nextInt(range)
        }

    }
}