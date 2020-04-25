package com.example.filemanagerkotlin2.Utils

import java.io.File

enum class FileType {

    FILE,
    FOLDER;

    companion object{
        fun getFileType(file: File) =when(file.isDirectory) {
            true -> FOLDER
            false -> FILE
        }
    }
}