package com.pravin.mycontactview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pravin.mycontactview.R
import com.pravin.mycontactview.remote.model.Result
import com.squareup.picasso.Picasso

class ContactAdapter(private val contactList :List<Result>):RecyclerView.Adapter<ContactAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contactList[position]
        holder.nameTextView.text = contact.name.first
        holder.numberTextView.text = contact.phone
        Picasso.get().load(contact.picture.thumbnail).fit().into(holder.profileImageView)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.contact_name)
        val numberTextView: TextView = itemView.findViewById(R.id.contact_number)
        val profileImageView: ImageView = itemView.findViewById(R.id.contact_profile_picture)
    }
}