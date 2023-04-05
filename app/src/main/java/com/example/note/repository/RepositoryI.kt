package com.example.note.repository

import com.example.note.noteModels.NoteModel

interface RepositoryI {

    suspend fun getData(): ArrayList<NoteModel>

    suspend fun insertData(noteModel: NoteModel)

    suspend fun deleteData(list: ArrayList<NoteModel>)

    suspend fun deleteData(noteModel: NoteModel)

    suspend fun updateData(noteModel: NoteModel)

}