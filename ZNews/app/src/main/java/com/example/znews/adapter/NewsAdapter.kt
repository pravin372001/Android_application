package com.example.znews.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.znews.R
import com.example.znews.database.NewsModel
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso

class NewsAdapter(
    private val newsList: List<NewsModel>,
    private val onNewsClick:(NewsModel)-> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_item_card, parent, false)
        return NewsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.newsTitle.text = news.title
        Picasso.get().load(news.imageUrl).into(holder.newsImage)
        holder.cardView.setOnClickListener {
            onNewsClick(news)
        }
        holder.icon.setOnClickListener {
            onNewsClick(news)
        }
    }

    override fun getItemCount() = newsList.size

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val newsTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        val newsImage: ImageView = itemView.findViewById(R.id.imageView)
        val cardView: LinearLayout = itemView.findViewById(R.id.news_card)
        val icon: MaterialButton = itemView.findViewById(R.id.moreButton)
    }

}