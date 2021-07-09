package ru.bikbulatov.comeWithMe.core.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import dagger.hilt.android.AndroidEntryPoint
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.authorization.ui.AuthActivity
import ru.bikbulatov.comeWithMe.core.domain.ViewPagerNavigator
import ru.bikbulatov.comeWithMe.core.geoLocation.GeoLocation
import ru.bikbulatov.comeWithMe.core.geoLocation.GeoLocationListener
import ru.bikbulatov.comeWithMe.core.network.TokenRepository
import ru.bikbulatov.comeWithMe.createEvent.ui.FragmentCreateEvent
import ru.bikbulatov.comeWithMe.databinding.ActivityMainBinding
import ru.bikbulatov.comeWithMe.events.ui.FragmentEvents
import ru.bikbulatov.comeWithMe.plans.ui.FragmentPlans
import ru.bikbulatov.comeWithMe.profile.ui.FragmentProfile
import ru.bikbulatov.comeWithMe.search.ui.FragmentSearch
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), GeoLocationListener, ViewPagerNavigator {
    private lateinit var binding: ActivityMainBinding
    private lateinit var geoLocation: GeoLocation
    private var backPressedTime: Long = 0

    companion object {
        const val vpEventsId = 0
        const val vpPlansId = 1
        const val vpCreateEventId = 2
        const val vpSearchId = 3
        const val vpProfileId = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        configureViewPager()
        configureBottomNavigation()
        geoLocation = GeoLocation(this)
        if (TokenRepository.accessToken.isEmpty())
            openAuthorization()
    }

    private fun openAuthorization() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1000)
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Log.d("Debug:", "You have the permission")
            else {
                val toastMessage = Toast.makeText(
                    this,
                    "Пожалуйста включите доступ к геолокации",
                    Toast.LENGTH_LONG
                )
                toastMessage.show()
            }
    }

    private fun configureViewPager() {
        val adapter = ViewPagerAdapter(this)
        adapter.addFrag(FragmentEvents())
        adapter.addFrag(FragmentPlans())
        adapter.addFrag(FragmentCreateEvent())
        adapter.addFrag(FragmentSearch())
        adapter.addFrag(FragmentProfile())
        binding.vpMain.isUserInputEnabled = false
        binding.vpMain.adapter = adapter
        binding.vpMain.adapter?.notifyDataSetChanged()
    }

    private fun configureBottomNavigation() {
        binding.bnvMain.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.event -> binding.vpMain.currentItem = vpEventsId
                R.id.plans -> binding.vpMain.currentItem = vpPlansId
                R.id.create -> binding.vpMain.currentItem = vpCreateEventId
//                R.id.search -> binding.vpMain.currentItem = vpSearchId
                R.id.profile -> binding.vpMain.currentItem = vpProfileId
            }
            true
        }
    }


    override fun onBackPressed() {
        val countFrag = supportFragmentManager.backStackEntryCount
        val countChild = 0

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            exitProcess(0)
        } else {
//            Toast.makeText(this, "Нажмите еще раз для выхода", Toast.LENGTH_SHORT).show()
            backPressedTime = System.currentTimeMillis()
        }

        if (countChild > 0) {
            for (str in supportFragmentManager.fragments) {
                if (str.childFragmentManager.backStackEntryCount >= 0) {
                    str.childFragmentManager.popBackStack()
                }
            }
        } else if (countFrag > 0) {
            supportFragmentManager.popBackStack()
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun getLocation(callback: OnCompleteListener<Location?>) {
        if (isLocationEnabled()) {
            geoLocation.getLastLocation(callback)
        } else {
            Toast.makeText(
                this,
                "Please enable your location service",
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    override fun getNewLocation() {
        geoLocation.getNewLocation()
    }

    override fun openEventsScreen() {
        binding.vpMain.currentItem = vpEventsId
        binding.bnvMain.selectedItemId = R.id.event
    }

    override fun openPlansScreen() {
        binding.vpMain.currentItem = vpPlansId
        binding.bnvMain.selectedItemId = R.id.plans
    }

    override fun openCreateEventScreen() {
        binding.vpMain.currentItem = vpCreateEventId
        binding.bnvMain.selectedItemId = R.id.create
    }

    override fun openSearchScreen() {
//        binding.vpMain.currentItem = vpSearchId
//        binding.bnvMain.selectedItemId = R.id.search
    }

    override fun openProfileScreen() {
        binding.vpMain.currentItem = vpProfileId
        binding.bnvMain.selectedItemId = R.id.profile
    }
}