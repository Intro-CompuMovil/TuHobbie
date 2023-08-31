package com.example.tuhobbie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val but:Button=findViewById(R.id.verCanchasButton)
        val but2:Button=findViewById(R.id.verEquiposButton)
        val but3:Button=findViewById(R.id.verEventosButton)
        val but4:Button=findViewById(R.id.verPerfilButton)
        but.setOnClickListener {
            intent= Intent(this,InfoCanchas::class.java)
            startActivity(intent)
        }
        but2.setOnClickListener {
            intent= Intent(this,Equipos::class.java)
            startActivity(intent)
        }
        but3.setOnClickListener {
            intent= Intent(this,Eventos::class.java)
            startActivity(intent)
        }
        but4.setOnClickListener {
            intent= Intent(this,PerfilUsuario::class.java)
            startActivity(intent)
        }
    }
}