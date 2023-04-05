package com.example.note.noteModels

import androidx.room.*

@Entity
data class NoteModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val name: String,
    val text: String,
    val listUri: ArrayList<String>,
    val dateTime: String
) {
    constructor(name: String, text: String, listUri: ArrayList<String>, dateTime: String) :
            this(0, name, text, listUri, dateTime)
}
