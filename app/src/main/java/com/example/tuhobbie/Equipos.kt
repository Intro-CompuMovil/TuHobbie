package com.example.tuhobbie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Equipos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equipos)
        val but:Button=findViewById(R.id.crearEquipoButton)
        but.setOnClickListener {
            intent= Intent(this,CrearEquipo::class.java)
            startActivity(intent)
        }
    }
}