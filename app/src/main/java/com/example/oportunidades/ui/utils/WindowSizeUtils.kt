package com.example.oportunidades.ui.utils

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable

// Tamaño de la ventana actual (guía 9: adaptabilidad)
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun obtenerWindowSizeClass(): WindowSizeClass {
    val activity = LocalActivity.current as Activity
    return calculateWindowSizeClass(activity)
}

// Ancho del contenido según el tamaño de la pantalla
@Composable
fun obtenerAnchoContenido(): Float {
    val ventana = obtenerWindowSizeClass()
    return when (ventana.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 1f
        WindowWidthSizeClass.Medium -> 0.85f
        else -> 0.65f
    }
}
