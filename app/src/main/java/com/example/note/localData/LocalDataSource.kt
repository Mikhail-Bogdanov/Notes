package com.example.note.localData

import com.example.note.noteModels.AppDatabase
import com.example.note.noteModels.NoteModel
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val appDatabase: AppDatabase) : LocalDataSourceI {

    override suspend fun getAllDatabaseData(): ArrayList<NoteModel> =
        ArrayList(appDatabase.noteDao().getAll())

    override suspend fun insertInDatabase(noteModel: NoteModel) =
        appDatabase.noteDao().insert(noteModel)

    override suspend fun updateUserInDatabase(noteModel: NoteModel) =
        appDatabase.noteDao().update(noteModel)

    override suspend fun deleteAllFromDatabase(list: ArrayList<NoteModel>) {
        for(i in list.indices){
            appDatabase.noteDao().delete(list[i])
        }
    }

    override suspend fun deleteOneUserFromDatabase(noteModel: NoteModel) =
        appDatabase.noteDao().delete(noteModel)
}