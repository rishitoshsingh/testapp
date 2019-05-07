package com.alphae.testapp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG: String = "MainActivity"
    val SELFIE_UPLOADED: Int = 201

    private var numpadState: Boolean = false
    private var numberValidated = false
    private var selfieUploaded = false

    private var mBottomSheetBehaviour: BottomSheetBehavior<*>? = null
    private lateinit var uploadedUrl: String

    private lateinit var mRadioOtpButtons: ArrayList<RadioButton>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewInitializations()
        listnersInitializations()
    }

    fun numberClicked(view: View) {
        if (number_text_view.isFocused) {
            val textView = view as TextView
            number_text_view.text.append(textView.text)
        }
    }

    fun clearClicked(view: View) {
        if (number_text_view.isFocused && number_text_view.text.isNotEmpty()) {
            val length = number_text_view.text.length
            number_text_view.text.delete(length - 1, length)
        }
    }

    private fun viewInitializations() {
        number_text_view.showSoftInputOnFocus = false
        take_selfie.requestFocus()
        val nestedScrollView = findViewById<View>(R.id.numpad_bottom_sheet)
        mBottomSheetBehaviour = BottomSheetBehavior.from(nestedScrollView)

        mRadioOtpButtons.apply {
            add(first)
            add(second)
            add(third)
            add(fourth)
            add(fifth)
            add(sixth)
        }
    }

    private fun listnersInitializations() {
        fab.setOnClickListener {
            if (numpadState) {
                numpadState = false
                mBottomSheetBehaviour?.state = BottomSheetBehavior.STATE_COLLAPSED
                fab.setImageDrawable(getDrawable(R.drawable.ic_arrow_up))
            } else {
                numpadState = true
                mBottomSheetBehaviour?.state = BottomSheetBehavior.STATE_EXPANDED
                fab.setImageDrawable(getDrawable(R.drawable.ic_arrow_down))
            }
        }

        number_text_view.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                numpadState = true
                mBottomSheetBehaviour?.state = BottomSheetBehavior.STATE_EXPANDED
                fab.setImageDrawable(getDrawable(R.drawable.ic_arrow_down))
            } else {
                numpadState = false
                mBottomSheetBehaviour?.state = BottomSheetBehavior.STATE_COLLAPSED
                fab.setImageDrawable(getDrawable(R.drawable.ic_arrow_up))
            }
        }

        number_text_view.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val number = s.toString().length
                if (number > 10)
                    s?.delete(number - 1, number)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        take_selfie.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivityForResult(intent, SELFIE_UPLOADED)
        }

        login_button.setOnClickListener {
            numberValidation()
            if (numberValidated && selfieUploaded) {
                Toast.makeText(this, "Login", Toast.LENGTH_LONG).show()
            } else if (!numberValidated) Toast.makeText(this, "Check Your Number", Toast.LENGTH_LONG).show()
            else if (!selfieUploaded) Toast.makeText(this, "Please Take your Selfie", Toast.LENGTH_LONG).show()
        }

    }

    private fun numberValidation() {
        numberValidated = number_text_view.text.length == 10
    }

    private fun enterOtp(position: Int) {
        mRadioOtpButtons[position].isChecked = true
    }

    private fun deleteOtp(position: Int) {
        mRadioOtpButtons[position].isChecked = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == SELFIE_UPLOADED) {
            selfieUploaded = true
            if (data != null) {
                uploadedUrl = data.getStringExtra("uploadedUrl")
            }
        } else Toast.makeText(this, "Error during Taking Selfie", Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        if (numpadState) {
            number_text_view.isFocusable = false
            number_text_view.isFocusableInTouchMode = true

            numpadState = false
            mBottomSheetBehaviour?.state = BottomSheetBehavior.STATE_COLLAPSED
            fab.setImageDrawable(getDrawable(R.drawable.ic_arrow_up))
        } else super.onBackPressed()
    }
}
