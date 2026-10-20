package com.example.oportunidades.ui.components
import android.net.Uri
import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
@Composable
fun ImagenInteligente(uri: Uri?, onError: () -> Unit) {
    if (uri == null) Icon(Icons.Default.Person, "Perfil de demostración", Modifier.size(140.dp))
    else AndroidView(
        factory = { ImageView(it).apply { scaleType = ImageView.ScaleType.CENTER_CROP; contentDescription = "Imagen ficticia del perfil" } },
        modifier = Modifier.size(140.dp).clip(CircleShape),
        update = { image -> try { image.setImageURI(uri) } catch (e: Exception) { onError() } }
    )
}
