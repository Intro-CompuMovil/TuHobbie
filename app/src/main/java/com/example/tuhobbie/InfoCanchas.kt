package com.example.tuhobbie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class InfoCanchas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_canchas)
        val but:Button=findViewById(R.id.reservarButton)
        val but2:Button=findViewById(R.id.agregarCanchaButton)
        but.setOnClickListener {
            intent= Intent(this,Reserva::class.java)
            startActivity(intent)
        }
        but2.setOnClickListener {
            intent= Intent(this,AgregarCancha::class.java)
            startActivity(intent)
        }
    }
}