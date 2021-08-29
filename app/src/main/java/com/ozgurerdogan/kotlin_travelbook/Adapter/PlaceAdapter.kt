package com.ozgurerdogan.kotlin_travelbook.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ozgurerdogan.kotlin_travelbook.Database.Place
import com.ozgurerdogan.kotlin_travelbook.View.ListActivity
import com.ozgurerdogan.kotlin_travelbook.View.MapsActivity
import com.ozgurerdogan.kotlin_travelbook.databinding.RecyclerRowBinding

class PlaceAdapter(val placeList:List<Place>): RecyclerView.Adapter<PlaceAdapter.PlaceHolder>() {

    class PlaceHolder(val binding:RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceHolder {
        return PlaceHolder(RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
        holder.binding.recyclerViewTextView.text=placeList.get(position).id.toString()+". "+placeList.get(position).placeName.toString()

        holder.itemView.setOnClickListener {
            val intent= Intent(holder.itemView.context, MapsActivity::class.java)
            intent.putExtra("place",placeList.get(position))
            intent.putExtra("info","old")
            holder.itemView.context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return placeList.size
    }



}