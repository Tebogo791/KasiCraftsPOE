package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.util.Locale

class CartFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvCartStatus = view.findViewById<TextView>(R.id.tvCartStatus)
        val tvSubtotal = view.findViewById<TextView>(R.id.tvSubtotalPrice)
        val tvDelivery = view.findViewById<TextView>(R.id.tvDeliveryPrice)
        val tvTotal = view.findViewById<TextView>(R.id.tvTotalPrice)
        val btnCheckout = view.findViewById<Button>(R.id.btnCheckout)

        // Calculate Totals
        val items = CartManager.getItems()
        val subtotalValue = CartManager.calculateSubtotal()
        
        val deliveryValue = if (items.isNotEmpty()) 65.0 else 0.0
        val totalValue = subtotalValue + deliveryValue

        // Update UI Text
        tvCartStatus.text = if (items.isEmpty()) {
            "Your basket is empty (0 items)"
        } else {
            "You have ${items.size} item(s) in your basket"
        }

        tvSubtotal.text = String.format(Locale.ROOT, "R %.2f", subtotalValue)
        tvDelivery.text = String.format(Locale.ROOT, "R %.2f", deliveryValue)
        tvTotal.text = String.format(Locale.ROOT, "R %.2f", totalValue)

        btnCheckout.setOnClickListener {
            if (items.isEmpty()) {
                Toast.makeText(requireContext(), "Your basket is empty!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Checkout is not available yet. No payment has been taken or loyalty points awarded.", Toast.LENGTH_LONG).show()
            }
        }
    }
}