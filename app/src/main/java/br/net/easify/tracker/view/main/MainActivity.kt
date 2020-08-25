package br.net.easify.tracker.view.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import br.net.easify.tracker.R
import br.net.easify.tracker.repositories.api.interceptor.AuthInterceptor
import br.net.easify.tracker.repositories.database.model.SqliteUser
import br.net.easify.tracker.view.fragments.GalleryFragment
import br.net.easify.tracker.view.fragments.HistoryFragment
import br.net.easify.tracker.view.fragments.HomeFragment
import br.net.easify.tracker.view.fragments.SettingsFragment
import br.net.easify.tracker.view.login.LoginActivity
import br.net.easify.tracker.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.config.Configuration


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    private var homeFragment: HomeFragment = HomeFragment()
    private var galleryFragment: GalleryFragment = GalleryFragment()
    private var historyFragment: HistoryFragment = HistoryFragment()
    private var settingsFragment: SettingsFragment = SettingsFragment()

    external fun stringFromJNI(): String

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.startLocationService()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, homeFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

        botomAppBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuHome -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, homeFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.menuHistory -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, historyFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.menuGallery -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, galleryFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.menuSettings -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, settingsFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
            }

            true
        }
    }
}
