package ru.bikbulatov.comeWithMe.core.geoLocation

import android.location.Location
import com.google.android.gms.tasks.OnCompleteListener

interface GeoLocationListener {
    fun getLocation(callback: OnCompleteListener<Location?>)
    fun getNewLocation()
}