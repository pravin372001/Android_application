package com.example.retrofitrecycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitrecycler.model.CountryItem
import com.squareup.picasso.Picasso

class CountryAdapter(private val datalist: List<CountryItem>) : RecyclerView.Adapter<CountryAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currItem = datalist[position]
        Picasso.get().load(currItem.flags.png).into(holder.rvFlag)
        holder.rvCountry.text = currItem.name.common
        holder.rvOffical.text = currItem.name.official
        holder.rvPopulation.text = currItem.population.toString()
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val rvFlag: ImageView = itemView.findViewById(R.id.flag)
        val rvCountry : TextView = itemView.findViewById(R.id.country)
        val rvOffical : TextView = itemView.findViewById(R.id.officalName)
        val rvPopulation : TextView = itemView.findViewById(R.id.populationcount)
    }

}