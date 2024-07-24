package com.example.znews.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.znews.R
import com.example.znews.listeners.CategoryClickListener
import com.google.android.material.chip.Chip

class CategoryAdapter(
    private val categories: List<String>,
    private val listener: CategoryClickListener,
    private var selectPostion: Int
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item_card, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryName.text = category.capitalizeFirstLetter()

        // Update the Chip's appearance based on the selected position
        if (position == selectPostion) {
            holder.categoryName.isChecked = true
            holder.categoryName.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.md_theme_onPrimaryContainer)
            )
            holder.categoryName.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.md_theme_inverseSurface_mediumContrast)
            )
        } else {
            holder.categoryName.isChecked = false
            holder.categoryName.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.md_theme_background)
            )
            holder.categoryName.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.md_theme_primary)
            )
        }

        holder.categoryName.setOnClickListener {
            val previousPosition = selectPostion
            selectPostion = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectPostion)
            listener.onCategoryClicked(category)
            listener.onCategoryPosition(position)
        }
    }

    private fun String.capitalizeFirstLetter(): String {
        return if (isNotEmpty()) {
            this[0].uppercaseChar() + substring(1)
        } else {
            this
        }
    }

    override fun getItemCount(): Int = categories.size

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: Chip = itemView.findViewById(R.id.category_chip)
    }

    companion object {
        private const val TAG = "CategoryAdapter"
    }
}
