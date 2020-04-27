package com.example.take_a_note_room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AllNotes")
data class NoteClass(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, var title: String,
    var content: String?,
    var color: Int, var userName: String
) {
    override fun equals(other: Any?): Boolean {

        if (other is NoteClass) {
            if (id == other.id)
                if (this.title == other.title) {
                    if (this.content.equals(other.content)) {
                        if (this.color == other.color)
                            return true
                    }
                }
        }
        return false
    }

    override fun toString(): String {
        return "$id $title $userName"
    }
}