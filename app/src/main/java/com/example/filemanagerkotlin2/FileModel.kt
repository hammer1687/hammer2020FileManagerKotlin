package com.example.filemanager_kotlin


import com.example.filemanagerkotlin2.Utils.FileType

data class FileModel (
    val path: String,
    val fileType: FileType,
    val name: String,
    val sizeInMB: Double,
    val extension: String = "",
    val subFiles: Int = 0


)