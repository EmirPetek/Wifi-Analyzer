package com.wifianalyzer.wifianalyzerproject.ui.fragment.locationInfo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.wifianalyzer.wifianalyzerproject.R
import com.wifianalyzer.wifianalyzerproject.databinding.FragmentCurrentLocationBinding


class CurrentLocationFragment : Fragment() {

    private lateinit var locationManager: LocationManager
    private lateinit var binding: FragmentCurrentLocationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrentLocationBinding.inflate(inflater,container,false)

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        binding.imageViewCurrentLocationBackButton.setOnClickListener { findNavController().popBackStack() }

        // Konum izni kontrolü ve isteme
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            // Konum güncellemelerini başlat
            startLocationUpdates()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun startLocationUpdates() {
        try {
            // Hem GPS hem de Ağ sağlayıcıları kullanarak konum al
            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (isGPSEnabled || isNetworkEnabled) {
                val locationListener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        // Enlem ve boylam bilgilerini güncelle
                        val latitude = location.latitude
                        val longitude = location.longitude
                        binding.textViewLocationLatitude.text = "Enlem: $latitude"
                        binding.textViewLocationLongitude.text = "Boylam: $longitude"
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {
                        Toast.makeText(requireContext(), "Konum sağlayıcı devre dışı", Toast.LENGTH_SHORT).show()
                    }
                }

                // GPS üzerinden konum alma
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000L,  // 5 saniyede bir güncelle
                        5f,    // 10 metre hareket ettiğinde
                        locationListener,
                        Looper.getMainLooper()
                    )
                }

                // Ağ üzerinden konum alma
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        1000L,
                        5f,
                        locationListener,
                        Looper.getMainLooper()
                    )
                }
            } else {
                Toast.makeText(requireContext(), "Konum sağlayıcı devre dışı", Toast.LENGTH_SHORT).show()
            }
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        }
    }

    // İzinlerin sonucunu kontrol et
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // İzin verildiyse konum güncellemelerini başlat
            startLocationUpdates()
        }
    }


}