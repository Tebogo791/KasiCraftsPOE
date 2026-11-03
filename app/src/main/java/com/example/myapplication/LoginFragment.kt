package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val emailInput = view.findViewById<EditText>(R.id.etEmail)
        val passwordInput = view.findViewById<EditText>(R.id.etPassword)
        val loginButton = view.findViewById<Button>(R.id.btnLogin)
        val googleButton = view.findViewById<Button>(R.id.btnGoogleSignIn)

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
                    auth.currentUser?.reload()?.addOnCompleteListener { reload ->
                        if (!isAdded) return@addOnCompleteListener
                        if (reload.isSuccessful && auth.currentUser?.isEmailVerified == true) {
                            findNavController().navigate(R.id.action_login_to_home)
                        } else {
                            auth.signOut()
                            Toast.makeText(requireContext(), if (!reload.isSuccessful) "Could not refresh account verification." else "Verify your email before signing in.", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Log.e("KasiCraftsLogin", "Email sign-in failed", task.exception)
                    Toast.makeText(requireContext(), task.exception?.localizedMessage ?: "Login failed", Toast.LENGTH_LONG).show()
                }
            }
        }

        googleButton.setOnClickListener {
            googleButton.isEnabled = false
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val option = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setAutoSelectEnabled(false)
                        .build()
                    val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
                    val result = CredentialManager.create(requireContext()).getCredential(
                        context = requireActivity(),
                        request = request
                    )
                    val credential = result.credential
                    if (credential !is CustomCredential ||
                        credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        throw IllegalStateException("Unsupported Google credential")
                    }
                    val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                        .addOnCompleteListener { task ->
                            if (!isAdded) return@addOnCompleteListener
                            googleButton.isEnabled = true
                            if (task.isSuccessful) {
                                findNavController().navigate(R.id.action_login_to_home)
                            } else {
                                Log.e("KasiCraftsLogin", "Google Firebase sign-in failed", task.exception)
                                Toast.makeText(requireContext(), task.exception?.localizedMessage ?: "Google sign-in failed", Toast.LENGTH_LONG).show()
                            }
                        }
                } catch (e: GetCredentialException) {
                    Log.e("KasiCraftsLogin", "Google credential request failed", e)
                    if (isAdded) Toast.makeText(requireContext(), "Google sign-in unavailable: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    googleButton.isEnabled = true
                } catch (e: Exception) {
                    Log.e("KasiCraftsLogin", "Google sign-in failed", e)
                    if (isAdded) Toast.makeText(requireContext(), "Google sign-in failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    googleButton.isEnabled = true
                }
            }
        }

        view.findViewById<TextView>(R.id.tvRegister).setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }
    }
}
