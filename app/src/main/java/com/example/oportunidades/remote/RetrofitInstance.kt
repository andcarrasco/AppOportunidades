package com.example.oportunidades.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singleton que contiene la configuración de Retrofit
object RetrofitInstance {

    // 10.0.2.2 es el computador visto desde el emulador.
    // En un teléfono físico se usa la IP del computador en la misma red Wi-Fi.
    private const val HOST = "http://10.0.2.2"

    val usuariosApi: UsuarioApiService by lazy {
        Retrofit.Builder()
            .baseUrl("$HOST:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UsuarioApiService::class.java)
    }

    val oportunidadesApi: OportunidadApiService by lazy {
        Retrofit.Builder()
            .baseUrl("$HOST:8082/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OportunidadApiService::class.java)
    }

    // API externa (no depende de los microservicios propios)
    val feriadosApi: FeriadoApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://date.nager.at/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FeriadoApiService::class.java)
    }
}
