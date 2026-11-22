package com.example.oportunidades.model.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        OrganizacionEntity::class,
        ContactoEntity::class,
        OportunidadEntity::class,
        SeguimientoEntity::class,
        ResultadoEntity::class,
        ActivacionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun organizacionDao(): OrganizacionDao
    abstract fun contactoDao(): ContactoDao
    abstract fun oportunidadDao(): OportunidadDao
    abstract fun seguimientoDao(): SeguimientoDao
    abstract fun resultadoDao(): ResultadoDao
    abstract fun activacionDao(): ActivacionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "oportunidades_database"
                ).fallbackToDestructiveMigration(true)
                    .addCallback(datosIniciales)
                    .build().also { INSTANCE = it }
            }
        }

        // Organizaciones ficticias con los mismos id que usan las oportunidades del microservicio.
        // Se insertan al abrir la base, cuando las tablas ya existen; si ya están, no se repiten.
        private val datosIniciales = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                insertarOrganizaciones(db)
            }
        }

        private fun insertarOrganizaciones(db: SupportSQLiteDatabase) {
            db.execSQL(
                "INSERT OR IGNORE INTO organizaciones " +
                        "(id, nombre, tipo, rubro, comuna, region, sitioWeb, publico, observaciones) " +
                        "VALUES (1, 'Municipalidad Demo', 'Municipalidad', 'Servicios públicos', 'Puente Alto', " +
                        "'Metropolitana', 'www.municipalidad-demo.cl', 'Vecinos', 'Datos ficticios')"
            )
            db.execSQL(
                "INSERT OR IGNORE INTO organizaciones " +
                        "(id, nombre, tipo, rubro, comuna, region, sitioWeb, publico, observaciones) " +
                        "VALUES (2, 'Empresa Demo', 'Empresa', 'Servicios', 'Las Condes', " +
                        "'Metropolitana', 'www.empresa-demo.cl', 'Colaboradores', 'Datos ficticios')"
            )
        }
    }
}
