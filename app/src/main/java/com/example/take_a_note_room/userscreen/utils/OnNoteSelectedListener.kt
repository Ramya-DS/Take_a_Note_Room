package com.example.take_a_note_room.userscreen.utils

import com.example.take_a_note_room.database.NoteClass

interface OnNoteSelectedListener {
    fun onNoteSelected(note: NoteClass)
}