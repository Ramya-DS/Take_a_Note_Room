package com.example.take_a_note_room.login.ui


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

//        viewModel.allAccounts.observe(this.viewLifecycleOwner, Observer {
//            it.forEach { acc ->
//                Log.d(acc.toString(), " ")
//            }
//        })

        var enable = false

        userNameTextLayout = rootView.findViewById(R.id.mail_wrapper)
        val userNameText: EditText = rootView.findViewById(R.id.username_text)

        passwordTextLayout = rootView.findViewById(R.id.password_wrapper)
        val passwordText: EditText = rootView.findViewById(R.id.password_text)

        val loginButton: Button =
            rootView.findViewById(R.id.login_button)

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
                            passwordTextLayout.error="Wrong Password"
                        }
                } else {
                    activity!!.runOnUiThread {
                        userNameTextLayout.error="Username does not exists"
                    }
                }

            }
        }
    }
}


