package com.example.take_a_note_room.login.ui


import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.take_a_note_room.R
import com.example.take_a_note_room.login.model.LoginEntity
import com.example.take_a_note_room.login.viewModel.LoginViewModel
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


/**
 * A simple [Fragment] subclass.
 */
class SignUpFragment : Fragment() {

    companion object {
        fun newInstance(): SignUpFragment {
            val fragment = SignUpFragment()
            fragment.arguments = Bundle()

            return fragment
        }
    }

    private lateinit var viewModel: LoginViewModel
    var mOnSuccessListener: OnSuccessListener? = null
    private lateinit var userNameTextLayout: TextInputLayout
    private lateinit var passwordTextLayout: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.sign_up_fragment, container, false)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(activity!!.application)
        ).get(LoginViewModel::class.java)

        viewModel.allAccounts.observe(this.viewLifecycleOwner, Observer {
            it.forEach { acc ->
                Log.d(acc.toString(), " ")
            }
        })
        var enable = false

        userNameTextLayout = rootView.findViewById(R.id.username_wrapper)
        val userNameText: EditText = rootView.findViewById(R.id.username_text)

        val passwordText: EditText = rootView.findViewById(R.id.password_text)
        val createButton: Button =
            rootView.findViewById(R.id.register)

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enable =
                    userNameText.text.trim().isNotEmpty() && passwordText.text.isNotEmpty()
                if (enable)
                    createButton.isEnabled = enable
            }
        }

        userNameText.addTextChangedListener(textWatcher)
        passwordText.addTextChangedListener(textWatcher)

        createButton.setOnClickListener {
            if (enable) {
                val userNameString = userNameText.text.trim().toString()
                val passwordString = passwordText.text.trim().toString()

                checkForUserName(userNameString, passwordString)
            }
        }
        val login: TextView = rootView.findViewById(R.id.login)
        val spannableString = SpannableString(login.text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                fragmentManager?.let {
                    val frag = it.findFragmentByTag("SIGN UP")
                    it.beginTransaction()
                        .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
                        .remove(frag!!).commit()
                    it.popBackStack()
                }
            }
        }

        spannableString.setSpan(
            clickableSpan,
            0,
            login.text.length - 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        login.text = spannableString
        login.movementMethod = LinkMovementMethod.getInstance()
        return rootView
    }

    private fun checkForUserName(userId: String, password: String) {
        var bool: Boolean
        runBlocking {
            withContext(Dispatchers.IO) {
                bool = viewModel.checkForUserName(userId)
                if (bool)
                    activity!!.runOnUiThread {
                        userNameTextLayout.error = "Username already exists"
                    }
                else {
                    activity!!.runOnUiThread {
                        Toast.makeText(context!!, "Account Created", Toast.LENGTH_SHORT).show()
                        viewModel.insert(LoginEntity(userId, password))
                        mOnSuccessListener?.onSuccess(userId)
                    }
                }
            }
        }

    }
}



