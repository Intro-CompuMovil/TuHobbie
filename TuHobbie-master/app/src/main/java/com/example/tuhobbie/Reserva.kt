package com.example.tuhobbie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Reserva : AppCompatActivity() {

    private lateinit var textViewName: TextView
    private lateinit var textViewInt: TextView
    private lateinit var textViewSigla: TextView
    private lateinit var textViewCapital: TextView

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserva)

        // Initialize UI elements
        val buttonReserva: Button = findViewById(R.id.btnReserva)
        val buttonCancelarReserva: Button = findViewById(R.id.btnCancelarReserva)

        textViewName = findViewById(R.id.nombre)
        textViewInt = findViewById(R.id.nombreInter)
        textViewSigla = findViewById(R.id.siglas)
        textViewCapital = findViewById(R.id.capital)

        buttonReserva.setOnClickListener {
            intent = Intent(this, CrearReserva::class.java)
            intent.putExtra("canchaElegida", textViewName.text.toString())
            startActivity(intent)
        }

        // Add logic to navigate to ListaReserva activity when the "Cancelar Reserva" button is clicked
        buttonCancelarReserva.setOnClickListener {
            val intent = Intent(this, ListaReserva::class.java)
            startActivity(intent)
        }

        databaseReference = FirebaseDatabase.getInstance().reference.child("app").child("canchas")

        val pais = intent.getStringExtra("canchaElegida")

        // Query Firebase for the selected cancha
        val query = databaseReference.orderByChild("nombre_cancha").equalTo(pais)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val internName = data.child("lugar").value.toString()
                        val sigla = data.child("dueno").value.toString()
                        val capital = data.child("deporte").value.toString()

                        // Update TextViews with Firebase data
                        textViewName.text = "Nombre cancha: $pais"
                        textViewInt.text = "Ubicacion: $internName"
                        textViewSigla.text = "Dueno: $sigla"
                        textViewCapital.text = "Deporte: $capital"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
