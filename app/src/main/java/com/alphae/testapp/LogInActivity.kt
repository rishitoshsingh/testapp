package com.alphae.testapp

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit


class LogInActivity : AppCompatActivity() {

    private var mTextViews: ArrayList<TextView> = ArrayList()

    private var mVerificationId: String? = null
    private var mAuth: FirebaseAuth? = null
    private var otp: String = ""

    private lateinit var mPhoneNumber: String
    private lateinit var mUploadedUrl: String
    private val TAG = "SignLogInActivity"
    private var numpadState: Boolean = false
    private var mBottomSheetBehaviour: BottomSheetBehavior<*>? = null

    private val mDatabase = FirebaseDatabase.getInstance()
    private var oldUser = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mPhoneNumber = intent.getStringExtra("phoneNumber")
        mUploadedUrl = intent.getStringExtra("uploadedUrl")
        mAuth = FirebaseAuth.getInstance()
        initializeView()

        val visitorDatabaseRef = mDatabase.getReference("visitors")
        visitorDatabaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                Log.d(TAG, "Got Data")
                for (child in p0.children) {
                    val user = child.getValue(User::class.java)
                    if (user?.mPhoneNumber.equals(mPhoneNumber)) {
                        Log.d(TAG, "Found Old User")
                        oldUser = true
                        user?.updateVisit()
                        child.ref.setValue(user)
                        updateUiforUser(user)
                        //Do Something
                    }
                }
                if (!oldUser) {
                    Log.d(TAG, "Old User not found")
                    sendVerificationCode(mPhoneNumber)
                }
            }
        })

        listnersInitializations()

    }

    private fun updateUiforUser(user: User?) {
        toggleScreen()
        user_phone_number.text = user?.mPhoneNumber.toString()
        user_visit_count.text = "Visits : " + user?.mVisitCount.toString()
        val uri = Uri.parse(user?.mUploadedUrl)
        user_image.setImageURI(user?.mUploadedUrl)
//        Glide.with(this)
//                .load(uri)
//                .into(user_image)
    }

    private fun toggleScreen() {
        enter_otp_text_box.visibility = View.GONE
        otp_views.visibility = View.GONE
        submit_button.visibility = View.GONE
        parent_card.visibility = View.VISIBLE
        welcome_text.visibility = View.VISIBLE
        login_fab.hide()
    }

    private fun initializeView() {
        mTextViews.apply {
            add(first)
            add(second)
            add(third)
            add(fourth)
            add(fifth)
            add(sixth)
        }
        placeholder_edittext.showSoftInputOnFocus = false

        val nestedScrollView = findViewById<View>(R.id.numpad_bottom_sheet_login)
        mBottomSheetBehaviour = BottomSheetBehavior.from(nestedScrollView)
        placeholder_edittext.requestFocus()
    }

    private fun sendVerificationCode(mobile: String) {

        val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                //Getting the code sent by SMS
                val code = phoneAuthCredential.smsCode
                if (code != null) {
                    Log.d(TAG, "VerificationColplete, Auto read Code")
                    var i = 0
                    for (n in code) enterOtp(i++, n.toString())
                    verifyVerificationCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                val visitorDatabaseRef = mDatabase.getReference("suspicious_users")
                val uniqueKey = visitorDatabaseRef.push().key
                val user = User(mPhoneNumber, mUploadedUrl, 1)
                visitorDatabaseRef.child(uniqueKey!!).setValue(user)
                Snackbar.make(login_root, "Verification Failed", Snackbar.LENGTH_LONG).show()
//                Toast.makeText(baseContext, e.message, Toast.LENGTH_LONG).show()
                Log.d(TAG, "onVerificationFailed", e)
            }

            override fun onCodeSent(s: String?, forceResendingToken: PhoneAuthProvider.ForceResendingToken?) {
                Log.d(TAG, "Code Sent")
                submit_button.isEnabled = true
                Log.d(TAG, "mVerificationId = $s")
                mVerificationId = s
                super.onCodeSent(s, forceResendingToken)
                //storing the verification id that is sent to the user
            }
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91$mobile",
                30,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks)
    }

    private fun verifyVerificationCode(code: String) {
        Log.d(TAG, "verifyVerificationCode Ver = $mVerificationId otp = $code")
        val credential = PhoneAuthProvider.getCredential(mVerificationId!!, code)
        if (credential != null) {
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.d(TAG, "SignInWithPhoneAuthCredential")
        mAuth?.signInWithCredential(credential)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val visitorDatabaseRef = mDatabase.getReference("visitors")
                        val uniqueKey = visitorDatabaseRef.push().key
                        val user = User(mPhoneNumber, mUploadedUrl, 1)
                        visitorDatabaseRef.child(uniqueKey!!).setValue(user)
                        updateUiforUser(user)
                        Log.d(TAG, "SignIn Successful")
                        Snackbar.make(login_root, "Verified", Snackbar.LENGTH_SHORT).show()
                    } else {
                        val visitorDatabaseRef = mDatabase.getReference("suspicious_users")
                        val uniqueKey = visitorDatabaseRef.push().key
                        val user = User(mPhoneNumber, mUploadedUrl, 1)
                        visitorDatabaseRef.child(uniqueKey!!).setValue(user)

                        var message = "Somthing is wrong, we will fix it soon..."
                        if (it.exception is FirebaseAuthInvalidCredentialsException) {
                            message = "Invalid code entered..."
                        }
                        Log.d(TAG, "signIn failed $message")
                    }

                }
    }


    private fun listnersInitializations() {
        login_fab.setOnClickListener {
            if (numpadState) {
                numpadState = false
                mBottomSheetBehaviour?.state = BottomSheetBehavior.STATE_COLLAPSED
                login_fab.setImageDrawable(getDrawable(R.drawable.ic_arrow_up))
            } else {
                numpadState = true
                mBottomSheetBehaviour?.state = BottomSheetBehavior.STATE_EXPANDED
                login_fab.setImageDrawable(getDrawable(R.drawable.ic_arrow_down))
            }
        }


        submit_button.setOnClickListener {
            otp = placeholder_edittext.text.toString().trim()
            if (otp.isEmpty() || otp.length < 6) {
                placeholder_edittext.requestFocus()
            }
            if (otp.length == 6)
                verifyVerificationCode(otp)
        }

        placeholder_edittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val number = s.toString()
                var i = 0
                for (n in number)
                    enterOtp(i++, n.toString())
                if (i < 6)
                    while (i < 6)
                        enterOtp(i++, "")

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }

    fun numberClicked(view: View) {
        val textView = view as TextView
        placeholder_edittext.text.append(textView.text)
    }

    fun clearClicked(view: View) {
        if (placeholder_edittext.text.isNotEmpty()) {
            val length = placeholder_edittext.text.length
            placeholder_edittext.text.delete(length - 1, length)
        }
    }


    private fun enterOtp(position: Int, number: String) {
        mTextViews[position].text = number
    }

    private fun deleteOtp(position: Int, number: String) {
        mTextViews[position].text = number
    }

    override fun onBackPressed() {
        if (numpadState) {
            placeholder_edittext.isFocusable = false
            placeholder_edittext.isFocusableInTouchMode = true

            numpadState = false
            mBottomSheetBehaviour?.state = BottomSheetBehavior.STATE_COLLAPSED
            login_fab.setImageDrawable(getDrawable(R.drawable.ic_arrow_up))
        } else super.onBackPressed()
    }

}
