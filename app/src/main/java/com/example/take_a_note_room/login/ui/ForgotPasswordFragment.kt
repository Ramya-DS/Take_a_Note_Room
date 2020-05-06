package com.example.take_a_note_room.login.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.example.take_a_note_room.R
import com.example.take_a_note_room.login.model.LoginEntity
import com.example.take_a_note_room.login.viewModel.LoginViewModel
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ForgotPasswordFragment : Fragment() {

    companion object {
        fun newInstance(userId: String): ForgotPasswordFragment {
            val fragment =
                ForgotPasswordFragment()
            val bundle = Bundle()
            bundle.putString("userId", userId)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var username: String
    private var enable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        username = arguments?.getString("userId")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_forgot_password, container, false)

        val viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(activity!!.application)
        ).get(LoginViewModel::class.java)

        val passwordText: EditText = rootView.findViewById(R.id.password_text)

        val passwordReenterLayout: TextInputLayout =
            rootView.findViewById(R.id.password_reenter_wrapper)
        val passwordReenter: EditText = rootView.findViewById(R.id.password_reenter_text)

        val submit: Button = rootView.findViewById(R.id.confirm)


        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enable =
                    passwordText.text.trim().isNotEmpty() && passwordReenter.text.trim().isNotEmpty()
                if (enable)
                    submit.isEnabled = enable
            }
        }

        passwordText.addTextChangedListener(textWatcher)
        passwordReenter.addTextChangedListener(textWatcher)

        submit.setOnClickListener {
            if (passwordText.text.toString() != passwordReenter.text.toString()) {
                passwordReenterLayout.error = "Password doesn't match"
            } else {
                runBlocking {
                    withContext(Dispatchers.IO) {
                        val id = viewModel.getUserId(username)
                        viewModel.update(
                            LoginEntity(
                                userId = id,
                                userName = username,
                                password = passwordText.text.toString()
                            )
                        )
                    }
                }

                Log.i("Password Changed", "Username: $username")
                fragmentManager?.popBackStack()
            }
        }

        return rootView
    }


}
