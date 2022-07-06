package com.example.mysampleproject.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.mysampleproject.R
import com.example.mysampleproject.databinding.FragmentFirstBinding
import com.example.mysampleproject.util.LocationUpdateService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), OnMapReadyCallback {

    companion object {
        var PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private lateinit var firsFragmentViewModel: FirstFragmentViewModel

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var _binding: FragmentFirstBinding? = null

    private lateinit var mGmap: GoogleMap

    private val locationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val locationIntent = intent.getBundleExtra("Location")
            val lastKnownLoc = locationIntent?.getParcelable("Location") as Location?
            firsFragmentViewModel.getDistanceFromStartingPoint(lastKnownLoc)
        }
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firsFragmentViewModel = ViewModelProvider(this).get(FirstFragmentViewModel::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            locationReceiver, IntentFilter("GPSLocationUpdates")
        )
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firsFragmentViewModel.userDistance.observe(viewLifecycleOwner) {
            showPopupNotification(it)
        }

        binding.locateMeFab.setOnClickListener {
            checkUserPermission()
        }
    }

    private fun showPopupNotification(message: String?) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage(message)
            .setPositiveButton(R.string.user_notification_dialog_button
            ) { dialog, id ->
                dialog.dismiss()
            }
        builder.create()
        builder.show()
    }

    private fun checkUserPermission() {
        if (hasPermissions(this@FirstFragment.requireContext(), PERMISSIONS)) {
            getCurrentLocation()
        } else {
            permReqLauncher.launch(PERMISSIONS)
        }
    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value == true
            }
            if (granted) {
                getCurrentLocation()
            }
        }

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        requireActivity().startService(
            Intent(
                requireActivity().baseContext,
                LocationUpdateService::class.java
            )
        )
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location.let {
                    val myLocation = LatLng(location?.latitude ?: 0.0, location?.longitude
                        ?: 0.0)
                    mGmap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
                    mGmap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 11.0f))
                    val marker = MarkerOptions()
                        .position(myLocation).title("Starting Location")
                    mGmap.addMarker(marker)?.tag = it
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().stopService(
            Intent(
                requireActivity().baseContext,
                LocationUpdateService::class.java
            )
        )
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGmap = googleMap
    }
}