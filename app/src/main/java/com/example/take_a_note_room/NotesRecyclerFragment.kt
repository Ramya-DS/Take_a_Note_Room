package com.example.take_a_note_room


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.take_a_note_room.database.NoteViewModel
import java.lang.ref.WeakReference


class NotesRecyclerFragment : Fragment(), OnNoteSelectedListener {

    companion object {
        fun newInstance(): NotesRecyclerFragment {
            val fragment = NotesRecyclerFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var noteViewModel: NoteViewModel
    lateinit var rootViewRef: WeakReference<View>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_notes_recycler, container, false)
        rootViewRef = WeakReference(rootView)
        val notesRecyclerView: RecyclerView = rootView.findViewById(R.id.notes_recycler)
        notesRecyclerView.layoutManager = LinearLayoutManager(context)
        notesRecyclerView.layoutAnimation =
            AnimationUtils.loadLayoutAnimation(
                context?.applicationContext,
                R.anim.recycler_dropdown
            )

        notesRecyclerView.setHasFixedSize(false)
        val adapter = NotesAdapter(context!!, this)
        notesRecyclerView.adapter = adapter

        noteViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)
        ).get(NoteViewModel::class.java)

        Log.d("viewmodel", "$noteViewModel ")

        noteViewModel.allNotes.observe(viewLifecycleOwner, Observer { note ->
            Log.d("inside observer", " ")
            note?.let {
                Log.d("setNotes", " ")
                adapter.setNotes(it)
                notesRecyclerView.scheduleLayoutAnimation()
            }

        })
        return rootView

    }

    override fun onNoteSelected(note: NoteClass) {
        val fragment = NoteFragment.newInstance(note, false)
        fragment.setTargetFragment(this, 2)
        fragmentManager!!.beginTransaction()
            .setCustomAnimations(R.anim.fragment_open_enter, R.anim.exit_fade)
            .replace(R.id.content_fragment, fragment)
            .addToBackStack("EDIT").commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var note: NoteClass


        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            data?.let {
                note = NoteClass(
                    title = it.getStringExtra("title")!!,
                    content = it.getStringExtra("content"),
                    color = it.getIntExtra("color", BackgroundColor.random())
                )
                noteViewModel.insert(note)
            }

        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            data?.let {
                note = NoteClass(
                    id = it.getIntExtra("id", 0),
                    title = it.getStringExtra("title")!!,
                    content = it.getStringExtra("content"),
                    color = it.getIntExtra("color", BackgroundColor.random())
                )
                noteViewModel.update(note)
            }
        } else if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            data?.let {
                note = NoteClass(
                    id = it.getIntExtra("id", 0),
                    title = it.getStringExtra("title")!!,
                    content = it.getStringExtra("content"),
                    color = it.getIntExtra("color", BackgroundColor.random())
                )
                noteViewModel.delete(note)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
