package com.example.laboratoriocircuitos.logic

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper

object LectorPDF {

    fun extraerTexto(
        context: Context,
        uri: Uri
    ): String {

        PDFBoxResourceLoader.init(context.applicationContext)

        context.contentResolver.openInputStream(uri).use { input ->

            requireNotNull(input) {
                "No se pudo abrir el archivo PDF."
            }

            PDDocument.load(input).use { documento ->

                require(!documento.isEncrypted) {
                    "El documento PDF está protegido con contraseña."
                }

                val extractor = PDFTextStripper()

                return extractor.getText(documento).trim()
            }
        }
    }
}
