package com.example.take_a_note_room.userscreen.noteslist


import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.take_a_note_room.R
import com.example.take_a_note_room.database.NoteClass
import com.example.take_a_note_room.database.NoteViewModel
import com.example.take_a_note_room.userscreen.note.NoteActivity
import com.example.take_a_note_room.userscreen.utils.OnNoteSelectedListener
import com.example.take_a_note_room.userscreen.utils.OnRecyclerViewScrollListener

class NotesRecyclerFragment : Fragment(),
    OnNoteSelectedListener {

    companion object {
        fun newInstance(search: Boolean, userId: Int): NotesRecyclerFragment {
            val fragment =
                NotesRecyclerFragment()
            val bundle = Bundle()
            bundle.putBoolean("search", search)
            bundle.putInt("userId", userId)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var notesRecyclerView: RecyclerView
    private var search = false
    private var setAnimations = true
    lateinit var adapter: NotesAdapter
    var userId: Int = -1
    private var mOnRecyclerViewScrollListener: OnRecyclerViewScrollListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        savedInstanceState?.let {
            setAnimations = it.getBoolean("animation", false)
            userId = it.getInt("userId")
        }

        val rootView = inflater.inflate(R.layout.fragment_notes_recycler, container, false)
        notesRecyclerView = rootView.findViewById(R.id.notes_recycler)
        notesRecyclerView.layoutManager = LinearLayoutManager(context)
        if (!search && setAnimations) {
            notesRecyclerView.layoutAnimation =
                AnimationUtils.loadLayoutAnimation(
                    context?.applicationContext,
                    R.anim.recycler_dropdown
                )
            setAnimations = false
        }

        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(notesRecyclerView)

        notesRecyclerView.setHasFixedSize(true)
        adapter =
            NotesAdapter(this)
        notesRecyclerView.adapter = adapter

        noteViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)
        ).get(NoteViewModel::class.java)


        if (!search) {
            noteViewModel.getUserNotes(userId).observe(viewLifecycleOwner, Observer { notes ->
                adapter.setNotes(notes)
            })
        }

        notesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    mOnRecyclerViewScrollListener?.onRecyclerViewScrolled(true)
                } else if (dy < 0) {
                    mOnRecyclerViewScrollListener?.onRecyclerViewScrolled(false)
                }
            }
        })
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
                    noteViewModel.delete(it)
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
                val background = ColorDrawable(getColor(context!!,
                    R.color.colorPrimaryLight
                ))
                background.setBounds(
                    viewHolder.itemView.left,
                    viewHolder.itemView.top + 30,
                    viewHolder.itemView.left + dX.toInt(),
                    viewHolder.itemView.bottom - 30
                )
                background.draw(c)
                val iconSize: Int
                val icon = ContextCompat.getDrawable(context!!,
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        search = arguments?.getBoolean("search") ?: false
        userId = arguments?.getInt("userId")!!
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("animation", setAnimations)
        outState.putInt("userId", userId)
    }

    fun setListener(listener: OnRecyclerViewScrollListener?) {
        mOnRecyclerViewScrollListener = listener
    }

}