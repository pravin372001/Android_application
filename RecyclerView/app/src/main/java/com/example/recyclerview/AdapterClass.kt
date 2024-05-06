package com.example.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterClass(private val dataList: ArrayList<Data>):
    RecyclerView.Adapter<AdapterClass.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currItem = dataList[position]
        holder.rvImage.setImageResource(currItem.dataImage)
        holder.rvName.text = currItem.dataName
        holder.rvTime.text = currItem.dataTime
        holder.rvMessage.text = currItem.dataMessage
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val rvImage: ImageView = itemView.findViewById(R.id.image)
        val rvName: TextView = itemView.findViewById(R.id.name)
        val rvTime: TextView = itemView.findViewById(R.id.time)
        val rvMessage: TextView = itemView.findViewById(R.id.message)
    }
    
}