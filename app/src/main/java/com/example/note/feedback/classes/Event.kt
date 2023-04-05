package com.example.note.feedback.classes

sealed class Event {
    data class ShowSnackBar(val text: String): Event()
    data class ShowToast(val text: String): Event()
}