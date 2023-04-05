package com.example.note.noteModels

import androidx.room.*

@Dao
interface NoteDao {
    @Query("SELECT * FROM notemodel")
    fun getAll(): List<NoteModel>

    @Insert
    fun insert(noteModel: NoteModel)

    @Update
    fun update(noteModel: NoteModel)

    @Delete
    fun delete(noteModel: NoteModel)
}