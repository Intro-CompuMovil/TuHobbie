package com.example.tuhobbie
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

import java.text.SimpleDateFormat
import java.util.*

class CreacionEvento : AppCompatActivity() {

    private lateinit var nombreEventoEditText: EditText
    private lateinit var organizadorEventoEditText: EditText
    private lateinit var calendarView: CalendarView
    private lateinit var horaNumberPicker: NumberPicker
    private lateinit var minutoNumberPicker: NumberPicker
    private lateinit var crearEventoButton: Button

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creacion_evento)

        nombreEventoEditText = findViewById(R.id.nombreEventoEditText)
        organizadorEventoEditText = findViewById(R.id.organizadorEventoEditText)
        calendarView = findViewById(R.id.calendarView)
        horaNumberPicker = findViewById(R.id.horaNumberPicker)
        minutoNumberPicker = findViewById(R.id.minutoNumberPicker)
        crearEventoButton = findViewById(R.id.crearEventoButton)

        databaseReference = FirebaseDatabase.getInstance().reference.child("app").child("eventos")

        // Set up NumberPickers
        horaNumberPicker.minValue = 0
        horaNumberPicker.maxValue = 23
        minutoNumberPicker.minValue = 0
        minutoNumberPicker.maxValue = 59

        // Set up Create Event Button click listener
        crearEventoButton.setOnClickListener {
            createEvent()
        }
    }

    private fun createEvent() {
        val nombreEvento = nombreEventoEditText.text.toString().trim()
        val organizadorEvento = organizadorEventoEditText.text.toString().trim()

        if (nombreEvento.isEmpty() || organizadorEvento.isEmpty()) {
            // Handle empty fields
            // You can show a Toast or some UI feedback here
            return
        }

        val selectedDate = Date(calendarView.date)
        val hora = horaNumberPicker.value
        val minuto = minutoNumberPicker.value

        val calendar = Calendar.getInstance().apply {
            time = selectedDate
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        val fecha = dateFormat.format(calendar.time)
        val horaFormateada = timeFormat.format(calendar.time)

        // Create a new event object
        val newEvent = mapOf(
            "organizador" to organizadorEvento,
            "nombre_evento" to nombreEvento,
            "fecha" to fecha,
            "hora" to horaFormateada
        )

        // Push the new event to Firebase
        databaseReference.push().setValue(newEvent)

        // Optionally, you can finish the activity or show a success message
        finish()
    }
}
