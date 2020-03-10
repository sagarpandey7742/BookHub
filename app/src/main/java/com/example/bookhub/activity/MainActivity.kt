package com.example.bookhub.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.bookhub.R
import com.example.bookhub.fragment.About_app_fragment
import com.example.bookhub.fragment.Dashboard_fragment
import com.example.bookhub.fragment.Favourite_fragment
import com.example.bookhub.fragment.Profile_fragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout : DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var frame: FrameLayout
    lateinit var navigationView: NavigationView

    var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frame = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)

        setUpToolbar()


        val actionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        openDashboard()

        navigationView.setNavigationItemSelectedListener {

            if(previousMenuItem != null){
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when(it.itemId){
                R.id.dashboard -> {
                    openDashboard()
                    drawerLayout.closeDrawers()
                }

                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, Profile_fragment())
                        .addToBackStack("Profile")
                        .commit()
                    supportActionBar?.title = "Profile"
                    drawerLayout.closeDrawers()                }

                R.id.aboutApp -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, About_app_fragment())
                        .addToBackStack("About App")
                        .commit()
                    supportActionBar?.title = "About App"
                    drawerLayout.closeDrawers()                }

                R.id.favourites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, Favourite_fragment())
                        .addToBackStack("Favourites")
                        .commit()
                    supportActionBar?.title = "Favourites"
                    drawerLayout.closeDrawers()                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId
        if(id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openDashboard(){
        val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, Dashboard_fragment())
            navigationView.setCheckedItem(R.id.dashboard)
            //transaction.addToBackStack("Dashboard")
            transaction.commit()
            supportActionBar?.title = "Dashboard"

    }

//    override fun onBackPressed() {
//        when(supportFragmentManager.findFragmentById(R.id.frame)){
//            !is Dashboard_fragment -> openDashboard()
//            else -> super.onBackPressed()
//        }
//    }
}
