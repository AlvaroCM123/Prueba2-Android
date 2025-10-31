package com.example.pruebaandroid2

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

data class Lugar(
    val nombre: String,
    val latitud: Double,
    val longitud: Double,
    val iconoResId: Int,
    val categoria: String
)

class MapActivity : AppCompatActivity(){
    private lateinit var mapView: MapView
    private lateinit var locationOverlay: MyLocationNewOverlay
    private val todosLosLugares = listOf(
        Lugar("Cerro San Cristóbal", -33.4246, -70.6346, R.drawable.ic_park, "Parque"),
        Lugar("Parque Bicentenario", -33.40056, -70.60222, R.drawable.ic_park, "Parque"),
        Lugar("Parque Forestal", -33.4349, -70.6416, R.drawable.ic_park, "Parque"),
        Lugar("Museo de Bellas Artes", -33.4357, -70.6397, R.drawable.ic_museum, "Museo"),
        Lugar("Museo Histórico Nacional",-33.4374, -70.6511, R.drawable.ic_museum, "Museo"),
        Lugar("La Chascona (Casa Neruda)", -33.43116, -70.63448, R.drawable.ic_museum, "Cultura"),
        Lugar("Centro Cultural GAM", -33.4395, -70.6398, R.drawable.ic_museum, "Cultura"),
        Lugar("Costanera Center", -33.4170, -70.6068, R.drawable.ic_mall, "Tienda"),
        Lugar("Patio Bellavista", -33.43417, -70.63506, R.drawable.ic_mall, "Tienda"),
        Lugar("Estadio Nacional", -33.4631, -70.6106, R.drawable.ic_stadium, "Deporte"),
        Lugar("Estadio Monumental David Arellano", -33.50648, -70.60598, R.drawable.ic_stadium, "Deporte"),
        Lugar("Estadio Bicentenario de La Florida",-33.5378, -70.5737, R.drawable.ic_stadium, "Deporte"),
        Lugar("Estadio Santa Laura", -33.4027, -70.6554, R.drawable.ic_stadium, "Deporte"),
        Lugar("Estadio San Carlos de Apoquindo",-33.3909, -70.5004, R.drawable.ic_stadium, "Deporte"),
        Lugar("Fantasilandia", -33.4603, -70.6628, R.drawable.ic_atraccions, "Entretención"),
        Lugar("Templo Bahá'í", -33.47222, -70.50917, R.drawable.ic_tourism, "Turismo"),
        Lugar("Palacio de La Moneda", -33.4430, -70.6533, R.drawable.ic_tourism, "Turismo")
    )
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                activarCapadeUbicacion()
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.activity_map)
        mapView = findViewById(R.id.map)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(14.0)

        val categorias = listOf("Parque","Tienda","Museo","Deporte","Entretención","Turismo")
        val adapter = ArrayAdapter(this,R.layout.spinner_item_custom, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner: Spinner = findViewById(R.id.filtromapa)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val categoriaSeleccionada = parent?.getItemAtPosition(position).toString()
                filtrarMarcadores(categoriaSeleccionada)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        filtrarMarcadores("Todos")

        when{
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                activarCapadeUbicacion()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
    private fun activarCapadeUbicacion() {
        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this),mapView)
        locationOverlay.enableMyLocation()
        mapView.overlays.add(locationOverlay)

        locationOverlay.runOnFirstFix {
            runOnUiThread {
                mapView.controller.animateTo(locationOverlay.myLocation)
                mapView.controller.setZoom(18.0)
            }
        }
    }
    private fun filtrarMarcadores(categoria: String) {
        mapView.overlays.removeAll {it is Marker}
        val lugaresMostrar = if (categoria == "Todos") {
            todosLosLugares
        }else{
            todosLosLugares.filter { it.categoria == categoria}
        }
        for (lugar in lugaresMostrar) {
            val marker = Marker(mapView)
            marker.position = GeoPoint(lugar.latitud, lugar.longitud)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = lugar.nombre
            marker.icon = ContextCompat.getDrawable(this, lugar.iconoResId)
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}
