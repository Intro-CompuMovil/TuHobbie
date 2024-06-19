package com.example.tuhobbie

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class InfoEvento : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_evento)

        databaseReference = FirebaseDatabase.getInstance().reference.child("app").child("eventos")

        val eventoNombre = intent.getStringExtra("eventoElegido")
        if (eventoNombre != null) {
            fetchEventoInfo(eventoNombre)
        }
    }

    private fun fetchEventoInfo(eventoNombre: String) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val textViewName: TextView = findViewById(R.id.nombre1)
                val textViewInt: TextView = findViewById(R.id.nombreInter1)
                val textViewSigla: TextView = findViewById(R.id.siglas1)
                val textViewCapital: TextView = findViewById(R.id.capital1)

                for (eventoSnapshot in dataSnapshot.children) {
                    val nombreEvento = eventoSnapshot.child("nombre_evento").getValue(String::class.java)
                    if (nombreEvento == eventoNombre) {
                        val fecha = eventoSnapshot.child("fecha").getValue(String::class.java)
                        val organizador = eventoSnapshot.child("organizador").getValue(String::class.java)
                        val hora = eventoSnapshot.child("hora").getValue(String::class.java)

                        textViewName.text = "Nombre del evento: $nombreEvento"
                        textViewInt.text = "Fecha: $fecha"
                        textViewSigla.text = "Organizador: $organizador"
                        textViewCapital.text = "Hora: $hora"

                        break  // No need to continue iterating once we found the matching evento
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
                databaseError.toException().printStackTrace()
            }
        })
    }
}
