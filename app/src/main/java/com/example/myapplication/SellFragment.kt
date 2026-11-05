package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

class SellFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sell, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etTitle = view.findViewById<EditText>(R.id.etProductTitle)
        val etPrice = view.findViewById<EditText>(R.id.etProductPrice)
        val etSeller = view.findViewById<EditText>(R.id.etProductSeller)
        val etArea = view.findViewById<EditText>(R.id.etSellerArea)
        val etImageUrl = view.findViewById<EditText>(R.id.etImageUrl)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategory)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmitCraft)

        val categories = arrayOf("Baskets", "Pottery", "Jewelry", "Textiles")
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCategory.adapter = categoryAdapter

        // Pre-fill seller name if user is logged in
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val displayName = currentUser.displayName
            val emailPrefix = currentUser.email?.substringBefore("@")
            if (!displayName.isNullOrEmpty()) {
                etSeller.setText(displayName)
            } else if (!emailPrefix.isNullOrEmpty()) {
                etSeller.setText(emailPrefix)
            }
        }

        btnSubmit.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                Toast.makeText(requireContext(), "Please sign in to list a craft on the marketplace", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val title = etTitle.text.toString().trim()
            val price = etPrice.text.toString().trim()
            val seller = etSeller.text.toString().trim()
            val area = etArea.text.toString().trim()
            val imageUrl = etImageUrl.text.toString().trim()
            val category = spinnerCategory.selectedItem?.toString() ?: "Baskets"

            if (title.isEmpty() || price.isEmpty() || seller.isEmpty()) {
                Toast.makeText(requireContext(), "Please complete product title, price and seller name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSubmit.isEnabled = false
            try {
                FirebaseDatabaseManager.uploadCraftProduct(
                    title = title,
                    price = price,
                    seller = seller,
                    area = area,
                    imageUrl = imageUrl,
                    category = category
                ).addOnSuccessListener {
                    if (!isAdded) return@addOnSuccessListener
                    btnSubmit.isEnabled = true
                    Toast.makeText(requireContext(), "Craft uploaded successfully as $category!", Toast.LENGTH_LONG).show()
                    etTitle.text.clear()
                    etPrice.text.clear()
                    etArea.text.clear()
                    etImageUrl.text.clear()

                    // Navigate automatically to Home screen so the user sees their uploaded product immediately
                    findNavController().navigate(R.id.homeFragment)
                }.addOnFailureListener { error ->
                    if (!isAdded) return@addOnFailureListener
                    btnSubmit.isEnabled = true
                    Toast.makeText(requireContext(), "Upload failed: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            } catch (error: Exception) {
                btnSubmit.isEnabled = true
                Toast.makeText(requireContext(), error.localizedMessage ?: "Upload failed", Toast.LENGTH_LONG).show()
            }
        }
    }
}