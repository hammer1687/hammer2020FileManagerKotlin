package com.example.filemanagerkotlin2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class TermsOfServiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_of_service)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    companion object{
        fun start(context: Context) {
            val intent  = Intent(context, TermsOfServiceActivity::class.java)
            context.startActivity(intent)

        }
    }
}
