package com.example.newswithweather.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.newswithweather.CategoryClickListener
import com.example.newswithweather.MainActivity
import com.example.newswithweather.R

class CategoryAdapter(private val categories: List<String>, private val listener: CategoryClickListener) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.category_recycler, parent, false)
        return  ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currItem = categories[position]
        holder.categoryName.text = currItem
        holder.cardView.setOnClickListener {
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