package com.poorskill.poorpassword.ui.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.poorskill.poorpassword.R
import com.poorskill.poorpassword.utility.Utility
import com.poorskill.poorpassword.ui.settings.PlayerPreferences

class PasswordDetailDialog(
        context: Context,
        private var passwordsList: ArrayList<String>,
        private val pos: Int
) : Dialog(context) {
    companion object {
        private lateinit var passwordEditText: EditText
        private lateinit var passwordStrength: TextView
        private lateinit var passwordProgressbar: ProgressBar
    }

    init {
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.password_detail_dialog)
        this.setCanceledOnTouchOutside(true)

        passwordEditText = findViewById(R.id.detailPasswordEditText)
        passwordEditText.setText(passwordsList[pos])
        passwordStrength = findViewById(R.id.passwordStrengthDetail)
        passwordProgressbar = findViewById(R.id.progressBarByteSizeDetail)

        findViewById<ImageButton>(R.id.detailCopyBtn).setOnClickListener {
            copyPassword(context)
        }
        findViewById<ImageButton>(R.id.shareDetailBtn).setOnClickListener {
            sharePassword()
        }

        passwordEditText.addTextChangedListener {
            changeTextInParent()
            updateDetailUI()
        }
        updateDetailUI()
    }

    private fun sharePassword() {
        if (passwordEditText.text.isNotEmpty()) {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, passwordEditText.text.toString())
            intent.type = "text/plain"
            context.startActivity(
                    Intent.createChooser(
                            intent,
                            context.getString(R.string.shareTitle)
                    )
            )
        }
    }

    private fun copyPassword(context: Context) {
        if (passwordEditText.text.isNotEmpty()) {
            val clip: ClipData = ClipData.newPlainText("poorpassword", passwordEditText.text)
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(clip)
            Toast.makeText(
                    context,
                    context.getText(R.string.singlePasswordClipboard),
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun changeTextInParent() {
        passwordsList[pos] = passwordEditText.text.toString()
    }

    @SuppressLint("SetTextI18n")
    private fun updateDetailUI() {
        val entropy = Utility.getPasswordEntropy(
            passwordsList[pos],
            context.getString(R.string.lowerCaseCharacter),
            context.getString(R.string.upperCaseCharacter),
            context.getString(R.string.numbersChars)
        )
        passwordProgressbar.progressDrawable.setColorFilter(
            Utility.getPasswordStrengthAsColor(entropy, context),
                android.graphics.PorterDuff.Mode.SRC_IN
        )
        passwordProgressbar.progress = Utility.entropySecurityLevelAsPercent(
            entropy,
            PlayerPreferences.getMinBitForVeryStrong(context)
        )
        passwordStrength.text =
                Utility.getPasswordStrengthAsString(
                    entropy,
                    context
                ) + context.getString(R.string.strengthBitStart) + entropy.toString() + context.getString(
                    R.string.strengthBitEnd
                )
    }

}