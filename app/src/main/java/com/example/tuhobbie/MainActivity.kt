package com.example.tuhobbie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
      val but:Button=findViewById(R.id.button2)
      val reg:TextView=findViewById(R.id.text6)
      but.setOnClickListener {
          intent= Intent(this,Home::class.java)
          startActivity(intent)
      }
      reg.setOnClickListener {
          intent= Intent(this,Registro::class.java)
          startActivity(intent)
      }
    }

}