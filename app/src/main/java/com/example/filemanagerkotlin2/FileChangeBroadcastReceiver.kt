package com.example.filemanagerkotlin2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class FileChangeBroadcastReceiver(val path: String, val onChange: () -> Unit ): BroadcastReceiver() {

    companion object{
        const val EXTRA_PATH = "com.example.filemanager_kotlin.fileservice.path"
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        val filePath = intent?.extras?.getString(EXTRA_PATH)
        if (filePath.equals(path)) {
            onChange.invoke()
        }
    }


}