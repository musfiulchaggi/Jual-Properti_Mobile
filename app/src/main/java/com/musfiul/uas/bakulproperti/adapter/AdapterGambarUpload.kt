package com.musfiul.uas.bakulproperti.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.musfiul.uas.bakulproperti.R
import com.musfiul.uas.bakulproperti.model.Gambar
import com.musfiul.uas.bakulproperti.util.Config
import com.squareup.picasso.Picasso

class AdapterGambarUpload (var activity: Activity, var data:ArrayList<Gambar>, var listeners:Listeners) : RecyclerView.Adapter<AdapterGambarUpload.Holder> (){

    class Holder(view : View) : RecyclerView.ViewHolder(view){
        val imageUpload = view.findViewById<ImageView>(R.id.imageUpload)
        val btnHapus = view.findViewById<ImageView>(R.id.btn_Hapus)

        val layout = view.findViewById<CardView>(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_gambar_upload, parent, false);

        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val gambar = data[position]

        val imageUri = Config.propertiUrl+data[position].gambar
        Picasso.get().load(imageUri).placeholder(R.drawable.images).error(R.drawable.images).into(holder.imageUpload);

        holder.btnHapus.setOnClickListener{
            //            mengubah is selected pilih true

            listeners.onClicked(gambar)
        }


    }


    interface Listeners{
        //        nama fungsi terserah kita
        fun onClicked(data:Gambar);
    }
    override fun getItemCount(): Int {
        return data.size
    }

}