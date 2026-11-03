package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_register, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val nameInput = view.findViewById<EditText>(R.id.etRegisterName)
        val emailInput = view.findViewById<EditText>(R.id.etRegisterEmail)
        val passwordInput = view.findViewById<EditText>(R.id.etRegisterPassword)
        val roleGroup = view.findViewById<RadioGroup>(R.id.rgRole)
        val registerButton = view.findViewById<Button>(R.id.btnCreateAccount)

        registerButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            if (name.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || password.length < 6) {
                Toast.makeText(requireContext(), "Enter a name, valid email and password (6+ characters)", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val role = if (roleGroup.checkedRadioButtonId == R.id.rbSeller) "seller" else "buyer"
            registerButton.isEnabled = false
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (!isAdded) return@addOnCompleteListener
                if (!task.isSuccessful) {
                    registerButton.isEnabled = true
                    Toast.makeText(requireContext(), task.exception?.localizedMessage ?: "Registration failed", Toast.LENGTH_LONG).show()
                    return@addOnCompleteListener
                }

                val user = auth.currentUser
                if (user == null) {
                    registerButton.isEnabled = true
                    Toast.makeText(requireContext(), "Account created, but session unavailable. Restart and sign in.", Toast.LENGTH_LONG).show()
                    return@addOnCompleteListener
                }

                // Save profile independently: Firestore may wait for a network connection.
                val profile = mapOf("name" to name, "email" to email, "role" to role, "loyaltyPoints" to 0)
                FirebaseFirestore.getInstance().collection("users").document(user.uid)
                    .set(profile).addOnFailureListener { exception ->
                        Log.e("KasiCraftsRegistration", "Profile save failed", exception)
                    }

                // Do not block verification on Firestore completion.
                user.sendEmailVerification().addOnCompleteListener { verificationTask ->
                    if (!isAdded) return@addOnCompleteListener
                    registerButton.isEnabled = true
                    if (verificationTask.isSuccessful) {
                        auth.signOut()
                        Toast.makeText(requireContext(), "Account created! Check your inbox and verify your email before logging in.", Toast.LENGTH_LONG).show()
                        findNavController().popBackStack()
                    } else {
                        Log.e("KasiCraftsRegistration", "Verification email failed", verificationTask.exception)
                        Toast.makeText(requireContext(), "Account created, but verification email failed: ${verificationTask.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
