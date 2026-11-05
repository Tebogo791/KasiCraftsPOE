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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.ListenerRegistration

class HomeFragment : Fragment() {
    private lateinit var adapter: ProductAdapter
    private var productListener: ListenerRegistration? = null
    private var allProducts: List<Product> = emptyList()
    private var searchQuery = ""
    private var selectedCategory = "All Items"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvProducts = view.findViewById<RecyclerView>(R.id.rvProducts)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        val cgCategories = view.findViewById<ChipGroup>(R.id.cgCategories)

        adapter = ProductAdapter { product ->
            CartManager.addItem(product)
            Toast.makeText(requireContext(), "${product.title} added to basket!", Toast.LENGTH_SHORT).show()
        }

        rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        rvProducts.adapter = adapter

        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString()?.trim().orEmpty()
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        cgCategories?.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) {
                selectedCategory = "All Items"
            } else {
                val chip = group.findViewById<Chip>(checkedIds.first())
                selectedCategory = chip?.text?.toString() ?: "All Items"
            }
            applyFilters()
        }

        fetchProducts(progressBar)
    }

    private fun applyFilters() {
        val filtered = allProducts.filter { product ->
            val matchesCategory = selectedCategory == "All Items" ||
                    selectedCategory == "Zonke Izinto" ||
                    selectedCategory == "Alle Items" ||
                    product.category.equals(selectedCategory, ignoreCase = true) ||
                    (selectedCategory.contains("Basket", ignoreCase = true) && product.category.contains("Basket", ignoreCase = true)) ||
                    (selectedCategory.contains("Mandji", ignoreCase = true) && product.category.contains("Basket", ignoreCase = true)) ||
                    (selectedCategory.contains("Izimbiza", ignoreCase = true) && product.category.contains("Basket", ignoreCase = true)) ||
                    (selectedCategory.contains("Pottery", ignoreCase = true) && product.category.contains("Pottery", ignoreCase = true)) ||
                    (selectedCategory.contains("Potteb", ignoreCase = true) && product.category.contains("Pottery", ignoreCase = true)) ||
                    (selectedCategory.contains("Izitsha", ignoreCase = true) && product.category.contains("Pottery", ignoreCase = true)) ||
                    (selectedCategory.contains("Jewel", ignoreCase = true) && product.category.contains("Jewel", ignoreCase = true)) ||
                    (selectedCategory.contains("Juweel", ignoreCase = true) && product.category.contains("Jewel", ignoreCase = true)) ||
                    (selectedCategory.contains("Izinto Zokuhlobisa", ignoreCase = true) && product.category.contains("Jewel", ignoreCase = true)) ||
                    (selectedCategory.contains("Textile", ignoreCase = true) && product.category.contains("Textile", ignoreCase = true)) ||
                    (selectedCategory.contains("Tekstiel", ignoreCase = true) && product.category.contains("Textile", ignoreCase = true)) ||
                    (selectedCategory.contains("Izindwangu", ignoreCase = true) && product.category.contains("Textile", ignoreCase = true))

            val matchesSearch = searchQuery.isEmpty() ||
                    product.title.contains(searchQuery, ignoreCase = true) ||
                    product.sellerName.contains(searchQuery, ignoreCase = true) ||
                    product.body.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesSearch
        }
        adapter.setData(filtered)
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
            val titleText = if (product.isSample) "${product.title} [Sample]" else product.title
            itemView.findViewById<android.widget.TextView>(R.id.tvTitle).text = titleText
            itemView.findViewById<android.widget.TextView>(R.id.tvSeller).text = product.sellerName
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