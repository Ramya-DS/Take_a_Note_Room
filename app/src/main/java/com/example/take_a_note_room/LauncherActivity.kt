package com.example.take_a_note_room

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.take_a_note_room.login.ui.LoginActivity
import com.example.take_a_note_room.userscreen.UserScreenActivity

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = getUserIdFromSharedPref()

        if (userId == -1)
            openLoginScreen()
        else
            notesListScreen(userId)
    }

    private fun getUserIdFromSharedPref(): Int {
        val sharedPreferences =
            getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE)
        return sharedPreferences.getInt("userId", -1)
    }

    private fun openLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun notesListScreen(userId: Int) {
        val intent = Intent(this, UserScreenActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }
}
