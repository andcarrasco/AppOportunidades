package com.example.oportunidades

import android.app.Application

// Clase de la aplicación: guarda el contexto para crear la base de datos en los repositorios
class OportunidadesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instancia = this
    }

    companion object {
        lateinit var instancia: OportunidadesApp
    }
}
