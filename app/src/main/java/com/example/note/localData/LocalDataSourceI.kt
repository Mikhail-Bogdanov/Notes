package com.example.note.localData

import com.example.note.noteModels.NoteModel

interface LocalDataSourceI {
    /**
     * получает всех пользователей из базы данных
     */
    suspend fun getAllDatabaseData(): ArrayList<NoteModel>
    /**
     * добавляет пользователя в базу данных
     */
    suspend fun insertInDatabase(noteModel: NoteModel)
    /**
     * очищает базу данных
     */
    suspend fun deleteAllFromDatabase(list: ArrayList<NoteModel>)
    /**
     * удаляет выбранного пользователя из базы данных
     */
    suspend fun deleteOneUserFromDatabase(noteModel: NoteModel)
    /**
     * обновляет информацию пользователя в базе данных
     */
    suspend fun updateUserInDatabase(noteModel: NoteModel)
}