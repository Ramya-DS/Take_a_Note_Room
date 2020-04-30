package com.example.take_a_note_room.database

import androidx.lifecycle.LiveData
import com.example.take_a_note_room.NoteClass

class NoteRepository(private val noteDao: NoteDao) {

    val allNote: LiveData<List<NoteClass>> = noteDao.getAllNotes()

    fun getUserNotes(userId: String): LiveData<List<NoteClass>> {
        return noteDao.getUserNotes(userId)
    }

    suspend fun insert(note: NoteClass) {
        noteDao.insert(note)
    }

    suspend fun update(note: NoteClass) {
        noteDao.update(note)
    }

    suspend fun delete(note: NoteClass) {
        noteDao.delete(note)
    }

    fun search(search: String, userName: String): List<NoteClass> {
        return noteDao.search(search, userName)
    }

}