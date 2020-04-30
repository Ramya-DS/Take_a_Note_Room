package com.example.take_a_note_room.login.ui


import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.take_a_note_room.ForgotPasswordFragment
import com.example.take_a_note_room.R
import com.example.take_a_note_room.login.viewModel.LoginViewModel
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass.
 */
class SigInFragment : Fragment() {

    companion object {
        fun newInstance(): SigInFragment {
            val fragment = SigInFragment()
            fragment.arguments = Bundle()

            return fragment
        }
    }

    private lateinit var viewModel: LoginViewModel
    private lateinit var userNameTextLayout: TextInputLayout
    private lateinit var passwordTextLayout: TextInputLayout

    var mOnSuccessListener: OnSuccessListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.sign_in_fragment, container, false)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(activity!!.application)
        ).get(LoginViewModel::class.java)

        var enable = false

        userNameTextLayout = rootView.findViewById(R.id.username_wrapper)
        val userNameText: EditText = rootView.findViewById(R.id.username_text)

        passwordTextLayout = rootView.findViewById(R.id.password_wrapper)
        val passwordText: EditText = rootView.findViewById(R.id.password_text)

        val loginButton: Button =
            rootView.findViewById(R.id.login)

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enable = userNameText.text.isNotEmpty() && passwordText.text.isNotEmpty()
                if (enable)
                    loginButton.isEnabled = enable
            }
        }

        userNameText.addTextChangedListener(textWatcher)
        passwordText.addTextChangedListener(textWatcher)

        loginButton.setOnClickListener {
            if (enable) {
                val userNameString = userNameText.text.toString()
                val passwordString = passwordText.text.toString()
                authentication(userNameString, passwordString)
            }
        }

        val forgotPassword: TextView = rootView.findViewById(R.id.forgot_password)
        forgotPassword.setOnClickListener {
            if (userNameText.text.trim().isNotEmpty()) {
                val frag = ForgotPasswordFragment.newInstance(userNameText.text.toString())
                fragmentManager!!.beginTransaction().replace(R.id.container_login, frag)
                    .addToBackStack("FORGOT PASSWORD").commit()
            } else
                Toast.makeText(context!!, "Enter username", Toast.LENGTH_SHORT).show()

        }

        val signup = rootView.findViewById<TextView>(R.id.signup)
        val spannableString = SpannableString(signup.text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                fragmentManager!!.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_fade_enter,R.anim.fragment_fade_exit)
                    .replace(R.id.container_login, SignUpFragment(), "SIGN UP")
                    .addToBackStack(null).commit()
            }
        }

        spannableString.setSpan(
            clickableSpan,
            0,
            signup.text.length - 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        signup.text = spannableString
        signup.movementMethod = LinkMovementMethod.getInstance()

        return rootView
    }

    private fun authentication(userId: String, password: String) {
        runBlocking {
            withContext(Dispatchers.IO) {
                if (viewModel.checkForUserName(userId)) {
                    if (viewModel.authentication(userId, password))
                        activity!!.runOnUiThread {
                            Toast.makeText(context!!, "Successful Login", Toast.LENGTH_SHORT).show()
                            mOnSuccessListener?.onSuccess(userId)
                        }
                    else
                        activity!!.runOnUiThread {
                            passwordTextLayout.error = "Wrong Password"
                        }
                } else {
                    activity!!.runOnUiThread {
                        userNameTextLayout.error = "Username does not exists"
                    }
                }

            }
        }
    }

    fun setHighLightedText(tv: TextView, textToHighlight: String) {
        val tvt = tv.text.toString()
        var ofe = tvt.indexOf(textToHighlight, 0)
        val wordToSpan: Spannable = SpannableString(tv.text)
        var ofs = 0
        while (ofs < tvt.length && ofe != -1) {
            ofe = tvt.indexOf(textToHighlight, ofs)
            if (ofe == -1) break else { // set color here
                wordToSpan.setSpan(
                    BackgroundColorSpan(-0x100),
                    ofe,
                    ofe + textToHighlight.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tv.setText(wordToSpan, TextView.BufferType.SPANNABLE)
            }
            ofs = ofe + 1
        }
    }
}


