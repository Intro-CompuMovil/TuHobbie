package com.example.tuhobbie

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ListaAmigos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_amigos)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)){

            }
        }
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), CONTACTOS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val text:TextView=findViewById(R.id.Text)
        if(requestCode== CONTACTOS){
            for (i in permissions.indices){
                val permission=permissions[i]
                val grantResult=grantResults[i]
                if(permission==Manifest.permission.READ_CONTACTS){
                    if(grantResult==PackageManager.PERMISSION_GRANTED){
                      text.text="PERMISO DADO"
                    }else{
                        text.text="PERMISO NO DADO"
                    }
                }
            }
        }
    }

    companion object {
        const val CONTACTOS=0
        private const val CAMARA=1
    }

}