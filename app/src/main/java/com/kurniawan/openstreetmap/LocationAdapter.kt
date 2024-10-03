package com.kurniawan.openstreetmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kurniawan.openstreetmap.databinding.LayoutBottomSheetLocationBinding
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class LocationAdapter(
    private val mapView: MapView,
    private val context: Context,
    private val locationList: List<Location>
) {
    fun addMarkers() {
        for (location in locationList) {
            val point = GeoPoint(location.latitude, location.longitude)
            val marker = Marker(mapView)
            marker.position = point
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = location.name

            // Get the original drawable
            val drawable = ContextCompat.getDrawable(context, R.drawable.custom_marker)

            // Resize the drawable
            val resizedDrawable = drawable?.let {
                val bitmap = (it as BitmapDrawable).bitmap
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, false) // Adjust the width and height here
                BitmapDrawable(context.resources, resizedBitmap)
            }

            // Set resized marker icon
            marker.icon = resizedDrawable

            // Set a click listener for each marker
            marker.setOnMarkerClickListener { _, _ ->
                showBottomSheet(location.name)
                true
            }

            // Add the marker to the map's overlays
            mapView.overlays.add(marker)
        }
    }

    private fun showBottomSheet(title: String) {
        val dialog = BottomSheetDialog(context)
        val bindingBottomSheet = LayoutBottomSheetLocationBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(bindingBottomSheet.root)
        bindingBottomSheet.apply {

            labelDescription.text = title
            dialog.setCancelable(true)
            dialog.show()
        }
    }
}
