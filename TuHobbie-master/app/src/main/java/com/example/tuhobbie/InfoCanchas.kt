package com.example.tuhobbie

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class InfoCanchas : AppCompatActivity() {
    private lateinit var canchasListView: ListView
    private var mCanchasAdapter: CanchasAdapter? = null
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_canchas)


        canchasListView = findViewById(R.id.listaCanchas)
        mCanchasAdapter = CanchasAdapter(this, R.layout.canchas_adapter, ArrayList())
        canchasListView.adapter = mCanchasAdapter

        databaseReference = FirebaseDatabase.getInstance().reference.child("app").child("canchas")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val canchanombre = ArrayList<String>()

                for (canchaSnapshot in dataSnapshot.children) {
                    val nombreCancha = canchaSnapshot.child("nombre_cancha").getValue(String::class.java)
                    nombreCancha?.let { canchanombre.add(it) }
                }

                mCanchasAdapter?.clear()
                mCanchasAdapter?.addAll(canchanombre)
                mCanchasAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
                databaseError.toException().printStackTrace()
            }
        })

        canchasListView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val canchaNombre = parent.getItemAtPosition(position) as String
                val intent = Intent(this, Reserva::class.java)
                intent.putExtra("canchaElegida", canchaNombre)
                startActivity(intent)
            }
    }
}
