package com.example.take_a_note_room.userscreen.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.take_a_note_room.database.NoteRepository
import com.example.take_a_note_room.database.TakeANote
import com.example.take_a_note_room.database.NoteClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository

    init {
        val notesDao = TakeANote.getDatabase(application, viewModelScope).noteDao()
        repository = NoteRepository(notesDao)
    }

    fun search(search: String, userName: Int): List<NoteClass> {
        return repository.search(search, userName)
    }

    fun delete(note: NoteClass) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(note)
    }
}