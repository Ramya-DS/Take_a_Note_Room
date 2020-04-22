package com.example.take_a_note_room

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.ref.WeakReference

class NoteActivity : AppCompatActivity(), OnColorSelectedListener {

    private lateinit var currentNote: NoteClass
    private lateinit var viewModel: SingleNoteViewModel
    private var add: Boolean = false
    private lateinit var childFragment: WeakReference<ColorPickerFragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        savedInstanceState?.let {
            currentNote = NoteClass(
                it.getInt("id"),
                it.getString("title")!!,
                it.getString("content"),
                it.getInt("color")
            )
        }

        getCurrentNote()
        initialiseViewModel()

        val title: EditText = findViewById(R.id.title)
        val content: EditText = findViewById(R.id.content)

        currentNote.title.let { title.text.insert(title.selectionStart, it) }
        currentNote.content?.let { content.text.insert(content.selectionStart, it) }
        changeBackgroundColor(currentNote.color)

        val noteBottomBar = findViewById<BottomNavigationView>(R.id.note_bottomNavigationView)

        noteBottomBar.setOnNavigationItemSelectedListener {
            when (it.itemId) {

                R.id.save -> {
                    currentNote.title = title.text.toString()
                    currentNote.content = content.text.toString()
                    if (add) {
                        viewModel.insert(currentNote)
                        finish()
                    } else {
                        viewModel.update(currentNote)
                        finish()
                    }
                    fragmentManager!!.popBackStack()
                    true
                }
                R.id.color -> {
                    childFragment = WeakReference(ColorPickerFragment.newInstance())
                    childFragment.get()!!.show(supportFragmentManager, "COLORPICKER")
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun getCurrentNote() {
        intent?.let {
            add = it.getBooleanExtra("add", false)
            currentNote = NoteClass(
                it.getIntExtra("id", 0),
                it.getStringExtra("title")!!,
                it.getStringExtra("content"),
                it.getIntExtra("color", BackgroundColor.random())
            )
        }

    }

    private fun changeBackgroundColor(newColor: Int) {
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, newColor))
    }

    private fun initialiseViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(SingleNoteViewModel::class.java)
    }

    override fun onColorSelected(color: Int) {
        childFragment.get()!!.dismiss()
        currentNote.color = color
        changeBackgroundColor(color)

    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        (childFragment as ColorPickerFragment).setListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.let {
            it.putInt("id", currentNote.id)
            it.putString("title", currentNote.title)
            it.putString("content", currentNote.content)
            it.putInt("color", currentNote.color)
        }
    }
}
