package com.example.take_a_note_room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.take_a_note_room.database.NoteRepository
import com.example.take_a_note_room.database.TakeANote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SingleNoteViewModel(app: Application) : AndroidViewModel(app) {

    private val repository: NoteRepository

    init {
        val notesDao = TakeANote.getDatabase(app, viewModelScope).noteDao()
        repository = NoteRepository(notesDao)
    }

    fun insert(note: NoteClass) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(note)
    }

    fun update(note: NoteClass) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(note)
    }
}