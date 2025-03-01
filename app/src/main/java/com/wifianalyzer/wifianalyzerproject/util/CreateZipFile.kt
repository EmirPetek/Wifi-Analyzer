package com.wifianalyzer.wifianalyzerproject.util

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


class CreateZipFile {

    fun zipFolder(sourceFolderPath: String, zipFilePath: String) {
        val sourceFolder = File(sourceFolderPath)
        val zipFile = File(zipFilePath)

        // ZipOutputStream ile zip dosyasına yazıyoruz
        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->
            addFolderToZip(rootFolder = sourceFolder, sourceFile = sourceFolder, zos = zos)
        }
    }

    private fun addFolderToZip(rootFolder: File, sourceFile: File, zos: ZipOutputStream) {
        if (sourceFile.isDirectory) {
            // Klasörse içerisindeki tüm dosya ve klasörleri işleme al
            sourceFile.listFiles()?.forEach { file ->
                addFolderToZip(rootFolder, file, zos)
            }
        } else {
            // Klasör yapısını korumak için, rootFolder'a göre relative path oluşturuyoruz
            val relativePath = sourceFile.absolutePath.substring(rootFolder.absolutePath.length + 1)
            FileInputStream(sourceFile).use { fis ->
                val entry = ZipEntry(relativePath)
                zos.putNextEntry(entry)
                // Dosya içeriğini zip'e kopyala
                fis.copyTo(zos)
                zos.closeEntry()
            }
        }

    }

}