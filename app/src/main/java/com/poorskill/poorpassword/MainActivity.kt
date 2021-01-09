package com.poorskill.poorpassword

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener


class MainActivity : BaseActivity() {

    companion object {
        private lateinit var expandableLayout: LinearLayout
        private lateinit var inputCardView: CardView
        private lateinit var expandInputButton: Button

        private lateinit var includeLowerCaseCheck: CheckBox
        private lateinit var includeUpperCaseCheck: CheckBox
        private lateinit var includeNumbersCheck: CheckBox
        private lateinit var includeSymbolsCheck: CheckBox
        private lateinit var isDistinctCheck: CheckBox
        private lateinit var generatePasswordButton: Button
        private lateinit var passwordLengthEditText: EditText
        private lateinit var usableSymbolsEditText: EditText
        private lateinit var excludeCheck: CheckBox
        private lateinit var excludeInput: EditText
        private lateinit var passwordCountInput: EditText
        private lateinit var spaceCheck: CheckBox
        private lateinit var copyToClipboardButton: Button

        private lateinit var addPasswordBtn: Button
        private lateinit var subPasswordBtn: Button
        private lateinit var addLengthBtn: Button
        private lateinit var subLengthBtn: Button

        private var usableCharacter = StringBuilder()

        private lateinit var passwords: ArrayList<String>
        private var passwordAdapter: PasswordListAdapter? = null
        private lateinit var passwordListView: ListView

        private var wasSettings = false

        private const val LENGTH_COUNT_MAX_VALUE = 50
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_menu, menu)
        return true
    }

    override fun onRestart() {
        //Not found a good way to refresh theme yet :(
        if (wasSettings) {
            wasSettings = false
            recreate()
        }
        super.onRestart()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settingsItem -> openSettingsActivity()
            R.id.resetSymbolsItem -> {
                resetInputCharacters()
            }
            R.id.aboutItem -> openAboutActivity()
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }

    private fun resetInputCharacters() {
        //Reset Include
        val defaultCharsInclude: String =
            if (PlayerPreferences.isUsingCustomIncludeCharInputs(this)) {
                PlayerPreferences.getCustomDefaultIncludePrefs(this).toString()
            } else {
                getString(R.string.defaultIncludeChars)
            }
        usableSymbolsEditText.setText(defaultCharsInclude)
        PlayerPreferences.setLocalIncludeCharsPreferences(defaultCharsInclude, this)
        //Reset Exclude
        val defaultCharsExclude: String =
            if (PlayerPreferences.isUsingCustomExcludeCharInputs(this)) {
                PlayerPreferences.getCustomDefaultExcludePrefs(this).toString()
            } else {
                ""
            }
        excludeInput.setText(defaultCharsExclude)
        PlayerPreferences.setLocalExcludeCharsPreferences(defaultCharsExclude, this)
    }

    private fun openSettingsActivity() {
        wasSettings = true
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun openAboutActivity() {
        startActivity(Intent(this, AboutActivity::class.java))
    }

    private fun openDetailPasswordDialog(position: Int) {
        val dialog = PasswordDetailDialog(this, passwords, position)
        dialog.setOnDismissListener {
            passwordAdapter?.notifyDataSetChanged()
        }
        dialog.show()
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        passwords = ArrayList()
        //SetViewVar
        inputCardView = findViewById(R.id.inputCardView)
        expandableLayout = findViewById(R.id.expandableLayout)
        expandInputButton = findViewById(R.id.expandInputButton)
        includeLowerCaseCheck = findViewById(R.id.lowercaseSwitch)
        includeUpperCaseCheck = findViewById(R.id.uppercaseSwitch)
        includeNumbersCheck = findViewById(R.id.numbersSwitch)
        includeSymbolsCheck = findViewById(R.id.symbolsSwitch)
        isDistinctCheck = findViewById(R.id.distinctSwitch)
        generatePasswordButton = findViewById(R.id.generatePasswordBtn)
        passwordLengthEditText = findViewById(R.id.passwordLengthInput)
        usableSymbolsEditText = findViewById(R.id.usableSymbols)
        copyToClipboardButton = findViewById(R.id.copyToClipboard)
        spaceCheck = findViewById(R.id.spaceCheck)
        excludeCheck = findViewById(R.id.excludeCheck)
        excludeInput = findViewById(R.id.excludeInput)
        passwordCountInput = findViewById(R.id.passwordCountInput)
        addPasswordBtn = findViewById(R.id.addPasswordBtn)
        subPasswordBtn = findViewById(R.id.subPasswordBtn)
        addLengthBtn = findViewById(R.id.addLengthBtn)
        subLengthBtn = findViewById(R.id.subLengthBtn)
        passwordListView = findViewById(R.id.password_list_view)
        //Filter
        passwordCountInput.addTextChangedListener { filterTextNumberInput(passwordCountInput) }
        passwordLengthEditText.addTextChangedListener { filterTextNumberInput(passwordLengthEditText) }
        //Listener
        generatePasswordButton.setOnClickListener { generateNewPasswordButtonClick() }
        includeUpperCaseCheck.setOnCheckedChangeListener { _, isChecked ->
            PlayerPreferences.setUpperCaseIncludedPreferences(isChecked, this)
            changeOfUsableCharacter()
        }
        includeNumbersCheck.setOnCheckedChangeListener { _, isChecked ->
            PlayerPreferences.setNumberIncludedPreferences(isChecked, this)
            changeOfUsableCharacter()
        }
        includeSymbolsCheck.setOnCheckedChangeListener { _, isChecked ->
            PlayerPreferences.setSymbolIncludedPreferences(isChecked, this)
            usableSymbolsEditText.isEnabled = includeSymbolsCheck.isChecked
            usableSymbolsEditText.isClickable = includeSymbolsCheck.isChecked
            if (includeSymbolsCheck.isChecked) {
                usableSymbolsEditText.clearFocus()
            }
            changeOfUsableCharacter()
        }

        isDistinctCheck.setOnCheckedChangeListener { _, isChecked ->
            PlayerPreferences.setDistinctPreferences(isChecked, this)
            changeOfUsableCharacter()
        }
        passwordCountInput.addTextChangedListener {
            if (passwordCountInput.text.isNotEmpty()) {
                when {
                    passwordCountInput.text.toString().toInt() <= 1 -> {
                        subPasswordBtn.isEnabled = false
                        subPasswordBtn.isClickable = false
                    }
                    passwordCountInput.text.toString()
                        .toInt() >= Int.MAX_VALUE -> {
                        addPasswordBtn.isEnabled = false
                        addPasswordBtn.isClickable = false
                    }
                    passwordCountInput.text.toString()
                        .toInt() >= LENGTH_COUNT_MAX_VALUE && PlayerPreferences.isLimitCountAndLength(
                        this
                    ) -> {
                        addPasswordBtn.isEnabled = false
                        addPasswordBtn.isClickable = false
                    }
                    else -> {
                        addPasswordBtn.isEnabled = true
                        addPasswordBtn.isClickable = true
                        subPasswordBtn.isEnabled = true
                        subPasswordBtn.isClickable = true
                    }
                }
                PlayerPreferences.setPasswordCountPreferences(
                    passwordCountInput.text.toString().toInt(), this
                )
            }
            if (PlayerPreferences.isAutoGeneratePasswordByChange(this)) {
                changeOfUsableCharacter()
            }
        }
        passwordLengthEditText.addTextChangedListener {
            if (passwordLengthEditText.text.isNotEmpty()) {
                when {
                    passwordLengthEditText.text.toString().toInt() <= 1 -> {
                        subLengthBtn.isEnabled = false
                        subLengthBtn.isClickable = false
                    }
                    passwordLengthEditText.text.toString().toInt() >= Int.MAX_VALUE -> {
                        addLengthBtn.isEnabled = false
                        addLengthBtn.isClickable = false
                    }
                    passwordLengthEditText.text.toString()
                        .toInt() >= LENGTH_COUNT_MAX_VALUE && PlayerPreferences.isLimitCountAndLength(
                        this
                    ) -> {
                        addLengthBtn.isEnabled = false
                        addLengthBtn.isClickable = false
                    }
                    else -> {
                        addLengthBtn.isEnabled = true
                        addLengthBtn.isClickable = true
                        subLengthBtn.isEnabled = true
                        subLengthBtn.isClickable = true
                    }
                }
                PlayerPreferences.setPasswordLengthPreferences(
                    passwordLengthEditText.text.toString().toInt(), this
                )
            }
            changeOfUsableCharacter()
        }
        usableSymbolsEditText.addTextChangedListener {
            PlayerPreferences.setLocalIncludeCharsPreferences(
                usableSymbolsEditText.text.toString(),
                this
            )
            changeOfUsableCharacter()
        }
        includeLowerCaseCheck.setOnCheckedChangeListener { _, isChecked ->
            PlayerPreferences.setLowerCaseIncludedPreferences(isChecked, this)
            changeOfUsableCharacter()
        }
        copyToClipboardButton.setOnClickListener {
            copyPasswordsToClipboard()
        }
        spaceCheck.setOnCheckedChangeListener { _, isChecked ->
            PlayerPreferences.setSpaceIncludedPreferences(isChecked, this)
            changeOfUsableCharacter()
        }
        excludeCheck.setOnClickListener {
            PlayerPreferences.setExcludePreferences(excludeCheck.isChecked, this)
            excludeInput.isEnabled = excludeCheck.isChecked
            excludeInput.isClickable = excludeCheck.isChecked
            if (excludeCheck.isChecked) {
                excludeInput.clearFocus()
            }
            changeOfUsableCharacter()
        }
        excludeInput.addTextChangedListener {
            PlayerPreferences.setLocalExcludeCharsPreferences(excludeInput.text.toString(), this)
            changeOfUsableCharacter()
        }
        //PasswordCountButtons
        addPasswordBtn.setOnClickListener { addPasswordCounter(true) }
        subPasswordBtn.setOnClickListener { addPasswordCounter(false) }
        //PasswordLengthButtons
        addLengthBtn.setOnClickListener { addLengthCounter(true) }
        subLengthBtn.setOnClickListener { addLengthCounter(false) }
        expandInputButton.setOnClickListener {
            if (expandableLayout.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(inputCardView, AutoTransition())
                expandableLayout.visibility = View.VISIBLE
                expandInputButton.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            } else {
                TransitionManager.beginDelayedTransition(inputCardView, AutoTransition())
                expandableLayout.visibility = View.GONE
                expandInputButton.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
        }
        inputCardView.setOnClickListener {
            if (expandableLayout.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(inputCardView, AutoTransition())
                expandableLayout.visibility = View.VISIBLE
                expandInputButton.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            }
        }
        //SetUpListView
        passwordAdapter = PasswordListAdapter(this, passwords)
        passwordListView.adapter = passwordAdapter
        passwordListView.setOnItemClickListener { _, _, position, _ ->
            openDetailPasswordDialog(position)
        }
        //SetDefaultValue//PlayerPrefs
        includeLowerCaseCheck.isChecked = PlayerPreferences.isLowerCaseIncludedPreferences(this)
        includeUpperCaseCheck.isChecked = PlayerPreferences.isUpperCaseIncludedPreferences(this)
        includeNumbersCheck.isChecked = PlayerPreferences.isNumberIncludedPreferences(this)
        includeSymbolsCheck.isChecked = PlayerPreferences.isSymbolsIncludedPreferences(this)
        isDistinctCheck.isChecked = PlayerPreferences.isDistinctPreferences(this)
        excludeCheck.isChecked = PlayerPreferences.isExcludePreferences(this)
        usableSymbolsEditText.setText(
            PlayerPreferences.getLocalIncludeCharsPreferences(this).toString()
        )
        excludeInput.setText(PlayerPreferences.getLocalSetExcludePreferences(this).toString())
        passwordLengthEditText.setText(PlayerPreferences.getSetLength(this).toString())
        passwordCountInput.setText(PlayerPreferences.getSetPasswords(this).toString())
        spaceCheck.isChecked = PlayerPreferences.isSpaceIncludedPreferences(this)
        excludeInput.isClickable = excludeCheck.isChecked
        excludeInput.isEnabled = excludeCheck.isChecked
        usableSymbolsEditText.isClickable = includeSymbolsCheck.isChecked
        usableSymbolsEditText.isEnabled = includeSymbolsCheck.isChecked
        copyToClipboardButton.isClickable = false
        copyToClipboardButton.isEnabled = false

    }

    private fun filterTextNumberInput(editText: EditText) {
        if (editText.text.isNotEmpty()) {
            if (editText.text.toString().toInt() == 0) {
                editText.setText("1")
                editText.setSelection(editText.text.length)
            } else if (PlayerPreferences.isLimitCountAndLength(this) && editText.text.toString()
                    .toInt() > LENGTH_COUNT_MAX_VALUE
            ) {
                editText.setText(LENGTH_COUNT_MAX_VALUE.toString())
                editText.setSelection(editText.text.length)
            }
        }
    }

    private fun copyPasswordsToClipboard() {
        val passwordsClipboard = StringBuilder()
        for (s in passwords) {
            passwordsClipboard.append(s + "\n")
        }
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label.toString(), passwordsClipboard)
        clipboard.setPrimaryClip(clip)
        val toastText = when {
            passwords.isEmpty() -> {
                //Should never occur
                getString(R.string.passwordsClipboardEmpty)
            }
            passwords.size < 2 -> {
                getString(R.string.singlePasswordClipboard)
            }
            else -> {
                passwords.size.toString() + " " + getString(R.string.allPasswordsCopiedToClipboard)
            }
        }
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
    }


    private fun addLengthCounter(positive: Boolean) {
        if (positive) {
            passwordLengthEditText.setText(
                (passwordLengthEditText.text.toString().toInt() + 1).toString()
            )
        } else {
            passwordLengthEditText.setText(
                (passwordLengthEditText.text.toString().toInt() - 1).toString()
            )
        }
        if (passwordLengthEditText.text.toString()
                .toInt() < 1
        ) passwordLengthEditText.setText(1.toString())
        if (passwordLengthEditText.text.toString()
                .toInt() >= Int.MAX_VALUE - 1
        ) passwordLengthEditText.setText((Int.MAX_VALUE - 1).toString())
    }

    private fun addPasswordCounter(positive: Boolean) {
        if (positive) {
            passwordCountInput.setText((passwordCountInput.text.toString().toInt() + 1).toString())
        } else {
            passwordCountInput.setText((passwordCountInput.text.toString().toInt() - 1).toString())
        }
        if (passwordCountInput.text.toString().toInt() < 1) passwordCountInput.setText(1.toString())
        if (passwordCountInput.text.toString()
                .toInt() >= Int.MAX_VALUE - 1
        ) passwordCountInput.setText((Int.MAX_VALUE - 1).toString())
        PlayerPreferences.setPasswordCountPreferences(
            passwordCountInput.text.toString().toInt(),
            this
        )
    }

    private fun changeOfUsableCharacter() {
        usableCharacter = PasswordGenerator.getUsableCharacter(
            includeLowerCaseCheck.isChecked,
            includeUpperCaseCheck.isChecked,
            includeNumbersCheck.isChecked,
            spaceCheck.isChecked,
            includeSymbolsCheck.isChecked,
            getIncludeCharacter(),
            excludeCheck.isChecked,
            getExcludeCharacter(),
            getString(R.string.lowerCaseCharacter),
            getString(R.string.upperCaseCharacter)
        )
        if (checkIfGeneratePasswordIsPossible(
                isDistinctCheck.isChecked,
                getUserPasswordLength()
            ) && PlayerPreferences.isAutoGeneratePasswordByChange(this)
        ) {
            generatePasswords(
                PlayerPreferences.getSetPasswords(this),
                PlayerPreferences.getSetLength(this),
                isDistinctCheck.isChecked
            )

        }
    }

    private fun getIncludeCharacter(): String {
        val includeCharacter = StringBuilder("")
        if (usableSymbolsEditText.text.isNotEmpty()) {
            includeCharacter.append(usableSymbolsEditText.text.toString())
        }
        return includeCharacter.toString()
    }

    private fun getExcludeCharacter(): String {
        val excludeCharacter = StringBuilder("")
        if (excludeInput.text.isNotEmpty()) {
            excludeCharacter.append(excludeInput.text.toString())
        }
        return excludeCharacter.toString()
    }

    private fun getUserPasswordLength(): Int {
        var length = 0
        if (passwordLengthEditText.text.isNotEmpty()) {
            length = passwordLengthEditText.text.toString().toInt()
        }
        return length
    }

    private fun getUserPasswordCount(): Int {
        var amount = 0
        if (passwordCountInput.text.isNotEmpty()) {
            amount = passwordCountInput.text.toString().toInt()
        }
        return amount
    }

    /**
     * Check if generate Password is possible and dis-/enable button
     */
    private fun checkIfGeneratePasswordIsPossible(isDistinct: Boolean, length: Int): Boolean {
        return if (isDistinct && usableCharacter.length < length) {
            generatePasswordButton.isEnabled = false
            generatePasswordButton.isClickable = false
            generatePasswordButton.isClickable = false
            false
        } else if (usableCharacter.isEmpty()) {
            generatePasswordButton.isEnabled = false
            generatePasswordButton.isClickable = false
            false
        } else {
            generatePasswordButton.isEnabled = true
            generatePasswordButton.isClickable = true
            true
        }
    }


    private fun generateNewPasswordButtonClick() {
        generatePasswords(getUserPasswordCount(), getUserPasswordLength(), isDistinct())
    }

    private fun generatePasswords(amount: Int, length: Int, isDistinct: Boolean) {
        passwords.clear()
        copyToClipboardButton.isClickable = true
        copyToClipboardButton.isEnabled = true

        if (checkIfGeneratePasswordIsPossible(isDistinct, length)) {
            //Reference needs to be the same for Adapter
            passwords.addAll(
                PasswordGenerator.generatePasswords(amount, length, isDistinct, usableCharacter)
            )
            passwordAdapter?.notifyDataSetChanged()
            passwordListView.adapter = passwordAdapter
        }
    }

    private fun isDistinct(): Boolean {
        return isDistinctCheck.isChecked
    }

}