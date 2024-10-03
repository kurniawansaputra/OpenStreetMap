package com.kurniawan.openstreetmap

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kurniawan.openstreetmap.databinding.ActivityLocationPickerBinding
import com.kurniawan.openstreetmap.databinding.ActivityMarkerBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class LocationPickerActivity : AppCompatActivity() {
    private lateinit var map: MapView
    private lateinit var gestureDetector: GestureDetector
    private var selectedLocation: GeoPoint? = null

    private lateinit var binding: ActivityLocationPickerBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLocationPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setToolbar()


        // Load configuration for osmdroid
        Configuration.getInstance().load(applicationContext, androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext))

        // Initialize the MapView
        map = findViewById(R.id.mapView)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        // Set initial zoom level
        map.controller.setZoom(15.0)

        // Set the initial map center to Kebumen
        val kebumenPoint = GeoPoint(-7.679303, 109.667380)
        map.controller.setCenter(kebumenPoint)

        // Gesture detector for single taps
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                e.let {
                    val projection = map.projection
                    val geoPoint = projection.fromPixels(e.x.toInt(), e.y.toInt()) as GeoPoint
                    updateSelectedLocation(geoPoint)
                }
                return true
            }
        })

        // Set up a listener for map touches but keep zoom and pan enabled
        map.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false // Return false to allow the map to process zoom/pan gestures
        }

        setConfirmListener()
    }

    private fun setToolbar() {
        binding.apply {
            topAppBar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun updateSelectedLocation(geoPoint: GeoPoint) {
        // Update the TextView with the selected coordinates
//        binding.textLatLng.text = "Lat: ${geoPoint.latitude}, Lng: ${geoPoint.longitude}"

        // Optionally, set a custom marker at the selected location
        val marker = Marker(map)
        marker.position = geoPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        // Load and resize the custom marker drawable
        val drawable = ContextCompat.getDrawable(this, R.drawable.location)
        drawable?.let {
            val bitmap = (it as BitmapDrawable).bitmap
            // Resize the bitmap
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, false)
            // Set the resized bitmap as the marker icon
            marker.icon = BitmapDrawable(resources, resizedBitmap)
        }

        map.overlays.clear() // Clear previous markers if any
        map.overlays.add(marker)
        map.invalidate() // Redraw the map to show the marker

        selectedLocation = geoPoint
    }

    private fun setConfirmListener() {
        // Set the Confirm button listener
        binding.buttonConfirm.setOnClickListener {
            if (selectedLocation != null) {
                // Location selected, pass data back to MainActivity
                val intent = Intent()
                intent.putExtra("lat", selectedLocation?.latitude)
                intent.putExtra("lng", selectedLocation?.longitude)
                setResult(Activity.RESULT_OK, intent)
                finish() // Finish the activity and return to the MainActivity
            } else {
                // No location selected, show a toast message
                Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}