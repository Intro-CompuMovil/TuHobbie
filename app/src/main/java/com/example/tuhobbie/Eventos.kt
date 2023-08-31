package com.example.tuhobbie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Eventos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos)
        val but:Button=findViewById(R.id.agregarEventosButton)
        but.setOnClickListener {
            intent= Intent(this,CreacionEvento::class.java)
            startActivity(intent)
        }
    }
}