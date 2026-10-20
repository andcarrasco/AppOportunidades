package com.example.oportunidades.data
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
private val Context.dataStore by preferencesDataStore(name = "preferencias_usuario")
class EstadoDataStore(private val context: Context) {
    private val avisos = booleanPreferencesKey("avisos_activados")
    fun obtenerEstado() = context.dataStore.data.map { it[avisos] ?: true }
    suspend fun guardarEstado(valor: Boolean) { context.dataStore.edit { it[avisos] = valor } }
}
