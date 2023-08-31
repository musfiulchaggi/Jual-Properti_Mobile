package com.musfiul.uas.bakulproperti.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.musfiul.uas.bakulproperti.R
import com.musfiul.uas.bakulproperti.activity.DataPropertiTambahActivity
import com.musfiul.uas.bakulproperti.activity.DetailPropertiActivity
import com.musfiul.uas.bakulproperti.activity.UploadGambar
import com.musfiul.uas.bakulproperti.app.ApiConfig
import com.musfiul.uas.bakulproperti.helper.Helper
import com.musfiul.uas.bakulproperti.model.Properti
import com.musfiul.uas.bakulproperti.model.ResponModel
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import retrofit2.Call
import retrofit2.Response

class AdapterPropertiJual (var activity: Activity, var data:ArrayList<Properti>, var listeners:Listeners):RecyclerView.Adapter<AdapterPropertiJual.Holder>(){
    class Holder(view: View):RecyclerView.ViewHolder(view) {

        val layout = view.findViewById<CardView>(R.id.layout_utama)
        val layoutSlider = view.findViewById<LinearLayout>(R.id.layoutSlider)

        val vpSliderProperti = view.findViewById<ViewPager>(R.id.vp_sliderProperti)

        val tvNama = view.findViewById<TextView>(R.id.tv_namaProperti)
        val tvLocation = view.findViewById<TextView>(R.id.tv_location)
        val tvHarga = view.findViewById<TextView>(R.id.tv_Harga)
        val tvDeskripsi = view.findViewById<TextView>(R.id.tvDeskripsi)
        val btn_ubahProperti = view.findViewById<Button>(R.id.btn_ubahProperti)
        val btn_hapusProperti = view.findViewById<Button>(R.id.btn_hapusProperti)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_properti_jual, parent, false);

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



        holder.btn_ubahProperti.setOnClickListener{
//            val intent = Intent(activity, DetailPropertiActivity::class.java)
//
//            val str = Gson().toJson(data[position], Properti::class.java)
//
//            intent.putExtra("extra",str)
//            activity.startActivity(intent)

            val intent = Intent(activity, DataPropertiTambahActivity::class.java)
            intent.putExtra("extra", Gson().toJson(data[position], Properti::class.java))
            activity.startActivity(intent)
        }


        holder.btn_hapusProperti.setOnClickListener{
//           membuat listener untuk menghapus dan kemudian mereload tampilan di FragmentAddProperti

            ApiConfig.instanceRetrofit.hapusData(data[position].id, data[position].user_id!!.toInt() ).enqueue(object : retrofit2.Callback<ResponModel>{


                override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

                    if(response.isSuccessful){
                        if(response.body()!!.success == 1){


                            SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success!")
                                .setContentText("Upload Properti Berhasil.")
                                .setConfirmClickListener {
                                    it.dismissWithAnimation()

//                              mengambil data dari server
                                    var respon = response.body()
//                                propertis = respon!!.propertis
//                                properti = propertis[0]
//                                jumlahGambar = respon!!.jumlahGambar

                                    Log.d("respon", respon!!.properti.toString())


                                }
                                .show()

//                            mammanggil listener
                            listeners.onClicked()

                        }else{
                            SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Success!")
                                .setContentText(response.body()!!.message)
                                .setConfirmClickListener {
                                    it.dismissWithAnimation()

                                }
                                .show()
                        }

                    }else{
                        SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Gagal Upload!")
                            .setContentText(response.message())
                            .setConfirmClickListener {
                                it.dismissWithAnimation()

                            }
                            .show()
                    }
                }

                override fun onFailure(call: Call<ResponModel>, t: Throwable) {

                    SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Gagal!")
                        .setContentText(t.message!!)
                        .setConfirmClickListener {
                            it.dismissWithAnimation()
                        }
                        .show()
                }

            })
        }


    }

    interface Listeners{
        //        nama fungsi terserah kita
        fun onClicked();
    }

    override fun getItemCount(): Int {
        return data.size
    }
}