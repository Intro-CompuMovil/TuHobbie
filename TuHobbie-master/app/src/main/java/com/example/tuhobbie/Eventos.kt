package com.example.tuhobbie

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class Eventos : AppCompatActivity() {
    private lateinit var eventosListView: ListView
    private var mEventosAdapter: EventosAdapter? = null
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos)

        eventosListView = findViewById(R.id.eventosListView)
        mEventosAdapter = EventosAdapter(this, R.layout.eventos_adapter, ArrayList())
        eventosListView.adapter = mEventosAdapter

        databaseReference = FirebaseDatabase.getInstance().reference.child("app").child("eventos")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val eventonombre = ArrayList<String>()

                for (eventoSnapshot in dataSnapshot.children) {
                    val nombreEvento = eventoSnapshot.child("nombre_evento").getValue(String::class.java)
                    nombreEvento?.let { eventonombre.add(it) }
                }

                mEventosAdapter?.clear()
                mEventosAdapter?.addAll(eventonombre)
                mEventosAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
                databaseError.toException().printStackTrace()
            }
        })

        eventosListView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val eventoNombre = parent.getItemAtPosition(position) as String
                val intent = Intent(this, InfoEvento::class.java)
                intent.putExtra("eventoElegido", eventoNombre)
                startActivity(intent)
            }
    }
}

