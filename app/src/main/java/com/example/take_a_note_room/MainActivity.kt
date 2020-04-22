package com.example.take_a_note_room

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var addNote: FloatingActionButton
    private lateinit var searchViewModel: SearchViewModel
    var searchFragment: NotesRecyclerFragment? = null
    lateinit var queryString: String
    private lateinit var searchView: SearchView
    private lateinit var toolBar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(SearchViewModel::class.java)

        toolBar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolBar)

        addNote = findViewById(R.id.add)

        val recyclerFragment = NotesRecyclerFragment.newInstance(false)
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_fragment, recyclerFragment)
            .commit()
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        val searchItem = menu?.findItem(R.id.search_notes)
        searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView.queryHint = "Search here..."

        searchView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
                addNote.show()
                supportFragmentManager.popBackStack()
            }

            override fun onViewAttachedToWindow(v: View?) {
                addNote.hide()
                searchFragment = NotesRecyclerFragment.newInstance(true)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_fragment, searchFragment!!)
                    .addToBackStack("Search").commit()
            }

        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    queryString = it
                    searchDb()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    queryString = it
                    searchDb()
                }
                return false
            }
        })
        return true
    }

    private fun searchDb() {
        val query = "%$queryString%"
        searchViewModel.search(query).observe(this, Observer {
            if (it != null) {
                ((searchFragment as NotesRecyclerFragment).notesRecyclerView.adapter as NotesAdapter).setNotes(
                    it
                )
            } else {
                Toast.makeText(applicationContext, "NOTHING", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onBackPressed() {
        if (searchFragment != null)
            supportFragmentManager.popBackStack()
        super.onBackPressed()
    }
}
