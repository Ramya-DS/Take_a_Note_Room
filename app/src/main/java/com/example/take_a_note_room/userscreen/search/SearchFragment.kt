package com.example.take_a_note_room.userscreen.search

import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.take_a_note_room.R
import com.example.take_a_note_room.userscreen.note.NoteActivity
import com.example.take_a_note_room.database.NoteClass
import com.example.take_a_note_room.userscreen.noteslist.NotesAdapter
import com.example.take_a_note_room.userscreen.utils.OnNoteSelectedListener

class SearchFragment : Fragment(), OnNoteSelectedListener {

    companion object {
        fun newInstance(userId: Int): SearchFragment {
            val fragment =
                SearchFragment()
            val bundle = Bundle()
            bundle.putInt("userId", userId)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var userId: Int = -1
    lateinit var adapter: NotesAdapter
    lateinit var viewModel: SearchViewModel
    lateinit var notesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getInt("userId")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        savedInstanceState?.let {
            userId = it.getInt("userId")
        }

        val rootView = inflater.inflate(R.layout.fragment_notes_recycler, container, false)
        notesRecyclerView = rootView.findViewById(R.id.notes_recycler)
        notesRecyclerView.layoutManager = LinearLayoutManager(context)

        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(notesRecyclerView)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(activity!!.application)
        ).get(SearchViewModel::class.java)

        notesRecyclerView.setHasFixedSize(true)
        adapter =
            NotesAdapter(this)
        notesRecyclerView.adapter = adapter

        return rootView
    }

    override fun onNoteSelected(note: NoteClass) {

        val intent = Intent(context, NoteActivity::class.java)
        intent.putExtra("id", note.id)
        intent.putExtra("title", note.title)
        intent.putExtra("content", note.content)
        intent.putExtra("color", note.color)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    private val itemTouchHelper =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val note = (viewHolder as NotesAdapter.NoteViewHolder).note
                note?.let {
                    viewModel.delete(it)
                    val notes = mutableListOf<NoteClass>()
                    notes.addAll(adapter.allNotes)
                    notes.remove(it)
                    adapter.setNotes(notes)

                }
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                val background =
                    ColorDrawable(
                        ContextCompat.getColor(
                            context!!,
                            R.color.colorPrimaryLight
                        )
                    )
                background.setBounds(
                    viewHolder.itemView.left,
                    viewHolder.itemView.top + 30,
                    viewHolder.itemView.left + dX.toInt(),
                    viewHolder.itemView.bottom - 30
                )
                background.draw(c)
                var iconSize = 0
                val icon = ContextCompat.getDrawable(
                    context!!,
                    R.drawable.delete_icon
                )
                if (icon != null) {
                    iconSize = icon.intrinsicHeight
                    val halfIcon = iconSize / 2
                    val top =
                        viewHolder.itemView.top + ((viewHolder.itemView.bottom - viewHolder.itemView.top) / 2 - halfIcon)
                    icon.setBounds(
                        viewHolder.itemView.left + 150,
                        top,
                        viewHolder.itemView.left + 150 + icon.intrinsicWidth,
                        top + icon.intrinsicHeight
                    )
                    icon.draw(c)
                }
            }
        }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("userId", userId)
    }
}
