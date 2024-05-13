package com.example.roomretrofitrecycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roomretrofitrecycler.model.Gender

class GenderAdapter(private val dataList: List<Gender>): RecyclerView.Adapter<GenderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
        return  ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currItem = dataList[position]
        holder.rvName.text = currItem.name
        holder.rvGender.text = currItem.gender
        holder.rvProbability.text = currItem.probability.times(100).toString()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val rvName : TextView = itemView.findViewById(R.id.card_name)
        val rvGender : TextView = itemView.findViewById(R.id.gender)
        val rvProbability : TextView = itemView.findViewById(R.id.percentage)
    }
}