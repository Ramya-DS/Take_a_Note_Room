package com.example.take_a_note_room.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.take_a_note_room.NoteClass

@Dao
interface NoteDao {

    @Query("SELECT * from ALlNotes")
    fun getAllNotes(): LiveData<List<NoteClass>>

    @Query("SELECT * from ALlNotes where userName = :userId")
    fun getUserNotes(userId: String): LiveData<List<NoteClass>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteClass)

    @Update
    suspend fun update(note: NoteClass)

    @Delete
    suspend fun delete(note: NoteClass)

    @Query(value = "SELECT * from AllNotes where (title LIKE :search OR content LIKE :search) AND (userName=:userName)")
    fun search(search: String, userName: String): LiveData<List<NoteClass>>
}

