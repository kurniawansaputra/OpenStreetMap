package com.kurniawan.openstreetmap

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kurniawan.openstreetmap.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setListener()
    }

    private fun setListener() {
        binding.apply {
            buttonMarker.setOnClickListener {
                startActivity(Intent(this@MainActivity, MarkerActivity::class.java))
            }

            buttonLocationPicker.setOnClickListener {
                val moveIntent = Intent(this@MainActivity, LocationPickerActivity::class.java)
                resultLatLng.launch(moveIntent)
            }
        }
    }

    private val resultLatLng =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val lat = data?.getDoubleExtra("lat", 0.0)
                val lng = data?.getDoubleExtra("lng", 0.0)

                if (lat != null && lng != null) {
                    binding.textLatLng.text = "Latitude: $lat, Longitude: $lng"
                }
            }
        }
}