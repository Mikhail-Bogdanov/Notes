package com.example.note.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.note.noteModels.NoteModel
import com.example.note.repository.RepositoryI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(private val repository: RepositoryI): ViewModel(){

    fun insertNote(noteModel: NoteModel){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(noteModel)
        }
    }


}