package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import timber.log.Timber
import java.util.Locale

const val KEY_MOTIONSTATE = "key_motionState"

class DetailFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel: RepresentativeViewModel by lazy {
        ViewModelProvider(this)[RepresentativeViewModel::class.java]
    }

    private lateinit var binding: FragmentRepresentativeBinding

    private val respresentativeAdapter = RepresentativeListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.recyclerRepresentatives.adapter = respresentativeAdapter

        viewModel.representatives.observe(viewLifecycleOwner)
        {
            respresentativeAdapter.submitList(it)
        }

        viewModel.showToast.observe(viewLifecycleOwner)
        {
            it?.let {
                Toast.makeText(
                    requireContext(),
                    it,
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.onToastShown()
            }
        }

        binding.state.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                viewModel.setState(requireContext().resources.getStringArray(R.array.states)[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.buttonLocation.setOnClickListener {
            Timber.i("Use Location button clicked")
            getLocation()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (savedInstanceState != null) {
            val state = savedInstanceState.getInt(KEY_MOTIONSTATE)
            binding.layoutMotion.transitionToState(state)
        }

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.run {
            putInt(KEY_MOTIONSTATE, binding.layoutMotion.currentState)
        }
    }

    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                getLocation()
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                Toast.makeText(
                    requireContext(),
                    R.string.permission_fine_missing,
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {
                // No location access granted.
            }
        }
    }


    private fun checkAndRequestLocationPermissions(): Boolean {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                return true
            }

            shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.permission_title)
                    .setMessage(R.string.permission_description)
                    .setPositiveButton("OK") { _, _ ->
                        locationPermissionRequest.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                    .setCancelable(true)
                    .create()
                    .show()
                return false
            }

            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                return false
            }
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        Timber.i("getLocation called")
        if (!checkAndRequestLocationPermissions()) {
            Timber.i("not granted")
            /*Toast.makeText(requireContext(), R.string.error_no_permission, Toast.LENGTH_SHORT)
                .show()*/
            return
        }
        if (!viewModel.isConnected(requireContext())) {
            Toast.makeText(requireContext(), R.string.error_no_connection, Toast.LENGTH_SHORT)
                .show()
            return
        }

        Timber.i("start getting location")

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                Timber.v("LastLocation listener called")
                location?.let {
                    Timber.v("got proper location")
                    val addr = geoCodeLocation(location)
                    addr?.let {
                        Timber.v("got proper address")
                        viewModel.address.value = it
                    }
                }
            }
    }

    private fun geoCodeLocation(location: Location): Address? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            ?.map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            ?.first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}