package com.poorskill.poorpassword

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ProgressBar
import android.widget.TextView

class PasswordListAdapter(private val context: Context, private val arrayList: ArrayList<String>) :
    BaseAdapter() {

    private lateinit var passwordText: TextView
    private lateinit var byteProgressBar: ProgressBar

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("SetTextI18n", "ViewHolder")
    override fun getView(position: Int, convertViewParam: View?, parent: ViewGroup): View {
        val convertView: View? =
            LayoutInflater.from(context).inflate(R.layout.password_list_item, parent, false)
        passwordText = convertView!!.findViewById(R.id.passwordText)
        byteProgressBar = convertView.findViewById(R.id.progressBarByteSize)
        passwordText.text = arrayList[position]
        val entropy = Utility.getPasswordEntropy(
            arrayList[position],
            context.getString(R.string.lowerCaseCharacter),
            context.getString(R.string.upperCaseCharacter),
            context.getString(R.string.numbersChars)
        )
        @Suppress("DEPRECATION")
        byteProgressBar.progressDrawable.setColorFilter(
            Utility.getPasswordStrengthAsColor(entropy, context),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        byteProgressBar.progress = Utility.entropySecurityLevelAsPercent(
            entropy,
            PlayerPreferences.getMinBitForVeryStrong(context)
        )
        return convertView
    }

}