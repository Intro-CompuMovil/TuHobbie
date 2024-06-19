package com.example.tuhobbie

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class CrearEquipo : AppCompatActivity() {

    private lateinit var nombreEquipoEditText: EditText
    private lateinit var capitanEquipoEditText: EditText
    private lateinit var deporteEquipoEditText: EditText
    private lateinit var crearEquipoButton: Button

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_equipo)

        nombreEquipoEditText = findViewById(R.id.nombreEquipoEditText)
        capitanEquipoEditText = findViewById(R.id.capitanEquipoEditText)
        deporteEquipoEditText = findViewById(R.id.deporteEquipoEditText)
        crearEquipoButton = findViewById(R.id.invitarAmigosButton) // Rename the button for consistency

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().reference.child("app").child("equipos")

        // Set up Create Team Button click listener
        crearEquipoButton.setOnClickListener {
            createEquipo()
        }
    }

    private fun createEquipo() {
        val nombreEquipo = nombreEquipoEditText.text.toString().trim()
        val capitanEquipo = capitanEquipoEditText.text.toString().trim()
        val deporteEquipo = deporteEquipoEditText.text.toString().trim()

        if (nombreEquipo.isEmpty() || capitanEquipo.isEmpty() || deporteEquipo.isEmpty()) {
            // Handle empty fields
            // You can show a Toast or some UI feedback here
            return
        }

        // Create a new team object
        val newEquipo = mapOf(
            "capitan" to capitanEquipo,
            "nombre_equipo" to nombreEquipo,
            "miembros" to 0,
            "deporte" to deporteEquipo
        )

        // Push the new team to Firebase
        databaseReference.push().setValue(newEquipo)
            .addOnSuccessListener {
                // Handle success, if needed
            }
            .addOnFailureListener {
                // Handle failure, if needed
            }

        // Optionally, you can finish the activity or show a success message
        finish()
    }
}