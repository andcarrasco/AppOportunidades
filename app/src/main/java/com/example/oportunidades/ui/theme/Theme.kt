package com.example.oportunidades.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
private val claro = lightColorScheme(primary = Color(0xFF176B5B), secondary = Color(0xFF425E73), tertiary = Color(0xFF8C612D))
@Composable
fun OportunidadesTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else claro, content = content)
}
