package com.example.tuhobbie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class Registro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        val but:Button=findViewById(R.id.btnRegister)
        val log:TextView=findViewById(R.id.loginTvBTN)
        but.setOnClickListener {
            intent= Intent(this,Home::class.java)
            startActivity(intent)
        }
        log.setOnClickListener {
            intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
}