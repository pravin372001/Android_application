package com.example.newswithweather.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.newswithweather.ListViewFragmentDirections
import com.example.newswithweather.R
import com.example.newswithweather.database.NewsModel
import com.example.newswithweather.model.news.News
import com.squareup.picasso.Picasso

class NewsAdapter(private val fragment:Fragment, private val newsList: List<NewsModel>) : RecyclerView.Adapter<NewsAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.news_card_recycler, parent, false)
        return NewsAdapter.ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = newsList[position]
        Picasso.get().load(currentItem.imageUrl).into(holder.imageView)
        holder.titleTextView.text = currentItem.title
        holder.descriptionTextView.text = currentItem.content
        holder.cardView.setOnClickListener {
            val action = ListViewFragmentDirections.actionListViewFragmentToWebView(readMoreUrl = currentItem.readMoreUrl)
            fragment.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

//    fun setItems(items: List<NewsModel>) {
//        newsList.clear()
//        newsList.addAll(items)
//        notifyDataSetChanged()
//    }
//
//    fun addItems(items: List<NewsModel>) {
//        val startPosition = newsList.size
//        newsList.addAll(items)
//        notifyItemRangeInserted(startPosition, items.size)
//    }
//
//    fun clearItems() {
//        newsList.clear()
//        notifyDataSetChanged()
//    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.thumbnail)
        val titleTextView: TextView = itemView.findViewById(R.id.title_name)
        val descriptionTextView: TextView = itemView.findViewById(R.id.description)
        val cardView: CardView = itemView.findViewById(R.id.item_layout)
    }
}