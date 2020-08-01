package br.net.easify.tracker.view.main

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import br.net.easify.tracker.R
import br.net.easify.tracker.background.services.LocationService
import br.net.easify.tracker.utils.ServiceHelper
import br.net.easify.tracker.view.fragments.GalleryFragment
import br.net.easify.tracker.view.fragments.HistoryFragment
import br.net.easify.tracker.view.fragments.HomeFragment
import br.net.easify.tracker.view.fragments.SettingsFragment
import br.net.easify.tracker.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.config.Configuration


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    private lateinit var homeFragment: HomeFragment
    private lateinit var galleryFragment: GalleryFragment
    private lateinit var historyFragment: HistoryFragment
    private lateinit var settingsFragment: SettingsFragment

    external fun stringFromJNI(): String

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance()
            .load(
                applicationContext,
                PreferenceManager.getDefaultSharedPreferences(applicationContext)
            )
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        homeFragment = HomeFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, homeFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

        botomAppBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuHome -> {
                    homeFragment = HomeFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, homeFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.menuHistory -> {
                    historyFragment = HistoryFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, historyFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.menuGallery -> {
                    galleryFragment = GalleryFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, galleryFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.menuSettings -> {
                    settingsFragment = SettingsFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, settingsFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
            }

            true
        }

        val gpsService = LocationService()
        val gpsIntent = Intent(applicationContext, gpsService::class.java)
        if (!ServiceHelper(applicationContext).isMyServiceRunning(gpsService::class.java)) {
            startService(gpsIntent)
        }
    }


}
