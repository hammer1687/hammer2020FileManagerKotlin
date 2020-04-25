package com.example.filemanagerkotlin2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    companion object{
        fun start(context: Context) {
            val intent  = Intent(context,SettingsActivity::class.java)
            context.startActivity(intent)

        }
    }
}
