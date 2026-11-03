package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etEmail = view.findViewById<EditText>(R.id.etProfileEmail)
        val switchNotifications = view.findViewById<Switch>(R.id.switchProfileNotifications)
        val switchSellerMode = view.findViewById<Switch>(R.id.switchSellerMode)
        val btnSave = view.findViewById<Button>(R.id.btnSaveProfile)

        val sharedPref = requireActivity().getSharedPreferences("KasiCraftsPrefs", Context.MODE_PRIVATE)
        
        // Load values
        val savedEmail = FirebaseAuth.getInstance().currentUser?.email.orEmpty()
        etEmail.setText(savedEmail)
        etEmail.isEnabled = false
        val nameView = view.findViewById<TextView>(R.id.tvAccountName)
        val trustView = view.findViewById<TextView>(R.id.tvTrustStatus)
        val pointsView = view.findViewById<TextView>(R.id.tvProfileLoyalty)
        val progress = view.findViewById<ProgressBar>(R.id.pbLoyalty)
        nameView.text = savedEmail.substringBefore("@").ifBlank { "Guest" }
        trustView.text = "Artisan verification: not verified"
        pointsView.text = "Loyalty points: unavailable"
        progress.progress = 0
        FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
            FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener { profile ->
                    if (!isAdded) return@addOnSuccessListener
                    nameView.text = profile.getString("name") ?: nameView.text
                    val points = (profile.getLong("loyaltyPoints") ?: 0L).coerceAtLeast(0L)
                    pointsView.text = "$points loyalty points"
                    progress.progress = (points % 1000L).toInt()
                    trustView.text = if (profile.getBoolean("artisanVerified") == true)
                        "Artisan verification: verified"
                    else "Artisan verification: not verified"
                }
        }
        switchNotifications.isChecked = sharedPref.getBoolean("profile_notifications", true)
        switchSellerMode.isChecked = sharedPref.getBoolean("seller_mode", false)

        btnSave.setOnClickListener {
            val email = etEmail.text.toString()
            val notificationsEnabled = switchNotifications.isChecked
            val sellerModeEnabled = switchSellerMode.isChecked

            with(sharedPref.edit()) {
                putString("user_email", savedEmail)
                putBoolean("profile_notifications", notificationsEnabled)
                putBoolean("seller_mode", sellerModeEnabled)
                apply()
            }

            Toast.makeText(requireContext(), "Profile Saved!", Toast.LENGTH_SHORT).show()
        }
    }
}