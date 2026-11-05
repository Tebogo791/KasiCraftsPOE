package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

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
        val switchDarkMode = view.findViewById<Switch>(R.id.switchDarkMode)
        val btnSave = view.findViewById<Button>(R.id.btnSaveSettings)

        val languageSpinner = view.findViewById<Spinner>(R.id.spinnerLanguage)
        val languages = listOf("English", "isiZulu", "Afrikaans")
        val tags = listOf("en", "zu", "af")
        languageSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, languages)
        val activeTag = AppCompatDelegate.getApplicationLocales().toLanguageTags().substringBefore("-")
        languageSpinner.setSelection(tags.indexOf(activeTag).takeIf { it >= 0 } ?: 0)
        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, selectedView: View?, position: Int, id: Long) {
                val newTag = tags[position]
                val current = AppCompatDelegate.getApplicationLocales().toLanguageTags().substringBefore("-").ifEmpty { "en" }
                if (newTag != current) AppCompatDelegate.setApplicationLocales(androidx.core.os.LocaleListCompat.forLanguageTags(newTag))
            }
        }

        val loyaltyText = view.findViewById<TextView>(R.id.tvLoyaltyPoints)
        val loyaltyProgress = view.findViewById<android.widget.ProgressBar>(R.id.pbLoyalty)
        loyaltyProgress.max = 1000
        loyaltyProgress.progress = 0
        loyaltyText.text = "Loyalty points: unavailable"

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.uid?.let { uid ->
            FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (!isAdded) return@addOnSuccessListener
                    val points = (doc.getLong("loyaltyPoints") ?: 0L).coerceAtLeast(0L)
                    loyaltyProgress.progress = (points % 1000L).toInt()
                    loyaltyText.text = "$points points earned; ${1000 - points % 1000} to next milestone"
                    doc.getString("name")?.let { dbName ->
                        if (dbName.isNotBlank()) tvName.text = dbName
                    }
                }
        }

        val btnSignOut = view.findViewById<Button>(R.id.btnSignOut)
        val sharedPref = requireActivity().getSharedPreferences("KasiCraftsPrefs", Context.MODE_PRIVATE)

        // Photo picker contract
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                ivAvatar.setImageURI(uri)
                ivAvatar.visibility = View.VISIBLE
                tvInitials.visibility = View.GONE
                sharedPref.edit().putString("profile_avatar_uri", uri.toString()).apply()
                Toast.makeText(requireContext(), "Profile picture updated!", Toast.LENGTH_SHORT).show()
            }
        }

        cvAvatar.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // Load avatar image
        val savedAvatarUri = sharedPref.getString("profile_avatar_uri", "") ?: ""
        if (savedAvatarUri.isNotEmpty()) {
            try {
                ivAvatar.setImageURI(android.net.Uri.parse(savedAvatarUri))
                ivAvatar.visibility = View.VISIBLE
                tvInitials.visibility = View.GONE
            } catch (e: Exception) {
                // Fallback to text initials
            }
        }

        // Display user name and initials
        val savedEmail = currentUser?.email.orEmpty()
        val displayName = currentUser?.displayName
        if (!displayName.isNullOrBlank()) {
            tvName.text = displayName
            val initials = displayName.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
            if (savedAvatarUri.isEmpty()) tvInitials.text = initials.ifBlank { "KC" }
        } else if (savedEmail.isNotEmpty()) {
            val namePrefix = savedEmail.substringBefore("@")
            val cleanName = namePrefix.replace(".", " ")
                .split(" ")
                .joinToString(" ") { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.ROOT) else it.toString() } }
            tvName.text = cleanName
            val initials = namePrefix.split(".", "_", "-")
                .filter { it.isNotEmpty() }
                .take(2)
                .joinToString("") { it.take(1).uppercase(Locale.ROOT) }
            if (savedAvatarUri.isEmpty()) tvInitials.text = initials.ifBlank { "KC" }
        } else {
            tvName.text = "Guest User"
            if (savedAvatarUri.isEmpty()) tvInitials.text = "GU"
        }

        // Configure switch states
        switchNotif.isChecked = sharedPref.getBoolean("notifications_enabled", true)
        switchNearMe.isChecked = sharedPref.getBoolean("near_me_enabled", true)
        switchOffline.isChecked = sharedPref.getBoolean("offline_mode_enabled", false)

        val isDarkMode = sharedPref.getBoolean("dark_mode_enabled", true)
        switchDarkMode.isChecked = isDarkMode

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("dark_mode_enabled", isChecked).apply()
            val mode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        btnSave.setOnClickListener {
            with(sharedPref.edit()) {
                putBoolean("notifications_enabled", switchNotif.isChecked)
                putBoolean("near_me_enabled", switchNearMe.isChecked)
                putBoolean("offline_mode_enabled", switchOffline.isChecked)
                putBoolean("dark_mode_enabled", switchDarkMode.isChecked)
                apply()
            }
            Toast.makeText(requireContext(), "Preferences Saved!", Toast.LENGTH_SHORT).show()
        }

        // 1. Edit Profile Handler
        view.findViewById<View>(R.id.llEditProfile).setOnClickListener {
            val inputName = EditText(requireContext()).apply {
                hint = "Enter your full name"
                setText(tvName.text)
                setPadding(40, 30, 40, 30)
            }
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit Profile Name")
                .setView(inputName)
                .setPositiveButton("Save") { dialog, _ ->
                    val newName = inputName.text.toString().trim()
                    if (newName.isNotBlank()) {
                        tvName.text = newName
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(newName)
                            .build()
                        currentUser?.updateProfile(profileUpdates)
                        currentUser?.uid?.let { uid ->
                            FirebaseFirestore.getInstance().collection("users").document(uid)
                                .update("name", newName)
                        }
                        Toast.makeText(requireContext(), "Profile name updated!", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // 2. Change Password Handler
        view.findViewById<View>(R.id.llChangePassword).setOnClickListener {
            val inputPassword = EditText(requireContext()).apply {
                hint = "Enter new password (min 6 chars)"
                inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                setPadding(40, 30, 40, 30)
            }
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Change Password")
                .setMessage("Enter a new password or request a reset email.")
                .setView(inputPassword)
                .setPositiveButton("Update Password") { dialog, _ ->
                    val newPass = inputPassword.text.toString().trim()
                    if (newPass.length < 6) {
                        Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    } else if (currentUser != null) {
                        currentUser.updatePassword(newPass).addOnSuccessListener {
                            Toast.makeText(requireContext(), "Password changed successfully!", Toast.LENGTH_LONG).show()
                        }.addOnFailureListener { error ->
                            Toast.makeText(requireContext(), "Failed: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Sign in to update password", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNeutralButton("Send Reset Email") { dialog, _ ->
                    val userEmail = currentUser?.email
                    if (!userEmail.isNullOrBlank()) {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail).addOnSuccessListener {
                            Toast.makeText(requireContext(), "Password reset link sent to $userEmail", Toast.LENGTH_LONG).show()
                        }.addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "No email associated with current session", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // 3. Delivery Addresses Handler
        view.findViewById<View>(R.id.llDeliveryAddresses).setOnClickListener {
            val savedAddress = sharedPref.getString("saved_delivery_address", "") ?: ""
            val inputAddress = EditText(requireContext()).apply {
                hint = "e.g. 124 Vilakazi St, Orlando West, Soweto"
                setText(savedAddress)
                setPadding(40, 30, 40, 30)
            }
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delivery Address")
                .setMessage("Set your default delivery address for basket checkout.")
                .setView(inputAddress)
                .setPositiveButton("Save Address") { dialog, _ ->
                    val newAddress = inputAddress.text.toString().trim()
                    sharedPref.edit().putString("saved_delivery_address", newAddress).apply()
                    currentUser?.uid?.let { uid ->
                        FirebaseFirestore.getInstance().collection("users").document(uid)
                            .update("deliveryAddress", newAddress)
                    }
                    Toast.makeText(requireContext(), "Delivery address saved!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // 4. Payment Methods Handler
        view.findViewById<View>(R.id.llPaymentMethods).setOnClickListener {
            val paymentMethods = arrayOf(
                "Credit / Debit Card (Visa / Mastercard)",
                "Instant EFT (Ozow / Capitec Pay)",
                "Cash on Delivery",
                "KasiCrafts Loyalty Points"
            )
            val spinnerPayment = Spinner(requireContext())
            val paymentAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, paymentMethods)
            spinnerPayment.adapter = paymentAdapter

            val savedPayment = sharedPref.getString("saved_payment_method", "") ?: ""
            if (savedPayment.isNotBlank()) {
                val idx = paymentMethods.indexOf(savedPayment)
                if (idx >= 0) spinnerPayment.setSelection(idx)
            }

            val layout = android.widget.LinearLayout(requireContext()).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(50, 40, 50, 20)
                addView(spinnerPayment)
            }

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Preferred Payment Method")
                .setMessage("Select your default payment method for Escrow protection:")
                .setView(layout)
                .setPositiveButton("Save Payment Method") { dialog, _ ->
                    val chosenMethod = spinnerPayment.selectedItem?.toString() ?: paymentMethods[0]
                    sharedPref.edit().putString("saved_payment_method", chosenMethod).apply()
                    currentUser?.uid?.let { uid ->
                        FirebaseFirestore.getInstance().collection("users").document(uid)
                            .update("paymentMethod", chosenMethod)
                    }
                    Toast.makeText(requireContext(), "Payment method saved: $chosenMethod", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        btnSignOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            with(sharedPref.edit()) {
                putBoolean("is_logged_in", false)
                putString("user_email", "")
                putString("profile_avatar_uri", "")
                apply()
            }
            Toast.makeText(requireContext(), "Signed out successfully", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
        }
    }
}