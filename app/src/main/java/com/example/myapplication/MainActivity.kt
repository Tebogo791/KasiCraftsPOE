package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val controller = navHost.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(controller)
        controller.addOnDestinationChangedListener { _, destination, _ ->
            bottomNav.visibility = if (destination.id == R.id.loginFragment || destination.id == R.id.registerFragment) View.GONE else View.VISIBLE
        }
        if (savedInstanceState == null) {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null && user.isEmailVerified) {
                controller.navigate(R.id.homeFragment)
            } else {
                FirebaseAuth.getInstance().signOut()
            }
        }
    }
}
