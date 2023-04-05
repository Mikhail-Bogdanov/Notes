package com.example.note.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.note.feedback.classes.Event
import com.example.note.noteModels.NoteModel
import com.example.note.repository.RepositoryI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlin.collections.ArrayList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: RepositoryI) : ViewModel() {

    val stateFlowLoader: MutableStateFlow<Boolean?> = MutableStateFlow(null)

    val stateFlowNotes: MutableStateFlow<ArrayList<NoteModel>?> = MutableStateFlow(null)

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    /**
     * получение данных всей базы
     */
    fun updateSFNotes(){
        enableLoader()
        var data: ArrayList<NoteModel>? = null
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                data = repository.getData()
            }.onSuccess {
                disableLoader()
                stateFlowNotes.value = data
            }.onFailure {
                disableLoader()
                failureFeedback()
            }.recover {
                recoverFeedback()
            }
        }
    }

    fun clearDatabase(list: ArrayList<NoteModel>){
        enableLoader()
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                repository.deleteData(list)
            }.onSuccess {
                disableLoader()
                stateFlowNotes.value = ArrayList()
                successfulFeedback()
            }.onFailure {
                disableLoader()
                failureFeedback()
            }.recover {
                recoverFeedback()
            }
        }
    }

    fun deleteNote(noteModel: NoteModel){
        enableLoader()
        viewModelScope.launch(Dispatchers.IO){
            runCatching {
                repository.deleteData(noteModel)
            }.onSuccess {
                var data: ArrayList<NoteModel>? = null
                runCatching {
                    data = repository.getData()
                }.onSuccess {
                    disableLoader()
                    stateFlowNotes.value = data
                    successfulFeedback()
                }.onFailure {
                    disableLoader()
                    failureFeedback()
                }.recover {
                    recoverFeedback()
                }
            }.onFailure {
                disableLoader()
                failureFeedback()
            }.recover {
                recoverFeedback()
            }
        }
    }

    fun updateNote(noteModel: NoteModel){
        enableLoader()
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                repository.updateData(noteModel)
            }.onSuccess {
                disableLoader()
                successfulFeedback()
            }.onFailure {
                disableLoader()
                failureFeedback()
            }.recover {
                recoverFeedback()
            }
        }
    }

    fun insertNote(noteModel: NoteModel){
        enableLoader()
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                repository.insertData(noteModel)
            }.onSuccess {
                var data: ArrayList<NoteModel>? = null
                runCatching{
                    data = repository.getData()
                }.onSuccess {
                    disableLoader()
                    stateFlowNotes.value = data
                    successfulFeedback()
                }.onFailure {
                    disableLoader()
                    failureFeedback()
                }.recover {
                    recoverFeedback()
                }
            }.onFailure {
                disableLoader()
                failureFeedback()
            }.recover {
                recoverFeedback()
            }
        }
    }

    private fun enableLoader(){
        stateFlowLoader.value = true
    }

    private fun disableLoader(){
        stateFlowLoader.value = false
    }

    private suspend fun successfulFeedback(){
        viewModelScope.launch {
            eventChannel.send(Event.ShowSnackBar("Successful"))
        }
    }

    private suspend fun failureFeedback(){
        viewModelScope.launch {
            eventChannel.send(Event.ShowSnackBar("Failure"))
        }
    }

    private suspend fun recoverFeedback(){
        viewModelScope.launch {
            eventChannel.send(Event.ShowSnackBar("Recovering..."))
        }
    }
}