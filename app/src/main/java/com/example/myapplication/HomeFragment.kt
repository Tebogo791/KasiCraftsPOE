package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ListenerRegistration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {
    private lateinit var adapter: ProductAdapter
    private var productListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvProducts = view.findViewById<RecyclerView>(R.id.rvProducts)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        adapter = ProductAdapter { product ->
            CartManager.addItem(product)
            Toast.makeText(requireContext(), "${product.title} added to basket!", Toast.LENGTH_SHORT).show()
        }

        rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        rvProducts.adapter = adapter

        fetchProducts(progressBar)
    }

    private fun fetchProducts(progressBar: ProgressBar) {
        progressBar.visibility = View.VISIBLE
        productListener = MarketplaceRepository.observeProducts(
            onProducts = { products, fromCache ->
                if (!isAdded) return@observeProducts
                progressBar.visibility = View.GONE
                adapter.setData(products)
                if (products.isEmpty() && !fromCache) {
                    Toast.makeText(requireContext(), "No crafts listed yet. Add one from Sell.", Toast.LENGTH_SHORT).show()
                }
            },
            onError = { error ->
                if (!isAdded) return@observeProducts
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Unable to load crafts: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        )
    }

    override fun onDestroyView() {
        productListener?.remove()
        productListener = null
        super.onDestroyView()
    }
}

class ProductAdapter(private val onClick: (Product) -> Unit) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private var products: List<Product> = emptyList()

    fun setData(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position], onClick)
    }

    override fun getItemCount() = products.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(product: Product, onClick: (Product) -> Unit) {
            itemView.findViewById<android.widget.TextView>(R.id.tvTitle).text = product.title
            itemView.findViewById<android.widget.TextView>(R.id.tvSeller).text = product.sellerName
            itemView.findViewById<android.widget.TextView>(R.id.tvPrice).text = product.price
            
            val ivProduct = itemView.findViewById<android.widget.ImageView>(R.id.ivProduct)
            
            // Professional Glide integration for dynamic web image loading and caching
            com.bumptech.glide.Glide.with(itemView.context)
                .load(product.imageResId ?: product.imageUrl)
                .placeholder(R.drawable.ic_placeholder_logo)
                .error(R.drawable.ic_placeholder_logo)
                .into(ivProduct)

            itemView.setOnClickListener { onClick(product) }
        }
    }
}