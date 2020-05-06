package com.example.take_a_note_room.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(app: Application) : AndroidViewModel(app) {

    private val repository: NoteRepository

    init {
        val notesDao = TakeANote.getDatabase(app, viewModelScope).noteDao()
        repository = NoteRepository(notesDao)
    }

    fun delete(note: NoteClass) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(note)
    }

    fun getUserNotes(userId: Int): LiveData<List<NoteClass>> {
        return repository.getUserNotes(userId)
    }

}