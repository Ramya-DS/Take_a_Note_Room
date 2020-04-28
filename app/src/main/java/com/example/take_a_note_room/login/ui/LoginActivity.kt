package com.example.take_a_note_room.login.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.take_a_note_room.MainActivity
import com.example.take_a_note_room.R


class LoginActivity : AppCompatActivity(), OnSuccessListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val logInFragment = supportFragmentManager.findFragmentByTag("LOG IN")
        if (logInFragment == null)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_login, SigInFragment(), "LOG IN")
                .commit()
        else
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_login, logInFragment as SigInFragment, "LOG IN")
                .commit()


        restoreFragments()
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

    override fun onSuccess(userId: String) {
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

    private fun restoreFragments() {
        val fragments = supportFragmentManager.fragments

        for (f in fragments) {
            f?.let {
                if (f is SigInFragment) {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.container_login,
                        f
                    ).commit()
                } else
                    supportFragmentManager.beginTransaction().replace(
                        R.id.container_login,
                        f
                    ).addToBackStack(null).commit()
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }
}
