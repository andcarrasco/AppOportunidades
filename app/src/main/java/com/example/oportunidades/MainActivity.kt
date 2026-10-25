package com.example.oportunidades

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.oportunidades.navigation.AppNavigation
import com.example.oportunidades.ui.theme.OportunidadesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { OportunidadesTheme { AppNavigation() } }
    }
}
