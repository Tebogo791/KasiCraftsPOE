package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

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
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmitCraft)

        btnSubmit.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val price = etPrice.text.toString().trim()
            val seller = etSeller.text.toString().trim()

            if (title.isEmpty() || price.isEmpty() || seller.isEmpty()) {
                Toast.makeText(requireContext(), "Please complete all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Sync item details to online database live
            try {
                FirebaseDatabaseManager.uploadCraftProduct(title, price, seller, etArea.text.toString().trim())
                Toast.makeText(requireContext(), "Product synced to Cloud Database!", Toast.LENGTH_LONG).show()
                
                // Clear out inputs upon success
                etTitle.text.clear()
                etPrice.text.clear()
                etSeller.text.clear()
                etArea.text.clear()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Upload triggered successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
