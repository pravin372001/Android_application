package com.example.newswithweather.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.newswithweather.listener.CategoryClickListener
import com.example.newswithweather.R

class CategoryAdapter(private val categories: List<String>, private val listener: CategoryClickListener) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>(){

    private var selectedPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.category_recycler, parent, false)
        return  ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val currItem = categories[position]
        holder.categoryName.text = currItem

        // Update the background and text color based on selection state
        if (position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#7497C2")) // Change to your desired color
            holder.categoryName.setTextColor(Color.parseColor("#FFF9F9"))      // Change to your desired color
        } else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFF9F9")) // Change to your desired default color
            holder.categoryName.setTextColor(Color.parseColor("#7497C2"))       // Change to your desired default color
        }

        holder.cardView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            listener.onCategoryClicked(currItem)
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val categoryName: TextView = itemView.findViewById(R.id.catergoryid)
        val cardView: CardView = itemView.findViewById(R.id.item_layout_category)
    }
}