package com.example.oportunidades.model.data.repository

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class EvidenciaRepository(private val context: Context) {
    private fun carpeta(oportunidadId: Long): File {
        require(oportunidadId > 0)
        return File(context.filesDir, "evidencias/$oportunidadId")
    }

    private fun uri(archivo: File): Uri =
        FileProvider.getUriForFile(context, context.packageName + ".fileprovider", archivo)

    fun obtenerImagenes(oportunidadId: Long): List<Uri> {
        val archivos = carpeta(oportunidadId).listFiles() ?: return emptyList()
        val imagenes = mutableListOf<Uri>()
        for (archivo in archivos) {
            if (archivo.isFile && archivo.name.startsWith("foto_") && archivo.length() > 0) {
                imagenes.add(uri(archivo))
            }
        }
        return imagenes
    }

    fun prepararCaptura(oportunidadId: Long): Uri {
        val destino = carpeta(oportunidadId)
        check(destino.isDirectory || destino.mkdirs())
        val archivo = File.createTempFile("pendiente_", ".jpg", destino)
        return uri(archivo)
    }

    fun finalizarCaptura(oportunidadId: Long, captura: Uri, aceptada: Boolean) {
        val nombre = captura.lastPathSegment ?: error("Captura sin nombre")
        require(nombre.startsWith("pendiente_") && '/' !in nombre)
        val archivo = File(carpeta(oportunidadId), nombre)
        if (!aceptada) {
            archivo.delete()
            return
        }
        if (!archivo.isFile || archivo.length() == 0L) {
            archivo.delete()
            error("La cámara no entregó una imagen")
        }
        val confirmada = File(archivo.parentFile, "foto_" + nombre.removePrefix("pendiente_"))
        check(archivo.renameTo(confirmada))
    }
}
