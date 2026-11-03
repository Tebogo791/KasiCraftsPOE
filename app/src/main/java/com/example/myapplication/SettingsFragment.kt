package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cvAvatar = view.findViewById<View>(R.id.cvAvatarContainer)
        val tvInitials = view.findViewById<TextView>(R.id.tvProfileInitials)
        val ivAvatar = view.findViewById<ImageView>(R.id.ivProfileAvatar)
        val tvName = view.findViewById<TextView>(R.id.tvProfileName)

        val switchNotif = view.findViewById<Switch>(R.id.switchNotifications)
        val switchNearMe = view.findViewById<Switch>(R.id.switchNearMe)
        val switchOffline = view.findViewById<Switch>(R.id.switchOfflineMode)
        val btnSave = view.findViewById<Button>(R.id.btnSaveSettings)
        val btnSignOut = view.findViewById<Button>(R.id.btnSignOut)

        val sharedPref = requireActivity().getSharedPreferences("KasiCraftsPrefs", Context.MODE_PRIVATE)

        // Register modern system photo picker contract
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                ivAvatar.setImageURI(uri)
                ivAvatar.visibility = View.VISIBLE
                tvInitials.visibility = View.GONE
                
                // Save picked URI string for persistence
                sharedPref.edit().putString("profile_avatar_uri", uri.toString()).apply()
                Toast.makeText(requireContext(), "Profile picture updated!", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener on avatar circle container to add custom picture
        cvAvatar.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // Load persisted avatar image if available
        val savedAvatarUri = sharedPref.getString("profile_avatar_uri", "") ?: ""
        if (savedAvatarUri.isNotEmpty()) {
            try {
                ivAvatar.setImageURI(android.net.Uri.parse(savedAvatarUri))
                ivAvatar.visibility = View.VISIBLE
                tvInitials.visibility = View.GONE
            } catch (e: Exception) {
                // Fallback to text initials if uri cannot be loaded
            }
        }

        // Load dynamic session information
        val savedEmail = FirebaseAuth.getInstance().currentUser?.email.orEmpty()
        
        if (savedEmail.isNotEmpty()) {
            // Extract display name from email prefix
            val namePrefix = savedEmail.substringBefore("@")
            val cleanName = namePrefix.replace(".", " ")
                .split(" ")
                .joinToString(" ") { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.ROOT) else it.toString() } }
            
            tvName.text = cleanName

            // Extract 1 or 2 letter initials
            val initials = namePrefix.split(".", "_", "-")
                .filter { it.isNotEmpty() }
                .take(2)
                .joinToString("") { it.take(1).uppercase(Locale.ROOT) }
            
            if (initials.isNotEmpty() && savedAvatarUri.isEmpty()) {
                tvInitials.text = initials
            } else if (savedAvatarUri.isEmpty()) {
                tvInitials.text = "KC"
            }
        } else {
            tvName.text = "Guest User"
            if (savedAvatarUri.isEmpty()) {
                tvInitials.text = "GU"
            }
        }

        // Configure switch states
        switchNotif.isChecked = sharedPref.getBoolean("notifications_enabled", true)
        switchNearMe.isChecked = sharedPref.getBoolean("near_me_enabled", true)
        switchOffline.isChecked = sharedPref.getBoolean("offline_mode_enabled", false)

        btnSave.setOnClickListener {
            with (sharedPref.edit()) {
                putBoolean("notifications_enabled", switchNotif.isChecked)
                putBoolean("near_me_enabled", switchNearMe.isChecked)
                putBoolean("offline_mode_enabled", switchOffline.isChecked)
                apply()
            }

            Toast.makeText(requireContext(), "Profile Preferences Saved!", Toast.LENGTH_SHORT).show()
        }

        btnSignOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            // Clear legacy prototype session flags
            with (sharedPref.edit()) {
                putBoolean("is_logged_in", false)
                putString("user_email", "")
                putString("profile_avatar_uri", "") // Clear custom picture on logout
                apply()
            }

            Toast.makeText(requireContext(), "Signed out successfully", Toast.LENGTH_SHORT).show()
            
            // Navigate back to login destination
            findNavController().navigate(R.id.loginFragment)
        }
    }
}