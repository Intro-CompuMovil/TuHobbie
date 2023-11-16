package com.example.tuhobbie

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class InfoEquipo : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_equipo)

        databaseReference = FirebaseDatabase.getInstance().reference.child("app").child("equipos")

        val equipoNombre = intent.getStringExtra("equipoElegido")
        if (equipoNombre != null) {
            fetchEquipoInfo(equipoNombre)
        }
    }

    private fun fetchEquipoInfo(equipoNombre: String) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val textViewName: TextView = findViewById(R.id.nombre2)
                val textViewInt: TextView = findViewById(R.id.nombreInter2)
                val textViewSigla: TextView = findViewById(R.id.siglas2)
                val textViewCapital: TextView = findViewById(R.id.capital2)

                for (equipoSnapshot in dataSnapshot.children) {
                    val nombreEquipo = equipoSnapshot.child("nombre_equipo").getValue(String::class.java)
                    if (nombreEquipo == equipoNombre) {
                        val internName = equipoSnapshot.child("miembros").getValue(String::class.java)
                        val sigla = equipoSnapshot.child("capitan").getValue(String::class.java)
                        val capital = equipoSnapshot.child("deporte").getValue(String::class.java)

                        textViewName.text = "Nombre del equipo: $nombreEquipo"
                        textViewInt.text = "No miembros: $internName"
                        textViewSigla.text = "Capitan: $sigla"
                        textViewCapital.text = "Deporte: $capital"

                        break  // No need to continue iterating once we found the matching equipo
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
