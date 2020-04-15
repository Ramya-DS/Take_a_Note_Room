package com.example.take_a_note_room


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.ref.WeakReference

class NoteFragment : Fragment(), OnColorSelectedListener {

    companion object {
        fun newInstance(note: NoteClass, add: Boolean): NoteFragment {
            val fragment = NoteFragment()
            val bundle = Bundle()
            bundle.putBoolean("add", add)
            note.let {
                bundle.putInt("id", it.id)
                bundle.putString("title", it.title)
                bundle.putString("content", it.content)
                bundle.putInt("color", it.color)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var currentNote: NoteClass
    private lateinit var rootView: WeakReference<View>
    private lateinit var childFragment: WeakReference<ColorPickerFragment>
    private var add: Boolean = false

    var mOnNoteFragmentCloseListener: OnNoteFragmentCloseListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        retainInstance = true

        rootView = WeakReference(inflater.inflate(R.layout.fragment_note, container, false))
        val mRootView = rootView.get()!!
        val title: EditText = mRootView.findViewById(R.id.title)
        val content: EditText = mRootView.findViewById(R.id.content)


        currentNote.title.let { title.text.insert(title.selectionStart, it) }
        currentNote.content?.let { content.text.insert(content.selectionStart, it) }
        mRootView.setBackgroundColor(getColor(context!!, currentNote.color))

        val noteBottomBar =
            mRootView.findViewById<BottomNavigationView>(R.id.note_bottomNavigationView)

        noteBottomBar.setOnNavigationItemSelectedListener {
            when (it.itemId) {

                R.id.save -> {
                    currentNote.title = title.text.toString()
                    currentNote.content = content.text.toString()
                    if (add) {
                        targetFragment?.onActivityResult(
                            1,
                            Activity.RESULT_OK,
                            createAddNoteIntent()
                        )
                    } else {
                        targetFragment?.onActivityResult(
                            2,
                            Activity.RESULT_OK,
                            createEditNoteIntent()
                        )
                    }
                    fragmentManager!!.popBackStack()
                    true
                }
                R.id.color -> {
                    childFragment = WeakReference(ColorPickerFragment.newInstance())
                    childFragment.get()!!.show(childFragmentManager, "COLORPICKER")
                    true
                }
                R.id.delete -> {
                    targetFragment?.onActivityResult(3, Activity.RESULT_OK, createEditNoteIntent())
                    fragmentManager!!.popBackStack()
                    true
                }
                else -> {
                    false
                }
            }
        }
        return mRootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            add = it.getBoolean("add")
            currentNote = NoteClass(
                it.getInt("id"),
                it.getString("title")!!,
                it.getString("content"),
                it.getInt("color")
            )
        }
        savedInstanceState?.let {
            currentNote = NoteClass(
                it.getInt("id"),
                it.getString("title")!!,
                it.getString("content"),
                it.getInt("color")
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            currentNote.color = data?.getIntExtra("color", BackgroundColor.random())!!
            rootView.get()!!.setBackgroundColor(getColor(context!!, currentNote.color))
        }
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

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        (childFragment as ColorPickerFragment).setListener(this)
    }

    override fun onColorSelected(color: Int) {
        childFragment.get()!!.dismiss()
        currentNote.color = color
        rootView.get()!!.setBackgroundColor(getColor(context!!, color))
    }

    override fun onDestroy() {
        super.onDestroy()
        mOnNoteFragmentCloseListener?.onNoteFragmentClosed()
    }

    private fun createAddNoteIntent(): Intent = Intent().apply {
        this.putExtra("title", currentNote.title)
        this.putExtra("content", currentNote.content)
        this.putExtra("color", currentNote.color)
    }

    private fun createEditNoteIntent(): Intent = Intent().apply {
        this.putExtra("id", currentNote.id)
        this.putExtra("title", currentNote.title)
        this.putExtra("content", currentNote.content)
        this.putExtra("color", currentNote.color)
    }
}
