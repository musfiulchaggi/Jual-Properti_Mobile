package com.musfiul.uas.bakulproperti.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.musfiul.uas.bakulproperti.R
import com.musfiul.uas.bakulproperti.model.Gambar
import com.musfiul.uas.bakulproperti.model.Properti
import com.musfiul.uas.bakulproperti.util.Config
import com.squareup.picasso.Picasso

class AdapterGambarJual (var activity: Activity, var data:ArrayList<Gambar>) : RecyclerView.Adapter<AdapterGambarJual.Holder> (){

    class Holder(view : View) : RecyclerView.ViewHolder(view){
        val imgGambar = view.findViewById<ImageView>(R.id.imageProduk)
        val layout = view.findViewById<CardView>(R.id.layout_utama)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_gambar, parent, false);

        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val a = data[position] //merepresentasikan suatu produk

//        holder.imgGambar.setImageResource(data[position].gambar)

        val imageUri = Config.propertiUrl+data[position].gambar
        Picasso.get().load(imageUri).placeholder(R.drawable.images).error(R.drawable.images).into(holder.imgGambar);



    }

    override fun getItemCount(): Int {
        return data.size
    }

}