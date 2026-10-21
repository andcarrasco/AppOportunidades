package com.example.oportunidades
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.oportunidades.model.data.local.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersistenciaTest {
    @Test fun variosRegistrosYModificacionSobrevivenAlReabrirLaBase() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val nombre = "prueba_persistencia.db"
        context.deleteDatabase(nombre)
        var db = Room.databaseBuilder(context, AppDatabase::class.java, nombre).build()
        try {
            val dao = db.organizacionDao()
            val id = dao.insertar(OrganizacionEntity(nombre = "Empresa Demo", tipo = "Empresa", comuna = "Demo", region = "Demo"))
            dao.insertar(OrganizacionEntity(nombre = "Institución Demo"))
            dao.modificar(id, "Empresa editada", "Empresa", "Servicios", "Comuna Demo", "Región Demo", "", "Trabajadores", "Prueba")
            assertEquals(2, dao.obtenerTodos().first().size)
            db.close()
            db = Room.databaseBuilder(context, AppDatabase::class.java, nombre).build()
            assertEquals(2, db.organizacionDao().obtenerTodos().first().size)
            assertEquals("Empresa editada", db.organizacionDao().obtener(id)?.nombre)
        } finally { db.close(); context.deleteDatabase(nombre) }
    }
}
