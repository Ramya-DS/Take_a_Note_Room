package com.example.take_a_note_room


import android.content.Intent
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.take_a_note_room.database.NoteViewModel
import java.lang.ref.WeakReference


class NotesRecyclerFragment : Fragment(), OnNoteSelectedListener {

    companion object {
        fun newInstance(search: Boolean): NotesRecyclerFragment {
            val fragment = NotesRecyclerFragment()
            val bundle = Bundle()
            bundle.putBoolean("search", search)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var rootViewRef: WeakReference<View>
    lateinit var notesRecyclerView: RecyclerView
    var search = false
    var setAnimations = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_notes_recycler, container, false)
        rootViewRef = WeakReference(rootView)
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
        val adapter = NotesAdapter(context!!, this)
        notesRecyclerView.adapter = adapter
        (notesRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        noteViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)
        ).get(NoteViewModel::class.java)

        Log.d("viewmodel", "$noteViewModel ")

        if (!search) {
            noteViewModel.allNotes.observe(viewLifecycleOwner, Observer { note ->
                Log.d("inside observer", " ")
                note?.let {
                    Log.d("setNotes", " ")
                    adapter.setNotes(it)
                }
            })
        }

        return rootView

    }

    override fun onNoteSelected(note: NoteClass) {

        val intent = Intent(context, NoteActivity::class.java)
        intent.putExtra("id", note.id)
        intent.putExtra("title", note.title)
        intent.putExtra("content", note.content)
        intent.putExtra("color", note.color)
        startActivity(intent)
    }


    private val itemTouchHelper =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val note = (viewHolder as NotesAdapter.NoteViewHolder).note
                note?.let {
                    noteViewModel.delete(it)
                    if (!search)
                        notesRecyclerView.adapter?.notifyDataSetChanged()
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
                val background = ColorDrawable(getColor(context!!, R.color.colorPrimaryLight))
                background.setBounds(
                    viewHolder.itemView.left,
                    viewHolder.itemView.top + 30,
                    viewHolder.itemView.left + dX.toInt(),
                    viewHolder.itemView.bottom - 30
                )
                background.draw(c)
                var iconSize = 0
                val icon = ContextCompat.getDrawable(context!!, R.drawable.delete_icon)
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
    }


}
