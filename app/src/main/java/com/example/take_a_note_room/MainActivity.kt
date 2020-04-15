package com.example.take_a_note_room

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), OnNoteFragmentCloseListener {

    private lateinit var addNote: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolBar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolBar)

        addNote = findViewById(R.id.add)

        val recyclerFragment = NotesRecyclerFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_fragment, recyclerFragment)
            .commit()
        addNote.setOnClickListener {
            val newNote = NoteFragment.newInstance(createNote(), true)
            newNote.setTargetFragment(recyclerFragment, 1)
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_fragment, newNote)
                .setCustomAnimations(R.anim.entry_animation, R.anim.exit_fade)
                .addToBackStack("ADD")
                .commit()
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        if (fragment is NoteFragment) {
            fragment.mOnNoteFragmentCloseListener = this
            addNote.hide()
        }
    }

    override fun onNoteFragmentClosed() {
        addNote.show()
    }

    private fun createNote(): NoteClass = NoteClass(
        title = "No title",
        content = null,
        color = BackgroundColor.random()
    )


}
