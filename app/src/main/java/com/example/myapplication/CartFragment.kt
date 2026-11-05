package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

class CartFragment : Fragment() {
    private lateinit var cartAdapter: CartAdapter
    private lateinit var tvCartStatus: TextView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvDelivery: TextView
    private lateinit var tvTotal: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvCartStatus = view.findViewById(R.id.tvCartStatus)
        tvSubtotal = view.findViewById(R.id.tvSubtotalPrice)
        tvDelivery = view.findViewById(R.id.tvDeliveryPrice)
        tvTotal = view.findViewById(R.id.tvTotalPrice)
        val btnCheckout = view.findViewById<Button>(R.id.btnCheckout)
        val rvCartItems = view.findViewById<RecyclerView>(R.id.rvCartItems)

        cartAdapter = CartAdapter(CartManager.getItems().toMutableList()) { position ->
            CartManager.removeItemAt(position)
            cartAdapter.updateData(CartManager.getItems())
            updateCartSummary()
            Toast.makeText(requireContext(), "Item removed from basket", Toast.LENGTH_SHORT).show()
        }

        rvCartItems.layoutManager = LinearLayoutManager(requireContext())
        rvCartItems.adapter = cartAdapter

        updateCartSummary()

        btnCheckout.setOnClickListener {
            val items = CartManager.getItems()
            if (items.isEmpty()) {
                Toast.makeText(requireContext(), "Your basket is empty!", Toast.LENGTH_SHORT).show()
            } else {
                showCheckoutDialog()
            }
        }
    }

    private fun updateCartSummary() {
        val items = CartManager.getItems()
        val subtotalValue = CartManager.calculateSubtotal()
        val deliveryValue = if (items.isNotEmpty()) 65.0 else 0.0
        val totalValue = subtotalValue + deliveryValue

        tvCartStatus.text = if (items.isEmpty()) {
            "Your basket is empty (0 items)"
        } else {
            "You have ${items.size} item(s) in your basket"
        }

        tvSubtotal.text = String.format(Locale.ROOT, "R %.2f", subtotalValue)
        tvDelivery.text = String.format(Locale.ROOT, "R %.2f", deliveryValue)
        tvTotal.text = String.format(Locale.ROOT, "R %.2f", totalValue)
    }

    private fun showCheckoutDialog() {
        val subtotal = CartManager.calculateSubtotal()
        val delivery = 65.0
        val total = subtotal + delivery

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 20)
        }

        val savedAddress = requireActivity().getSharedPreferences("KasiCraftsPrefs", Context.MODE_PRIVATE)
            .getString("saved_delivery_address", "") ?: ""

        val inputAddress = EditText(requireContext()).apply {
            hint = "Enter delivery address"
            if (savedAddress.isNotBlank()) setText(savedAddress)
        }

        val tvPaymentLabel = TextView(requireContext()).apply {
            text = "\nSelect Payment Method:"
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val spinnerPayment = Spinner(requireContext())
        val paymentMethods = arrayOf(
            "Credit / Debit Card (Visa / Mastercard)",
            "Instant EFT (Ozow / Capitec Pay)",
            "Cash on Delivery",
            "KasiCrafts Loyalty Points"
        )
        val paymentAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, paymentMethods)
        spinnerPayment.adapter = paymentAdapter

        val savedPayment = requireActivity().getSharedPreferences("KasiCraftsPrefs", Context.MODE_PRIVATE)
            .getString("saved_payment_method", "") ?: ""
        if (savedPayment.isNotBlank()) {
            val idx = paymentMethods.indexOf(savedPayment)
            if (idx >= 0) spinnerPayment.setSelection(idx)
        }

        layout.addView(inputAddress)
        layout.addView(tvPaymentLabel)
        layout.addView(spinnerPayment)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Checkout - Order Summary")
            .setMessage(
                "Items: ${CartManager.getItemCount()}\n" +
                "Subtotal: R ${String.format(Locale.ROOT, "%.2f", subtotal)}\n" +
                "Delivery Fee: R ${String.format(Locale.ROOT, "%.2f", delivery)}\n" +
                "Total Amount: R ${String.format(Locale.ROOT, "%.2f", total)}"
            )
            .setView(layout)
            .setPositiveButton("Place Order") { dialog, _ ->
                val address = inputAddress.text.toString().trim()
                val selectedPayment = spinnerPayment.selectedItem?.toString() ?: paymentMethods[0]

                if (address.isEmpty()) {
                    Toast.makeText(requireContext(), "Please provide a delivery address", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val orderId = "KC-" + (100000..999999).random()
                CartManager.clearCart()
                cartAdapter.updateData(emptyList())
                updateCartSummary()
                dialog.dismiss()

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Order Confirmed! 🎉")
                    .setMessage(
                        "Thank you for supporting local South African artisans!\n\n" +
                        "Order ID: $orderId\n" +
                        "Delivery Address: $address\n" +
                        "Payment Method: $selectedPayment\n" +
                        "Status: Payment secured in Escrow. Your craft items are being prepared for dispatch."
                    )
                    .setPositiveButton("OK", null)
                    .show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

class CartAdapter(
    private var items: MutableList<Product>,
    private val onRemoveClick: (Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    fun updateData(newItems: List<Product>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position], onRemoveClick)
    }

    override fun getItemCount() = items.size

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(product: Product, onRemoveClick: (Int) -> Unit) {
            itemView.findViewById<TextView>(R.id.tvCartTitle).text = product.title
            itemView.findViewById<TextView>(R.id.tvCartSeller).text = "by " + product.sellerName
            itemView.findViewById<TextView>(R.id.tvCartPrice).text = product.price

            val ivProduct = itemView.findViewById<ImageView>(R.id.ivCartProduct)
            val imageSource: Any = when {
                product.imageResId != null && product.imageResId != 0 -> product.imageResId
                product.imageUrl.isNotBlank() -> product.imageUrl
                else -> R.drawable.ic_placeholder_logo
            }

            com.bumptech.glide.Glide.with(itemView.context)
                .load(imageSource)
                .placeholder(R.drawable.ic_placeholder_logo)
                .error(R.drawable.ic_placeholder_logo)
                .into(ivProduct)

            itemView.findViewById<ImageButton>(R.id.btnRemoveCart).setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onRemoveClick(pos)
                }
            }
        }
    }
}