package com.example.tuhobbie

import android.content.pm.PackageManager
import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class AgregarCancha : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_cancha)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            }
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMARA)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val text: TextView=findViewById(R.id.textView4)
        if (requestCode == CAMARA) {
            for (i in permissions.indices) {
                val permission = permissions[i]
                val grantResult = grantResults[i]
                if (permission == Manifest.permission.CAMERA) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        text.text = "PERMISO DE CÁMARA DADO"
                    } else {
                        text.text = "PERMISO DE CÁMARA NO DADO"
                    }
                }
            }
        }
    }

    companion object {
        private const val CONTACTOS = 0
        const val CAMARA = 1
    }
}