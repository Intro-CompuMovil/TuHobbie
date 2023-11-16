package com.example.tuhobbie

import android.Manifest
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.TilesOverlay
import java.io.IOException
import android.location.Address
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONException
import org.json.JSONObject
import java.util.Locale

class Home : AppCompatActivity() {
    private lateinit var mapView: MapView
    private var roadOverlay: Polyline? = null
    private lateinit var roadManager: RoadManager
    private lateinit var Direccion: EditText
    private var deporteSeleccionado: String = "Todos"
    private val markers = ArrayList<Marker>()
    private val marcadoresDeportes = mapOf(
        "Cancha Fútbol" to "Fútbol",
        "Cancha Tenis" to "Tenis",
        "Cancha Americano" to "Americano",
        "Cancha Voleibol" to "Voleibol",
        "Cancha Baloncesto" to "Baloncesto",
        "Cancha Beisbol" to "Beisbol"

        // Agrega más entradas según tus marcadores
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        Configuration.getInstance()
            .load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION
        Direccion = findViewById(R.id.texto)

        roadManager = OSRMRoadManager(this, "ANDROID")

        if (ContextCompat.checkSelfPermission(
                this,
                fineLocationPermission
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                coarseLocationPermission
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Mostrar explicación si es necesario (esto es opcional)
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, fineLocationPermission) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, coarseLocationPermission)
                        )
            ) {
                // Puedes mostrar una explicación aquí si lo deseas.
            }

            // Solicitar permisos de ubicación
            ActivityCompat.requestPermissions(
                this,
                arrayOf(fineLocationPermission, coarseLocationPermission),
                LOCALIZACION
            )
        } else {
            mapView = findViewById(R.id.mapView)
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)
            setUpMapViewListener()
            // Después de inicializar la variable Direccion
            val spinnerDeportes = findViewById<Spinner>(R.id.spinnerDeportes)
            val deportes = resources.getStringArray(R.array.deportes_array)
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, deportes)
            spinnerDeportes.adapter = adapter

            spinnerDeportes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val deporte = deportes[position]
                    deporteSeleccionado = deporte
                    aplicarFiltroDeporte(deporte)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No se necesita implementación aquí
                }
            }


            val currentLocation = getCurrentLocation()
            if (currentLocation != null) {
                mapView.controller.setZoom(18.0)
                mapView.controller.setCenter(
                    org.osmdroid.util.GeoPoint(
                        currentLocation.latitude,
                        currentLocation.longitude
                    )
                )
            }
            Direccion.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    val dir = Direccion.text.toString()
                    val geoPoint = buscarDireccion(dir)
                    if (dir.isNotEmpty()) {
                        if (geoPoint != null) {

                            showMarker(geoPoint, "Ubicacion encontrada", "B_Dir")
                            val mapController = mapView.controller
                            mapController.setZoom(18.0)  // Puedes ajustar el nivel de zoom
                            mapController.setCenter(geoPoint)
                        } else {
                            Toast.makeText(
                                this,
                                "La direccion no pudo ser encontrada",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    return@setOnEditorActionListener true // Agregar esta línea
                }
                false // No se maneja otro tipo de evento de acción
            }
            parsearJSON()


        }

        val but2: Button = findViewById(R.id.verEquiposButton)
        val but3: Button = findViewById(R.id.verEventosButton)
        val but4: Button = findViewById(R.id.verPerfilButton)


        but2.setOnClickListener {
            intent = Intent(this, Equipos::class.java)
            startActivity(intent)
        }
        but3.setOnClickListener {
            intent = Intent(this, Eventos::class.java)
            startActivity(intent)
        }
        but4.setOnClickListener {
            intent = Intent(this, PerfilUsuario::class.java)
            startActivity(intent)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCALIZACION) {
            var fineLocationGranted = false
            var coarseLocationGranted = false
            for (i in permissions.indices) {
                val permission = permissions[i]
                val grantResult = grantResults[i]
                if (permission == Manifest.permission.ACCESS_FINE_LOCATION) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        fineLocationGranted = true
                    }
                } else if (permission == Manifest.permission.ACCESS_COARSE_LOCATION) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        coarseLocationGranted = true
                    }
                }
            }

            if (fineLocationGranted && coarseLocationGranted) {
                Toast.makeText(this, "PERMISOS DE LOCALIZACIÓN DADOS", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "ALGUNO DE LOS PERMISOS DE LOCALIZACIÓN NO FUE DADO",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Reanudar el mapa de OSMDroid
        mapView.onResume()
        val mapController: IMapController = mapView.controller
        mapController.setZoom(18.0)


        // Obtener la ubicación actual
        val currentLocation = getCurrentLocation()

        // Verificar si se pudo obtener la ubicación actual
        if (currentLocation != null) {
            // Obtener el controlador del mapa
            val mapController = mapView.controller

            // Configurar el zoom y el centro del mapa en la ubicación actual
            mapController.setZoom(18.0)
            mapController.setCenter(
                org.osmdroid.util.GeoPoint(
                    currentLocation.latitude,
                    currentLocation.longitude
                )
            )

            // Establecer marcadores en el mapa
            showMarker(
                org.osmdroid.util.GeoPoint(
                    currentLocation.latitude,
                    currentLocation.longitude
                ), "Ubicacion actual", "U_A"
            )

            showMarker(
                org.osmdroid.util.GeoPoint(4.647327414834436, -74.07575100117528),
                "Cancha Tenis",
                "Tenis"
            )

            showMarker(
                org.osmdroid.util.GeoPoint(4.7112444237342395, -74.07177802349177),
                "Cancha Fútbol",
                "Futbol"
            )
            showMarker(
                org.osmdroid.util.GeoPoint(4.704653322769599, -74.12105912354257),
                "Cancha Americano",
                "Americano"
            )
            showMarker(
                org.osmdroid.util.GeoPoint(4.734634729222503, -74.0615729005996),
                "Cancha Voleibol",
                "Voleibol"
            )
            showMarker(
                org.osmdroid.util.GeoPoint(4.713044447775575, -74.14344360073954),
                "Cancha Baloncesto",
                "Baloncesto"
            )
            showMarker(
                org.osmdroid.util.GeoPoint(4.664892128032969, -74.09746650107071),
                "Cancha Beisbol",
                "Beisbol"
            )


        } else {
            Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_SHORT).show()
        }
        val uiManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        if (uiManager.nightMode == UiModeManager.MODE_NIGHT_YES) {
            mapView.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
        }
    }

    private fun showMarker(geoPoint: org.osmdroid.util.GeoPoint, markerName: String, tipo: String) {
        // Crea y muestra un nuevo marcador en la ubicación proporcionada
        val marker = Marker(mapView)
        marker.title = markerName
        marker.position = geoPoint


        marker.setOnMarkerClickListener { _, _ ->
            calculateRouteToMarker(geoPoint)
            true
        }




        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        // Icono personalizado según el deporte
        when (tipo) {
            "U_A" -> marker.icon = resources.getDrawable(R.drawable.baseline_emoji_people_24, theme)
            "Tenis" -> marker.icon = resources.getDrawable(R.drawable.pista_de_tenis, theme)
            "Americano" -> marker.icon =
                resources.getDrawable(R.drawable.campo_de_futbol_americano, theme)

            "Baloncesto" -> marker.icon = resources.getDrawable(R.drawable.pista_de_baloncesto, theme)
            "Futbol" -> marker.icon = resources.getDrawable(R.drawable.campo_de_futbol, theme)
            "Voleibol" -> marker.icon = resources.getDrawable(R.drawable.red_de_voleibol, theme)
            "Beisbol" -> marker.icon = resources.getDrawable(R.drawable.campo_de_beisbol, theme)
            "B_Dir" -> marker.icon =
                resources.getDrawable(R.drawable.baseline_location_on_24, theme)


        }

        mapView.overlays.add(marker)
        markers.add(marker)
    }


    private fun getCurrentLocation(): Location? {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return null
            }
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun calculateRouteToMarker(destinationPoint: org.osmdroid.util.GeoPoint) {
        // Inicia la tarea asíncrona para calcular la ruta
        GetRouteTask().execute(destinationPoint)
    }

    private inner class GetRouteTask : AsyncTask<org.osmdroid.util.GeoPoint, Void, Road>() {
        override fun doInBackground(vararg params: org.osmdroid.util.GeoPoint): Road? {
            val routePoints = ArrayList<org.osmdroid.util.GeoPoint>()

            // Obtener la ubicación actual del usuario
            val currentLocation = getCurrentLocation()
            if (currentLocation != null) {
                routePoints.add(
                    org.osmdroid.util.GeoPoint(
                        currentLocation.latitude,
                        currentLocation.longitude
                    )
                )
            } else {
                // Mostrar un mensaje de error si no se pudo obtener la ubicación actual
                Toast.makeText(
                    this@Home,
                    "No se pudo obtener la ubicación actual",
                    Toast.LENGTH_SHORT
                ).show()
                return null
            }

            // Agregar el destino a los puntos de la ruta
            routePoints.add(params[0]) // Destino

            return roadManager.getRoad(routePoints)
        }

        override fun onPostExecute(result: Road?) {
            super.onPostExecute(result)
            if (result != null) {
                // Dibujar la ruta
                drawRoadOverlay(result)
            } else {
                // Mostrar un mensaje de error en caso de que no se pueda obtener la ruta
                Toast.makeText(this@Home, "Error al obtener la ruta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun drawRoadOverlay(road: Road) {
        roadOverlay?.let { mapView.overlays.remove(it) }
        roadOverlay = RoadManager.buildRoadOverlay(road)
        roadOverlay?.outlinePaint?.color = ContextCompat.getColor(this, R.color.red)
        roadOverlay?.outlinePaint?.strokeWidth = 10f
        mapView.overlays.add(roadOverlay)
    }

    private fun setUpMapViewListener() {
        mapView.setOnClickListener {
            // Ubicación actual del usuario
            val currentLocation = getCurrentLocation()

            // Verifica si se pudo obtener la ubicación actual
            if (currentLocation != null) {
                // Obtener el controlador del mapa
                val mapController = mapView.controller

                // Configurar el zoom y el centro del mapa en la ubicación actual
                mapController.setZoom(18.0)
                mapController.setCenter(
                    org.osmdroid.util.GeoPoint(
                        currentLocation.latitude,
                        currentLocation.longitude
                    )
                )

                // Establecer marcadores en el mapa
                showMarker(
                    org.osmdroid.util.GeoPoint(
                        currentLocation.latitude,
                        currentLocation.longitude
                    ), "Ubicacion actual", "U_A"
                )

                // ... (otros marcadores)

            } else {
                Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buscarDireccion(direccion: String): org.osmdroid.util.GeoPoint? {
        val mGeocoder = Geocoder(baseContext)
        val addressString = direccion
        if (addressString.isNotEmpty()) {
            try {
                val addresses = mGeocoder.getFromLocationName(addressString, 2)
                if (!addresses.isNullOrEmpty()) {
                    val addressResult = addresses[0]
                    return org.osmdroid.util.GeoPoint(
                        addressResult.latitude,
                        addressResult.longitude
                    )
                }
            } catch (e: IOException) {
                Log.e("Error", "Error en buscarDireccion: ${e.message}")
                e.printStackTrace()
            }

        }
        return null
    }

    private fun aplicarFiltroDeporte(deporte: String) {
        for (marker in markers) {
            val deporteMarcador = obtenerDeporteMarcador(marker)
            if (deporte == "Todos" || deporte == deporteMarcador) {
                // Si el deporte coincide o se selecciona "Todos", muestra el marcador
                if (!mapView.overlays.contains(marker)) {
                    mapView.overlays.add(marker)
                }
            } else {
                // Si el deporte no coincide, oculta el marcador
                if (mapView.overlays.contains(marker)) {
                    mapView.overlays.remove(marker)
                }
            }
        }
        mapView.invalidate() // Solicita la actualización del mapa
    }


    private fun obtenerDeporteMarcador(marker: Marker): String {
        // Obtén el título del marcador
        val titulo = marker.title

        // Busca el deporte correspondiente en el mapa de marcadoresDeportes
        return marcadoresDeportes[titulo] ?: "Desconocido"
    }

    private fun parsearJSON() {
        // Obtener referencia a la base de datos de Firebase
        val database = Firebase.database
        val referenciaCanchas = database.reference.child("canchas")

        // Escuchar cambios en la base de datos
        referenciaCanchas.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Limpiar el mapa antes de agregar nuevos marcadores
                // (puedes implementar esta lógica según tus necesidades)

                // Iterar sobre los datos de Firebase
                for (canchaSnapshot in dataSnapshot.children) {
                    val lugar: String = (canchaSnapshot.child("lugar").getValue(String::class.java)) ?: ""
                    val nombreCancha: String =
                        (canchaSnapshot.child("nombre_cancha").getValue(String::class.java)) ?: ""
                    val dueno: String = (canchaSnapshot.child("dueno").getValue(String::class.java)) ?: ""
                    val deporte: String =
                        (canchaSnapshot.child("deporte").getValue(String::class.java)) ?: "default_value"

                    // Dentro del bucle que recorre las canchas desde el JSON
                    val geoPoint = buscarDireccion(lugar)
                    if (geoPoint != null) {
                        if (deporte.equals("Futbol")) {
                            showMarker(geoPoint, "Cancha Fútbol", deporte)
                        } else if (deporte.equals("Tenis")) {
                            showMarker(geoPoint, "Cancha Tenis", deporte)

                        } else if (deporte.equals("Americano")) {
                            showMarker(geoPoint, "Cancha Americano", deporte)

                        } else if (deporte.equals("Voleibol")) {
                            showMarker(geoPoint, "Cancha Voleibol", deporte)

                        } else if (deporte.equals("Baloncesto")) {
                            showMarker(geoPoint, "Cancha Baloncesto", deporte)

                        } else if (deporte.equals("Beisbol")) {
                            showMarker(geoPoint, "Cancha Beisbol", deporte)

                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", "Error in database operation:", databaseError.toException())
            }
        })
    }


    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }


    companion object {
        const val LOCALIZACION = 1
    }
}


