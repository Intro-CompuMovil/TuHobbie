package com.example.tuhobbie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class PerfilUsuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_usuario)
        val but:Button=findViewById(R.id.configuracionButton)
        val but2:Button=findViewById(R.id.amigosButton)
        but.setOnClickListener {
            intent= Intent(this,Configuracion::class.java)
            startActivity(intent)
        }
        but2.setOnClickListener {
            intent= Intent(this,ListaAmigos::class.java)
            startActivity(intent)
        }
    }
}