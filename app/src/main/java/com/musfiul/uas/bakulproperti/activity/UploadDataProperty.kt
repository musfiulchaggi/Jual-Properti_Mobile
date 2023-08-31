package com.musfiul.uas.bakulproperti.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import com.musfiul.uas.bakulproperti.R
import com.musfiul.uas.bakulproperti.app.ApiConfig
import com.musfiul.uas.bakulproperti.helper.Helper
import com.musfiul.uas.bakulproperti.helper.SharedPref
import com.musfiul.uas.bakulproperti.model.ModelAlamat
import com.musfiul.uas.bakulproperti.model.Properti
import com.musfiul.uas.bakulproperti.model.ResponModel
import com.musfiul.uas.bakulproperti.model.User
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import retrofit2.Call
import retrofit2.Response

class UploadDataProperty : AppCompatActivity() {
    lateinit var toolbar : Toolbar

    lateinit var spn_jenis : Spinner
    lateinit var edt_phone : EditText
    lateinit var edt_email : EditText
    lateinit var edt_deskripsi : EditText
    lateinit var edt_harga : EditText

    lateinit var btn_simpan : Button

    lateinit var properti: Properti

    //    inisialisai sp
    lateinit var sp: SharedPref
    var user = User()
    var user_id = User().id

//    arraylist nama
    var arrayString = ArrayList<String>()
    var jenis: String? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_data_property)

        init()

        sp = SharedPref(this)
        user = sp.getUser()!!
        user_id = user.id

        var intent= intent

        if(intent.hasExtra("extra")){
            val data= intent.getStringExtra("extra")
            properti = Gson().fromJson<Properti>(data, Properti::class.java)

//            untuk menghilangkan inten
            intent.removeExtra("extra")
        }

        //        setToolBar
        Helper().setToolbar(this, toolbar , "Data Properti")

    }

    fun init(){
        btn_simpan = findViewById(R.id.btn_simpan)

        spn_jenis = findViewById(R.id.spn_jenis)
        edt_phone = findViewById(R.id.edt_phone)
        edt_email = findViewById(R.id.edt_email)
        edt_deskripsi = findViewById(R.id.edt_deskripsi)
        edt_harga = findViewById(R.id.edt_harga)


//        toolbar
        toolbar = findViewById(R.id.toolbar)

        btn_simpan.setOnClickListener{
            simpanAlamat()
        }

        tampilSpn()


    }

    fun tampilSpn(){
        arrayString.add("Pilih Jenis Properti ")
        arrayString.add("Rumah")
        arrayString.add("Apartemen")

        jenis = arrayString[0]

        val adapter = ArrayAdapter<Any>(this@UploadDataProperty, R.layout.item_spinner, arrayString.toTypedArray())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spn_jenis.adapter = adapter

        spn_jenis.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    id: Long,
                ) {
                    if(position != 0){
                        jenis = arrayString[position]
                    }
                }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }


    fun simpanAlamat(){

        if(edt_phone.text.isEmpty()){

            edt_phone.error = "Kolom Phone Harus Diisi."
            edt_phone.requestFocus();
            return;
        }else if( edt_phone.text.toString()[0] == '0' ){

            edt_phone.error = "Format Phone Salah."
            edt_phone.requestFocus();
            return;

        }else if(edt_email.text.isEmpty()){
            edt_email.error = "Kolom Email Harus Diisi."
            edt_email.requestFocus();
            return;
        }else if(edt_deskripsi.text.isEmpty()){
            edt_deskripsi.error = "Kolom Deskripsi Harus Diisi."
            edt_deskripsi.requestFocus();
            return;
        }else if(edt_harga.text.isEmpty()){
            edt_harga.error = "Kolom Harga Harus Diisi."
            edt_harga.requestFocus();
            return;
        }

        if(jenis.equals(arrayString[0])){
            toast("Silahkan Pilih Jenis Properi")
            return
        }

        properti.harga = edt_harga.text.toString().toInt()
        properti.email = edt_email.text.toString()
        properti.phone = "0"+edt_phone.text.toString()
        properti.deskripsi = edt_deskripsi.text.toString()
        properti.nama_properti = jenis

        insert(properti)

    }

    fun insert (data:Properti){

        //      //     sweet alert dialog
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Loading"
        pDialog.setCancelable(false)
        pDialog.show()

        ApiConfig.instanceRetrofit.uploadData(data.id , user_id, data.nama_properti!! ,data.harga!! , data.phone!!,data.email!! ,data.deskripsi!! ).enqueue(object : retrofit2.Callback<ResponModel>{


            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

                if(response.isSuccessful){
                    if(response.body()!!.success == 1){

                        //                    menghilangkan progress bar
                        pDialog.dismiss()

                        SweetAlertDialog(this@UploadDataProperty, SweetAlertDialog.SUCCESS_TYPE)
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


                                // auto back dengan onbackpressed
                                onBackPressed()
                            }
                            .show()

                    }else{
                        SweetAlertDialog(this@UploadDataProperty, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Success!")
                            .setContentText(response.body()!!.message)
                            .setConfirmClickListener {
                                it.dismissWithAnimation()

                                // auto back dengan onbackpressed
                                onBackPressed()
                            }
                            .show()
                    }

                }else{
                    SweetAlertDialog(this@UploadDataProperty, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Gagal Upload!")
                        .setContentText(response.message())
                        .setConfirmClickListener {
                            it.dismissWithAnimation()

                            // auto back dengan onbackpressed
                            onBackPressed()
                        }
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {

                SweetAlertDialog(this@UploadDataProperty, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Gagal!")
                    .setContentText(t.message!!)
                    .setConfirmClickListener {
                        it.dismissWithAnimation()

                        // auto back dengan onbackpressed
                        onBackPressed()
                    }
                    .show()
            }

        })
    }

    private fun toast(string: String){
        Toast.makeText(this,string , Toast.LENGTH_SHORT).show()
    }

    //  digunakan untuk memunculkan fungsi onbackPressed
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}