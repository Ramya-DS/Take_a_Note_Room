package com.example.take_a_note_room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AllNotes")
data class NoteClass(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, var title: String,
    var content: String?,
    var color: Int
)