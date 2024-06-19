package com.example.tuhobbie

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CrearReserva : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_reserva)

        val textView: TextView = findViewById(R.id.textoCancha)
        val canchaElegida = intent.getStringExtra("canchaElegida")
        textView.text = canchaElegida

        val nombreEquipoEditText: EditText = findViewById(R.id.nombreEquipoEditText)
        val calendarView: CalendarView = findViewById(R.id.calendarViewReserva)
        val horaNumberPicker: NumberPicker = findViewById(R.id.horaNumberPickerReserva)
        val minutoNumberPicker: NumberPicker = findViewById(R.id.minutoNumberPickerReserva)

        val crearReservaButton: Button = findViewById(R.id.crearReservaButton)

        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("app").child("reserva")

        crearReservaButton.setOnClickListener {
            val fechaReserva =
                SimpleDateFormat("MMMM d", Locale.getDefault()).format(Date(calendarView.date))
            val horaReserva = "${horaNumberPicker.value}:${minutoNumberPicker.value} am"

            // Utilizar un coroutine para manejar la operación asíncrona
            lifecycleScope.launch(Dispatchers.Main) {
                // Obtener el nombre del capitán
                val capitan = obtenerCapitan(nombreEquipoEditText.text.toString())

                // Crear un objeto de reserva
                val nuevaReserva = mapOf(
                    "nombre_equipo" to nombreEquipoEditText.text.toString(),
                    "capitan" to capitan,
                    "nombre_cancha" to canchaElegida,
                    "hora" to horaReserva,
                    "fecha" to fechaReserva
                )

                // Almacenar la reserva en Firebase
                databaseReference.push().setValue(nuevaReserva)

                // Puedes agregar un mensaje de éxito o realizar otras acciones después de almacenar la reserva

                // Finalizar la actividad
                finish()
            }
        }
    }

    private suspend fun obtenerCapitan(nombreEquipo: String): String? =
        suspendCoroutine { continuation ->
            val equiposReference: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child("app").child("equipos")

            // Realizar la consulta para obtener el capitán del equipo específico
            equiposReference.orderByChild("nombre_equipo").equalTo(nombreEquipo)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // El equipo existe en la base de datos
                            for (equipoSnapshot in snapshot.children) {
                                val capitan =
                                    equipoSnapshot.child("capitan").getValue(String::class.java)

                                // Llamar al callback de la continuación con el nombre del capitán
                                continuation.resume(capitan)
                                return
                            }
                        } else {
                            // Llamar al callback de la continuación con null si el equipo no existe
                            continuation.resume(null)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Llamar al callback de la continuación con null en caso de error
                        continuation.resume(null)
                        println("Error en la consulta: ${error.message}")
                    }
                })
        }
}
