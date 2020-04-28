package com.example.take_a_note_room

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.take_a_note_room.login.ui.LoginActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity(), OnRecyclerViewScrollListener {

    private lateinit var addNote: FloatingActionButton
    private lateinit var searchViewModel: SearchViewModel
    private var searchFragment: NotesRecyclerFragment? = null
    private var mSearchQuery: String? = null
    private var submit = false
    private lateinit var searchView: SearchView
    private lateinit var toolBar: Toolbar
    private var recyclerFragment: NotesRecyclerFragment? = null
    private var isExpanded: Boolean = false
    private var userId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        savedInstanceState?.let {
            mSearchQuery = it.getString("query")
            submit = it.getBoolean("submit")
            isExpanded = it.getBoolean("expanded")
            recyclerFragment =
                supportFragmentManager.findFragmentByTag("contents") as NotesRecyclerFragment
            recyclerFragment?.setListener(this)
        }

        userId = getUserNameFromSharedPref()

        if (userId == null) {
            openLoginScreen()
        } else {
            if (recyclerFragment == null)
                displayNotes(userId!!)
            else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_fragment, recyclerFragment!!).commit()
            }
        }

        searchViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(SearchViewModel::class.java)

        toolBar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolBar)

        addNote = findViewById(R.id.add)
        addNote.setOnClickListener {
            startActivity(createAddNoteIntent())
        }

    }

    private fun createAddNoteIntent(): Intent = Intent(this, NoteActivity::class.java).apply {
        val content: String? = null
        this.putExtra("add", true)
        this.putExtra("title", "No title")
        this.putExtra("content", content)
        this.putExtra("color", BackgroundColor.random())
        this.putExtra("userId", userId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                updateSharedPreferencesForNoUser()
                openLoginScreen()
                return true
            }
            R.id.account_info -> {
                Toast.makeText(this, "Curret account: $userId", Toast.LENGTH_LONG).show()
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        val searchItem = menu?.findItem(R.id.search_notes)
        searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView.maxWidth = resources.displayMetrics.widthPixels
        searchView.queryHint = "Search here..."

        if (mSearchQuery != null && isExpanded) {
            searchViewRestore(searchItem)
        }

        searchItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                searchViewSetup()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                searchViewClose()
                return true
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    submit = true
                    searchDb(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    mSearchQuery = newText

                    searchDb(it)
                }
                return false
            }
        })
        return true
    }

    private fun searchDb(query: String) {
        val queryString = "%$query%"
        mSearchQuery = query
        searchViewModel.search(queryString, userId!!).observe(this, Observer {
            if (it != null && searchFragment != null) {
                val recyclerView = (searchFragment as NotesRecyclerFragment).notesRecyclerView
                if (it.isEmpty())
                    Toast.makeText(this, "NO MATCHES", Toast.LENGTH_SHORT).show()
                else {
                    (recyclerView.adapter as NotesAdapter).setNotes(it)
                }

            }
        })
    }

    override fun onBackPressed() {
        removeSearchFragment()
        super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("query", searchView.query.toString())
        outState.putBoolean("submit", submit)
        outState.putBoolean("expanded", isExpanded)
    }

    private fun createSearchFragment() {
        searchFragment = NotesRecyclerFragment.newInstance(true, userId!!)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_close_exit)
            .replace(R.id.content_fragment, searchFragment!!)
            .addToBackStack("Search").commit()
    }

    private fun removeSearchFragment() {
        if (searchFragment != null) {
            supportFragmentManager.popBackStack()
            searchFragment = null
        }
    }

    private fun searchViewSetup() {
        isExpanded = true
        addNote.hide()
        createSearchFragment()
    }

    private fun searchViewClose() {
        isExpanded = false
        addNote.show()
        removeSearchFragment()
    }

    private fun searchViewRestore(searchItem: MenuItem?) {
        searchItem?.expandActionView()
        removeSearchFragment()
        searchViewSetup()
        searchView.setQuery(mSearchQuery, false)
        if (submit)
            searchDb(mSearchQuery!!)
        searchView.isFocusable = true

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 1) {
            userId = data?.getStringExtra("userId")
            displayNotes(userId!!)
        }
    }

    private fun displayNotes(userId: String) {
        if (recyclerFragment != null) {
            supportFragmentManager.beginTransaction().remove(recyclerFragment!!).commit()
            recyclerFragment == null
            recyclerFragment?.setListener(null)
        }
        recyclerFragment = NotesRecyclerFragment.newInstance(false, userId)
        recyclerFragment?.setListener(this)
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_fragment, recyclerFragment!!, "contents").commit()

    }

    private fun getUserNameFromSharedPref(): String? {
        val sharedPreferences =
            getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE)
        return sharedPreferences.getString("userId", null)
    }

    private fun openLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, 1)
    }

    private fun updateSharedPreferencesForNoUser() {
        val sharedPreferences =
            getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            this.putString("userId", null)
            this.commit()
        }
    }

    override fun onRecyclerViewScrolled(hide: Boolean) {
        if (hide)
            addNote.hide()
        else
            addNote.show()
    }
}
