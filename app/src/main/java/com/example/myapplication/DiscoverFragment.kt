package com.example.myapplication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ListenerRegistration

class DiscoverFragment : Fragment() {
    private lateinit var adapter: DiscoverProductAdapter
    private var productListener: ListenerRegistration? = null
    private var allProducts: List<Product> = emptyList()
    private var searchQuery = ""
    private var areaQuery = ""

    private fun applyFilters() {
        val filtered = allProducts.filter { product ->
            val matchesArea = areaQuery.isBlank() || product.sellerArea.contains(areaQuery, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    product.title.contains(searchQuery, ignoreCase = true) ||
                    product.sellerName.contains(searchQuery, ignoreCase = true) ||
                    product.body.contains(searchQuery, ignoreCase = true)
            matchesArea && matchesSearch
        }
        adapter.setData(filtered)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvProducts = view.findViewById<RecyclerView>(R.id.rvProducts)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        val areaInput = view.findViewById<EditText>(R.id.etDiscoverArea)

        adapter = DiscoverProductAdapter { product ->
            CartManager.addItem(product)
            Toast.makeText(requireContext(), "${product.title} added to basket!", Toast.LENGTH_SHORT).show()
        }

        rvProducts.layoutManager = LinearLayoutManager(requireContext())
        rvProducts.adapter = adapter

        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString()?.trim().orEmpty()
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        areaInput?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                areaQuery = s?.toString()?.trim().orEmpty()
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        fetchProducts(progressBar)
    }

    private fun fetchProducts(progressBar: ProgressBar) {
        progressBar.visibility = View.VISIBLE
        productListener = MarketplaceRepository.observeProducts(
            onProducts = { products, fromCache ->
                if (!isAdded) return@observeProducts
                progressBar.visibility = View.GONE
                allProducts = products
                applyFilters()
            },
            onError = { error ->
                if (!isAdded) return@observeProducts
                progressBar.visibility = View.GONE
            }
        )
    }

    override fun onDestroyView() {
        productListener?.remove()
        productListener = null
        super.onDestroyView()
    }
}

class DiscoverProductAdapter(private val onClick: (Product) -> Unit) : RecyclerView.Adapter<DiscoverProductAdapter.DiscoverViewHolder>() {
    private var products: List<Product> = emptyList()

    fun setData(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_discover_product, parent, false)
        return DiscoverViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiscoverViewHolder, position: Int) {
        holder.bind(products[position], onClick)
    }

    override fun getItemCount() = products.size

    class DiscoverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(product: Product, onClick: (Product) -> Unit) {
            val titleText = if (product.isSample) "${product.title} [Sample]" else product.title
            itemView.findViewById<android.widget.TextView>(R.id.tvTitle).text = titleText
            itemView.findViewById<android.widget.TextView>(R.id.tvSeller).text = "by " + product.sellerName
            itemView.findViewById<android.widget.TextView>(R.id.tvPrice).text = product.price

            val ivProduct = itemView.findViewById<android.widget.ImageView>(R.id.ivProduct)

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

            itemView.setOnClickListener { onClick(product) }
        }
    }
}