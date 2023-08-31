package com.musfiul.uas.bakulproperti.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.musfiul.uas.bakulproperti.R
import com.musfiul.uas.bakulproperti.activity.DetailPropertiActivity
import com.musfiul.uas.bakulproperti.helper.Helper
import com.musfiul.uas.bakulproperti.model.Properti

class AdapterProperti (var activity: Activity, var data:ArrayList<Properti>):RecyclerView.Adapter<AdapterProperti.Holder>(){
    class Holder(view: View):RecyclerView.ViewHolder(view) {

        val layout = view.findViewById<CardView>(R.id.layout_utama)
        val layoutSlider = view.findViewById<LinearLayout>(R.id.layoutSlider)

        val vpSliderProperti = view.findViewById<ViewPager>(R.id.vp_sliderProperti)

        val tvNama = view.findViewById<TextView>(R.id.tv_namaProperti)
        val tvLocation = view.findViewById<TextView>(R.id.tv_location)
        val tvHarga = view.findViewById<TextView>(R.id.tv_Harga)
        val tvDeskripsi = view.findViewById<TextView>(R.id.tvDeskripsi)
        val btn_tambah = view.findViewById<ImageView>(R.id.btn_tambah)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_properti, parent, false);

        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.tvNama.text = data[position].nama_properti
        holder.tvLocation.text = data[position].provinsi+", "+data[position].kabupaten+", "+data[position].kecamatan
        holder.tvHarga.text = Helper().gantiRupiah(data[position].harga)
        holder.tvDeskripsi.text = data[position].deskripsi


        //        membuat tampilan slider dengan ViewPager
        //        semua gambar yang berada di drawable bernilai integer

        val arraySlider = ArrayList<String>();

        for(gambar in data[position].get_gambar){
            arraySlider.add(gambar.gambar!!)
        }

        val adapterSlider = AdapterPropertiSlider(arraySlider, activity);
        holder.vpSliderProperti.adapter =adapterSlider;



        holder.layout.setOnClickListener{
            val intent = Intent(activity, DetailPropertiActivity::class.java)

            val str = Gson().toJson(data[position], Properti::class.java)

            intent.putExtra("extra",str)
            activity.startActivity(intent)
        }

        holder.layoutSlider.setOnClickListener{
            val intent = Intent(activity, DetailPropertiActivity::class.java)

            val str = Gson().toJson(data[position], Properti::class.java)

            intent.putExtra("extra",str)
            activity.startActivity(intent)
        }



        holder.btn_tambah.setOnClickListener{
//            val intent = Intent(activity, DetailPropertiActivity::class.java)
//
//            val str = Gson().toJson(data[position], Properti::class.java)
//
//            intent.putExtra("extra",str)
//            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}