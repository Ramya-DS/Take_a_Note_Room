package com.example.take_a_note_room.login.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.take_a_note_room.MainActivity
import com.example.take_a_note_room.R


class LoginActivity : AppCompatActivity(), OnSuccessListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Log.d("in login activity", "yes!!")

        val signUpButton = findViewById<Button>(R.id.sign_up)

        signUpButton.setOnClickListener {
            Log.d("in login activity", "button signup")
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_login, SignUpFragment.newInstance())
                .setCustomAnimations(R.anim.entry_animation, R.anim.exit_animation).commit()
        }
        val signInButton: Button = findViewById(R.id.sign_in)
        signInButton.setOnClickListener {
            Log.d("in login activity", "button signin")
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container_login,
                    SigInFragment.newInstance()
                ).setCustomAnimations(R.anim.entry_animation, R.anim.exit_animation).commit()
        }

    }

    override fun onSuccess(userId: String) {
        Log.d("Success", userId)
        updateSharedPreferences(userId)
        navigateToUserAccount(userId)
        finish()
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        if (fragment is SigInFragment)
            fragment.mOnSuccessListener = this
        else if (fragment is SignUpFragment)
            fragment.mOnSuccessListener = this
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finishAffinity()
    }

    private fun updateSharedPreferences(userId: String) {
        val sharedPreferences =
            getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            this.putString("userId", userId)
            val bool = this.commit()
            Log.d("data written", "$bool")
        }
    }

    private fun navigateToUserAccount(userId: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userId", userId)
        setResult(Activity.RESULT_OK, intent)
    }
}
