package com.example.note.repository

import com.example.note.localData.LocalDataSourceI
import com.example.note.noteModels.NoteModel
import javax.inject.Inject

class Repository @Inject constructor(private val localDataSource: LocalDataSourceI) : RepositoryI {

    override suspend fun getData(): ArrayList<NoteModel> =
        localDataSource.getAllDatabaseData()

    override suspend fun insertData(noteModel: NoteModel) =
        localDataSource.insertInDatabase(noteModel)

    override suspend fun deleteData(list: ArrayList<NoteModel>) =
        localDataSource.deleteAllFromDatabase(list)

    override suspend fun deleteData(noteModel: NoteModel) =
        localDataSource.deleteOneUserFromDatabase(noteModel)

    override suspend fun updateData(noteModel: NoteModel) =
        localDataSource.updateUserInDatabase(noteModel)

}