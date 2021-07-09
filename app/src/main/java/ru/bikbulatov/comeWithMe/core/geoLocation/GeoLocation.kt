package ru.bikbulatov.comeWithMe.core.geoLocation

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener

class GeoLocation(val activity: Activity) {
    private var fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)
    lateinit var locationRequest: LocationRequest

    fun getLastLocation(onCompleteListener: OnCompleteListener<Location?>) {
        if (PermissionManager.checkPermission(activity)) {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener(onCompleteListener)
        } else
            PermissionManager.getPermissions(activity)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation = p0.lastLocation
            Log.d("test9999FromCallback", "location latitude ${lastLocation.latitude}")
            Log.d("test9999FromCallback", "location longitude ${lastLocation.longitude}")
        }
    }

    @SuppressLint("MissingPermission")
    fun getNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.numUpdates = 2
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

}