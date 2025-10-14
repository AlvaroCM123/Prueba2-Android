package com.example.pruebaandroid2

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainActivity : AppCompatActivity(){
    private lateinit var mapView: MapView
    private lateinit var locationOverlay: MyLocationNewOverlay
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                activarCapadeUbicacion()
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.map)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)


        val mapController = mapView.controller
        mapController.setZoom(16.0)
        val startPoint = GeoPoint(-33.4430, -70.6533)
        mapController.setCenter(startPoint)

        val startMarker = Marker(mapView)
        startMarker.position = startPoint
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.title = "Palacio de La Moneda"
        startMarker.snippet = "Sede del Presidente de la República de Chile"
        startMarker.subDescription = "Edificio histórico y gubernamental"
        mapView.overlays.add(startMarker)
        mapView.invalidate()

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
    override fun onResume() {
            super.onResume()
            mapView.onResume()
        }

    override fun onPause() {
            super.onPause()
            mapView.onPause()
        }
    }
