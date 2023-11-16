package com.example.tuhobbie

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import com.google.firebase.auth.*
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PerfilUsuario : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2

    private val desiredWidth = 500
    private val desiredHeight = 500

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestGalleryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                Toast.makeText(this, "Permiso de galería denegado", Toast.LENGTH_SHORT).show()
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_usuario)
        val but: Button = findViewById(R.id.agregarCancha)
        val but2: Button = findViewById(R.id.amigosButton)
        val but3: Button = findViewById(R.id.crearEvento)
        val but4: Button = findViewById(R.id.crearEquipo)
        val but5: Button = findViewById(R.id.verHistorial)

        val btnCerrarSesión: Button = findViewById(R.id.editarPerfilButton)

        btnCerrarSesión.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        but2.setOnClickListener {
            intent = Intent(this, ListaAmigos::class.java)
            startActivity(intent)
        }
        but3.setOnClickListener {
            intent = Intent(this, CreacionEvento::class.java)
            startActivity(intent)
        }
        but.setOnClickListener {
            intent = Intent(this, AgregarCancha::class.java)
            startActivity(intent)
        }
        but4.setOnClickListener {
            intent = Intent(this, CrearEquipo::class.java)
            startActivity(intent)
        }
        but5.setOnClickListener {
            intent = Intent(this, InfoCanchas::class.java)
            startActivity(intent)
        }
        val cam: Button = findViewById(R.id.camara)
        val gal: Button = findViewById(R.id.Gallery)
        imageView = findViewById(R.id.imagen)
        cam.setOnClickListener {
            checkCameraPermission()
        }

        gal.setOnClickListener {
            checkGalleryPermission()
        }
    }
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            dispatchTakePictureIntent()
        }
    }
    private fun checkGalleryPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestGalleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            openGallery()
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Manejar el resultado de la cámara
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            // Redimensionar la imagen capturada antes de mostrarla
            val resizedImage = resizeImage(imageBitmap, desiredWidth, desiredHeight)

            imageView.setImageBitmap(resizedImage)

            // Guarda la imagen capturada en la galería del teléfono
            val imageUri = saveImageToGallery(resizedImage)
            if (imageUri != null) {
                Toast.makeText(
                    this,
                    "Imagen de la cámara guardada en la galería",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Manejar el resultado de la selección de la galería
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                val imageUri = data.data
                try {
                    val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

                    // Redimensionar la imagen seleccionada antes de mostrarla
                    val resizedImage = resizeImage(imageBitmap, desiredWidth, desiredHeight)

                    imageView.setImageBitmap(resizedImage)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        "Error al cargar la imagen de la galería",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "No se seleccionó ninguna imagen de la galería",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun resizeImage(image: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(image, width, height, false)
    }


    private fun saveImageToGallery(bitmap: Bitmap): Uri? {
        val savedImageURL = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            "ImageTitle",
            "ImageDescription"
        )

        return Uri.parse(savedImageURL)
    }


    companion object {
        const val CAMARA = 1
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_PICK_IMAGE = 2
    }
}