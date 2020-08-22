package br.net.easify.tracker.view.main

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import br.net.easify.tracker.R
import br.net.easify.tracker.view.fragments.GalleryFragment
import br.net.easify.tracker.view.fragments.HistoryFragment
import br.net.easify.tracker.view.fragments.HomeFragment
import br.net.easify.tracker.view.fragments.SettingsFragment
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

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

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
