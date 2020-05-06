package com.example.take_a_note_room.userscreen.noteslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.take_a_note_room.R
import com.example.take_a_note_room.database.NoteClass
import com.example.take_a_note_room.userscreen.utils.OnNoteSelectedListener

class NotesAdapter(val mOnNoteSelectedListener: OnNoteSelectedListener) :
    RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    var allNotes = mutableListOf<NoteClass>()

    inner class NoteViewHolder(noteView: View) : RecyclerView.ViewHolder(noteView),
        View.OnClickListener {
        var note: NoteClass? = null
        val noteLayout: View = noteView.findViewById(R.id.note)
        val title: TextView = noteLayout.findViewById(R.id.title)
        val content: TextView = noteLayout.findViewById(R.id.content)

        init {
            noteView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            mOnNoteSelectedListener.onNoteSelected(note!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.notes_recycler_layout,
                parent,
                false
            )
        )

    override fun getItemCount() = allNotes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val current = allNotes[position]
        holder.note = current
        holder.title.text = current.title
        holder.content.text = current.content
        holder.noteLayout.setBackgroundColor(
            getColor(
                holder.noteLayout.context,
                current.color
            )
        )
    }

    override fun onBindViewHolder(
        holder: NoteViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val bundle = payloads[0] as Bundle
            for (key in bundle.keySet()) {
                when (key) {
                    "title" -> {
                        holder.note?.title = bundle.getString(key)!!
                        holder.title.text = bundle.getString(key)!!
                    }
                    "content" -> {
                        holder.note?.content = bundle.getString(key)!!
                        holder.content.text = bundle.getString(key)!!
                    }
                    "color" -> {
                        holder.note?.color = bundle.getInt(key)
                        holder.noteLayout.setBackgroundColor(
                            getColor(
                                holder.noteLayout.context,
                                bundle.getInt(key)
                            )
                        )
                    }
                }

            }
        }
    }

    internal fun setNotes(note: List<NoteClass>) {
        val result = DiffUtil.calculateDiff(
            NotesListDiffUtilCallback(
                this.allNotes,
                note
            )
        )
        result.dispatchUpdatesTo(this)
        allNotes.clear()
        allNotes.addAll(note)
    }

    class NotesListDiffUtilCallback(
        private var oldItems: List<NoteClass>,
        private var newItems: List<NoteClass>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition].id == newItems[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition] == newItems[newItemPosition]

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val oldNote = oldItems[oldItemPosition]
            val newNote = newItems[newItemPosition]

            val diffBundle = Bundle()
            if (oldNote.title != newNote.title) {
                diffBundle.putString("title", newNote.title)
            }
            if (oldNote.content != newNote.content) {
                diffBundle.putString("content", newNote.content)
            }
            if (oldNote.color != newNote.color) {
                diffBundle.putInt("color", newNote.color)
            }

            return if (diffBundle.size() == 0) null else diffBundle

        }

    }
}

