package com.example.take_a_note_room

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(
    private val context: Context,
    val mOnNoteSelectedListener: OnNoteSelectedListener
) :
    RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private var allNotes = emptyList<NoteClass>()

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
            Log.i("Onclick", "${note?.id}")
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
                context,
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
            val combinedChange = createCombinedPayload(payloads as List<Change<NoteClass>>)
            val oldData = combinedChange.oldData
            val newData = combinedChange.newData

            if (newData.title != oldData.title) {
                holder.title.text = newData.title
            }

            if (newData.content != oldData.content) {
                holder.content.text = newData.content
            }

            if (newData.color != oldData.color) {
                holder.noteLayout.setBackgroundColor(getColor(context, newData.color))
            }
        }
    }

    internal fun setNotes(note: List<NoteClass>) {
        Log.d("Inside_set_notes", "${note.size}")
        val result = DiffUtil.calculateDiff(NotesListDiffUtilCallback(this.allNotes, note))
        result.dispatchUpdatesTo(this)
        this.allNotes = note
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
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]

            return Change(
                oldItem,
                newItem
            )
        }
    }

}