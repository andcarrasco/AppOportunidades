package com.example.oportunidades.ui.utils
import android.app.Activity
import androidx.compose.material3.windowsizeclass.*
import androidx.compose.runtime.Composable
import androidx.activity.compose.LocalActivity
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun obtenerWindowSizeClass(): WindowSizeClass = calculateWindowSizeClass(LocalActivity.current as Activity)
