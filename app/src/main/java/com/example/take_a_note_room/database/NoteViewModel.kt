package com.example.take_a_note_room.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.take_a_note_room.NoteClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(app: Application) : AndroidViewModel(app) {

    private val repository: NoteRepository

    val allNotes: LiveData<List<NoteClass>>

    init {
        val notesDao = TakeANote.getDatabase(app, viewModelScope).noteDao()
        repository = NoteRepository(notesDao)
        allNotes = repository.allNote

    }

    fun insert(note: NoteClass) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(note)
    }

    fun update(note: NoteClass) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(note)
    }

    fun delete(note: NoteClass) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(note)
    }
}