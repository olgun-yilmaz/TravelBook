package com.olgunyilmaz.travelbook.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.olgunyilmaz.travelbook.view.MapsActivity
import com.olgunyilmaz.travelbook.databinding.RecyclerRowBinding
import com.olgunyilmaz.travelbook.model.Place

class TravelAdapter (val travelList : ArrayList<Place>) : RecyclerView.Adapter<TravelAdapter.TravelHolder>()   {

    class TravelHolder(val binding : RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravelHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TravelHolder(binding)
    }

    override fun getItemCount(): Int {
        return travelList.size
    }

    override fun onBindViewHolder(holder: TravelHolder, position: Int) {
        holder.binding.recyclerViewtextView.text = travelList[position].name
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, MapsActivity :: class.java)
            holder.itemView.context.startActivity(intent)
        }
    }
}