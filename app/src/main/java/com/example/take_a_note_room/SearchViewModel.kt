package com.example.take_a_note_room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.take_a_note_room.database.NoteRepository
import com.example.take_a_note_room.database.TakeANote

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository

    init {
        val notesDao = TakeANote.getDatabase(application, viewModelScope).noteDao()
        repository = NoteRepository(notesDao)
    }

    fun search(search: String): LiveData<List<NoteClass>> {
        return repository.search(search)
    }
}