package com.alphae.testapp

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private var numpad_state: Boolean = false
    private var mBottomSheetBehaviour: BottomSheetBehavior<*>? = null
    private val NUMBER_MESSAGE: String = "Enter Your Number"
    private var numberCount = 0
    private val TAG: String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nestedScrollView = findViewById<View>(R.id.numpad_bottom_sheet)
        mBottomSheetBehaviour = BottomSheetBehavior.from(nestedScrollView)

        fab.setOnClickListener {
            if (numberCount == 10) {
                val ft: android.support.v4.app.FragmentTransaction = supportFragmentManager.beginTransaction()
                val dialogFragment = CameraDialog()
                dialogFragment.show(ft, "cameraDialog")

            }
            if (numpad_state) {
                numpad_state = false
                (mBottomSheetBehaviour as BottomSheetBehavior<*>?)?.state = BottomSheetBehavior.STATE_COLLAPSED
                fab.setImageDrawable(getDrawable(R.drawable.ic_arrow_up))
            } else {
                numpad_state = true
                (mBottomSheetBehaviour as BottomSheetBehavior<*>?)?.state = BottomSheetBehavior.STATE_EXPANDED
                fab.setImageDrawable(getDrawable(R.drawable.ic_arrow_down))
            }
        }

        number_text_view.setOnClickListener {
            if (numpad_state) {
                numpad_state = false
                (mBottomSheetBehaviour as BottomSheetBehavior<*>?)?.state = BottomSheetBehavior.STATE_COLLAPSED
                fab.setImageDrawable(getDrawable(R.drawable.ic_arrow_up))
            } else {
                numpad_state = true
                (mBottomSheetBehaviour as BottomSheetBehavior<*>?)?.state = BottomSheetBehavior.STATE_EXPANDED
                fab.setImageDrawable(getDrawable(R.drawable.ic_arrow_down))
            }
        }

    }

    fun numberClicked(view: View) {
        if (number_text_view.text == NUMBER_MESSAGE) number_text_view.text = ""
        if (numberCount != 10) {
            val textView = view as TextView
            number_text_view.text = number_text_view.text.toString() + textView.text
            numberCount++
        }

        if (numberCount == 10) {
            val fabDrawable = resources.getDrawable(R.drawable.ic_check)
            val newFabIcon = fabDrawable.constantState!!.newDrawable()
            newFabIcon.mutate().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY)
            fab.setImageDrawable(newFabIcon)
        }

    }

    fun clearClicked(view: View) {
        if (numberCount != 0) {
            val textView = view as TextView
            val number = number_text_view.text.toString()
            number_text_view.text = number.substring(0, number.length - 1)
            numberCount--
            val fabDrawable = resources.getDrawable(R.drawable.ic_arrow_down)
            val newFabIcon = fabDrawable.constantState!!.newDrawable()
            newFabIcon.mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
            fab.setImageDrawable(newFabIcon)
        }
        if (numberCount == 0) number_text_view.text = NUMBER_MESSAGE
    }


//    private fun login() {
//
//        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                Log.d(TAG, "onVerificationCompleted:$credential")
//                signInWithPhoneAuthCredential(credential)
//                val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
//            }
//
//            override fun onVerificationFailed(e: FirebaseException) {
//                Log.w(TAG, "onVerificationFailed", e)
//                if (e is FirebaseAuthInvalidCredentialsException) {
//                } else if (e is FirebaseTooManyRequestsException) {
//                }
//            }
//
//            override fun onCodeSent(
//                    verificationId: String?,
//                    token: PhoneAuthProvider.ForceResendingToken
//            ) {
//                Log.d(TAG, "onCodeSent:" + verificationId!!)
//                storedVerificationId = verificationId
//                resendToken = token
//
//            }
//        }
//
//
//        val phoneNumber = number_text_view.text.toString()
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNumber,      // Phone number to verify
//                60,               // Timeout duration
//                TimeUnit.SECONDS, // Unit of timeout
//                this,             // Activity (for callback binding)
//                callbacks) // OnVerificationStateChangedCallbacks
//
//
//    }

}
