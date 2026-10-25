package com.example.oportunidades.ui.utils

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.*
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun obtenerWindowSizeClass(): WindowSizeClass {
    val activity = LocalActivity.current as Activity
    return calculateWindowSizeClass(activity)
}

@Composable
fun obtenerAnchoContenido(): Float {
    val ventana = obtenerWindowSizeClass()
    return when (ventana.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 1f
        WindowWidthSizeClass.Medium -> 0.85f
        else -> 0.65f
    }
}
