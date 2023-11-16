package com.example.tuhobbie

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class Equipos : AppCompatActivity() {
    private lateinit var equiposListView: ListView
    private var mEquiposAdapter: EquiposAdapter? = null
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equipos)

        equiposListView = findViewById(R.id.listaEquipos)
        mEquiposAdapter = EquiposAdapter(this, R.layout.eventos_adapter, ArrayList())
        equiposListView.adapter = mEquiposAdapter

        databaseReference = FirebaseDatabase.getInstance().reference.child("app").child("equipos")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val equiponombre = ArrayList<String>()

                for (equipoSnapshot in dataSnapshot.children) {
                    val nombreEquipo = equipoSnapshot.child("nombre_equipo").getValue(String::class.java)
                    nombreEquipo?.let { equiponombre.add(it) }
                }

                mEquiposAdapter?.clear()
                mEquiposAdapter?.addAll(equiponombre)
                mEquiposAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
                databaseError.toException().printStackTrace()
            }
        })

        equiposListView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val equipoNombre = parent.getItemAtPosition(position) as String
                val intent = Intent(this, InfoEquipo::class.java)
                intent.putExtra("equipoElegido", equipoNombre)
                startActivity(intent)
            }
    }
}
