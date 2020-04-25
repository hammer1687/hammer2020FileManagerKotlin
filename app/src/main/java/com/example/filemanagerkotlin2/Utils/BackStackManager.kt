package com.example.filemanagerkotlin2.Utils

import android.util.Log
import com.example.filemanager_kotlin.FileModel


class BackStackManager {
    private var files = mutableListOf<FileModel>()
    var onStackChangeListener: ((List<FileModel>) -> Unit)? = null

    val top: FileModel
        get() = files[files.size-1]

    fun addToStack(fileModel: FileModel) {
        Log.i("LOG_TAG", "addToStack: " + fileModel.path)
        files.add(fileModel)
        onStackChangeListener?.invoke(files)
    }

    fun popFromStack() {
        if(files.isNotEmpty())
            Log.i("LOG_TAG", "popFromStack")
        files.removeAt(files.size-1)
        onStackChangeListener?.invoke(files)
    }

    fun popFromStackTill(fileModel: FileModel) {
        Log.i("LOG_TAG", "popFromStackTill")
        files = files.subList(0,files.indexOf(fileModel)+1)
        onStackChangeListener?.invoke(files)
    }
}