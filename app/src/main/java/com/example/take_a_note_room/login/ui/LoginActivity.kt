package com.example.take_a_note_room.login.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.take_a_note_room.userscreen.UserScreenActivity
import com.example.take_a_note_room.R

class LoginActivity : AppCompatActivity(), OnSuccessListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val logInFragment = supportFragmentManager.findFragmentByTag("LOG IN")
        if (logInFragment == null)
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
                .replace(R.id.container_login, SignInFragment.newInstance(), "LOG IN")
                .commit()
        else
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
                .replace(R.id.container_login, logInFragment as SignInFragment, "LOG IN")
                .commit()

        restoreFragments()
    }

    private fun updateSharedPreferences(userId: Int) {
        val sharedPreferences =
            getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            this.putInt("userId", userId)
            this.apply()
            Log.i("User logged in", "UserID: $userId")
        }
    }

    private fun navigateToUserAccount(userId: Int) {
        val intent = Intent(this, UserScreenActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
        finish()
    }

    override fun onSuccess(userId: Int) {
        updateSharedPreferences(userId)
        navigateToUserAccount(userId)
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        if (fragment is SignInFragment)
            fragment.mOnSuccessListener = this
        else if (fragment is SignUpFragment)
            fragment.mOnSuccessListener = this
    }

    private fun restoreFragments() {
        val fragments = supportFragmentManager.fragments

        for (f in fragments) {
            f?.let {
                if (f is SignInFragment) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_login, f)
                        .commit()
                } else
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_login, f)
                        .addToBackStack(null)
                        .commit()
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }
}
