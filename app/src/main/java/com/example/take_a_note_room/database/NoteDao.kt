package com.example.take_a_note_room.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.take_a_note_room.NoteClass

@Dao
interface NoteDao {

    @Query("SELECT * from ALlNotes")
    fun getAllNotes(): LiveData<List<NoteClass>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteClass)

    @Update
    suspend fun update(note: NoteClass)

    @Delete
    suspend fun delete(note: NoteClass)
}

