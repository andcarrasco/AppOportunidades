package com.example.oportunidades.model.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactoDao {

    @Insert
    suspend fun insertar(contacto: ContactoEntity): Long

    @Update
    suspend fun modificar(contacto: ContactoEntity)

    @Query("SELECT * FROM contactos ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<ContactoEntity>>
}
