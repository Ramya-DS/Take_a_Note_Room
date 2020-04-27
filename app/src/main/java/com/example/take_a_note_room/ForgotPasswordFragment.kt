package com.example.take_a_note_room


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.example.take_a_note_room.login.model.LoginEntity
import com.example.take_a_note_room.login.viewModel.LoginViewModel
import com.google.android.material.textfield.TextInputLayout

/**
 * A simple [Fragment] subclass.
 */
class ForgotPasswordFragment : Fragment() {

    companion object {
        fun newInstance(userId: String): ForgotPasswordFragment {
            val fragment = ForgotPasswordFragment()
            val bundle = Bundle()
            bundle.putString("userId", userId)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var userId: String
    var enable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getString("userId")!!
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

        val submit: Button = rootView.findViewById(R.id.submit)


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
                viewModel.update(LoginEntity(userId, passwordText.text.toString()))
                fragmentManager?.popBackStack()
            }
        }

        return rootView
    }


}
