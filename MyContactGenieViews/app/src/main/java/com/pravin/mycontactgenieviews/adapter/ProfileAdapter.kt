package com.pravin.mycontactgenieviews.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pravin.mycontactgenieviews.R
import com.pravin.mycontactgenieviews.remote.model.Result
import com.squareup.picasso.Picasso

class ProfileAdapter(
    private val results: List<Result>,
    private val onProfileClick: (Result) -> Unit
) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile, parent, false)
        return ProfileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val result = results[position]
        holder.bind(result, onProfileClick)
    }

    override fun getItemCount(): Int = results.size

    class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView = itemView.findViewById<TextView>(R.id.profile_name)
        private val profileImageView = itemView.findViewById<ImageView>(R.id.profile_image)

        fun bind(result: Result, onProfileClick: (Result) -> Unit) {
            val name = result.name.first + " " + result.name.last
            nameTextView.text = name
            Picasso.get().load(result.picture.large).into(profileImageView)

            itemView.setOnClickListener {
                onProfileClick(result)
            }
        }
    }
}
