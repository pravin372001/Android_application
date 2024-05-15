package com.example.newswithweather.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.newswithweather.fragment.ListViewFragmentDirections
import com.example.newswithweather.R
import com.example.newswithweather.database.NewsModel
import com.example.newswithweather.listener.ImageClickListener
import com.example.newswithweather.model.news.News
import com.squareup.picasso.Picasso

class NewsAdapter(private val fragment:Fragment, private val imageClickListener: ImageClickListener) : RecyclerView.Adapter<NewsAdapter.ViewHolder>(){

    private val newsList: MutableList<NewsModel> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.news_card_recycler, parent, false)
        return NewsAdapter.ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = newsList[position]
        Picasso.get().load(currentItem.imageUrl).into(holder.imageView)
        holder.imageView.setOnClickListener {
            imageClickListener.onClick(currentItem.imageUrl)
        }
        holder.titleTextView.text = currentItem.title
        holder.descriptionTextView.text = currentItem.content
        holder.timeText.text = currentItem.time
        holder.readMore.setOnClickListener {
            val action = ListViewFragmentDirections.actionListViewFragmentToWebView(readMoreUrl = currentItem.readMoreUrl)
            fragment.findNavController().navigate(action)
        }
        holder.share.setOnClickListener {
            shareNews(currentItem.title, currentItem.readMoreUrl)
        }
    }

    private fun shareNews(title: String, url: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        shareIntent.putExtra(Intent.EXTRA_TEXT, "$title\n$url")
        fragment.startActivity(Intent.createChooser(shareIntent, "Check this News"))
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    fun addNews(news: List<NewsModel>) {
        val currentSize = itemCount
        newsList.addAll(news)
        notifyItemRangeInserted(currentSize, newsList.size - 1)
    }

    fun setNews(news: List<NewsModel>) {
        newsList.clear()
        newsList.addAll(news)
        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.thumbnail)
        val titleTextView: TextView = itemView.findViewById(R.id.title_name)
        val descriptionTextView: TextView = itemView.findViewById(R.id.description)
        val timeText: TextView = itemView.findViewById(R.id.date_time)
        val readMore: Button = itemView.findViewById(R.id.materialButton_readmore)
        val share: Button = itemView.findViewById(R.id.share)
    }
}