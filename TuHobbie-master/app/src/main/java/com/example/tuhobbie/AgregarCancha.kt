package com.example.tuhobbie

import android.content.pm.PackageManager
import android.Manifest
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AgregarCancha : AppCompatActivity() {

    private lateinit var nombreCanchaEditText: EditText
    private lateinit var ubicacionCanchaEditText: EditText
    private lateinit var duenoCanchaEditText: EditText
    private lateinit var deporteCanchaEditText: EditText
    private lateinit var guardarCanchaButton: Button
    private lateinit var photoImageView: ImageView

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_cancha)

        // Initialize UI elements
        nombreCanchaEditText = findViewById(R.id.nombreCanchaEditText)
        ubicacionCanchaEditText = findViewById(R.id.ubicacionCanchaEditText)
        duenoCanchaEditText = findViewById(R.id.duenoCanchaEditText)
        deporteCanchaEditText = findViewById(R.id.deporteCanchaEditText)
        guardarCanchaButton = findViewById(R.id.guardarCanchaButton)
        photoImageView = findViewById(R.id.photoImageView)

        databaseReference = FirebaseDatabase.getInstance().reference.child("app").child("canchas")

        // Request camera permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // Explain why you need the camera permission
            }
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMARA)
        }

        // Set up Save Cancha Button click listener
        guardarCanchaButton.setOnClickListener {
            saveCanchaToFirebase()
        }
    }

    private fun saveCanchaToFirebase() {
        val nombreCancha = nombreCanchaEditText.text.toString().trim()
        val ubicacionCancha = ubicacionCanchaEditText.text.toString().trim()
        val duenoCancha = duenoCanchaEditText.text.toString().trim()
        val deporteCancha = deporteCanchaEditText.text.toString().trim()

        if (nombreCancha.isEmpty() || ubicacionCancha.isEmpty() || duenoCancha.isEmpty() || deporteCancha.isEmpty()) {
            // Handle empty fields
            Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a new cancha object
        val newCancha = mapOf(
            "nombre_cancha" to nombreCancha,
            "lugar" to ubicacionCancha,
            "dueno" to duenoCancha,
            "deporte" to deporteCancha
        )

        // Push the new cancha to Firebase
        databaseReference.push().setValue(newCancha)

        // Optionally, you can finish the activity or show a success message
        Toast.makeText(this, "Cancha saved successfully", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMARA) {
            for (i in permissions.indices) {
                val permission = permissions[i]
                val grantResult = grantResults[i]
                if (permission == Manifest.permission.CAMERA) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "CAMERA PERMISSION GRANTED", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "CAMERA PERMISSION DENIED", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    companion object {
        private const val CAMARA = 1
    }
}
