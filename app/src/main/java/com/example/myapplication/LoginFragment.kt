package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val emailInput = view.findViewById<EditText>(R.id.etEmail)
        val passwordInput = view.findViewById<EditText>(R.id.etPassword)
        val loginButton = view.findViewById<Button>(R.id.btnLogin)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || password.isBlank()) {
                Toast.makeText(requireContext(), "Enter a valid email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            loginButton.isEnabled = false
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (!isAdded) return@addOnCompleteListener
                loginButton.isEnabled = true
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user?.isEmailVerified == true) {
                        findNavController().navigate(R.id.action_login_to_home)
                    } else {
                        auth.signOut()
                        Toast.makeText(requireContext(), "Verify your email before signing in.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(requireContext(), task.exception?.localizedMessage ?: "Login failed", Toast.LENGTH_LONG).show()
                }
            }
        }

        view.findViewById<TextView>(R.id.tvRegister).setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }
    }
}
