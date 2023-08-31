package com.musfiul.uas.bakulproperti.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.gson.Gson
import com.inyongtisto.myhelper.extension.isNotNull
import com.inyongtisto.myhelper.extension.isNull
import com.inyongtisto.myhelper.extension.toMultipartBody
import com.musfiul.uas.bakulproperti.R
import com.musfiul.uas.bakulproperti.adapter.AdapterGambarUpload
import com.musfiul.uas.bakulproperti.app.ApiConfig
import com.musfiul.uas.bakulproperti.helper.Helper
import com.musfiul.uas.bakulproperti.helper.SharedPref
import com.musfiul.uas.bakulproperti.model.Gambar
import com.musfiul.uas.bakulproperti.model.Properti
import com.musfiul.uas.bakulproperti.model.ResponModel
import com.musfiul.uas.bakulproperti.model.User
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Response
import java.io.File

class UploadGambar : AppCompatActivity() {
    lateinit var btn_tambahGambar : Button
    lateinit var toolbar : Toolbar

    lateinit var div_kosong: LinearLayout
    lateinit var rv_gambar : RecyclerView;

    //     alert dialog pilih kirim atau kembali pilih gambar
    var alertDialog:AlertDialog? = null

//    sharedpref
    lateinit var sp: SharedPref
    var user: User? = null
    var id_user : Int =0

    var jumlahGambar = 0
    var propertis = ArrayList<Properti>()
    lateinit var properti: Properti


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_gambar)

        sp = SharedPref(this)
        user = sp.getUser()
        id_user = user!!.id


        init()

        val intent = intent

        if(intent.hasExtra("extra")){

            val data= intent.getStringExtra("extra")
            properti = Gson().fromJson<Properti>(data, Properti::class.java)

//            untuk menghilangkan inten
            intent.removeExtra("extra")
        }

        displayGambar()

        //        setToolBar
        Helper().setToolbar(this, toolbar , "List Gambar")
    }

    private fun init(){
        btn_tambahGambar = findViewById(R.id.btn_tambahGambar)
        toolbar = findViewById(R.id.toolbar)
        div_kosong = findViewById(R.id.div_kosong)
        rv_gambar = findViewById(R.id.rv_gambar)

        btn_tambahGambar.setOnClickListener{
//            startActivity(Intent(this, TambahAlamatActivity::class.java))
//              mulai ambil gambar
            imagePicker()
        }
    }

    //    image picker stage 2
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val uri = it.data?.data!!
            // Use the uri to load the image
            // Only if you are not using crop feature:
//            uri?.let { galleryUri ->
//                contentResolver.takePersistableUriPermission(
//                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
//                )
//
//            }

            val fileUri: Uri = uri

            displayUpload(File(fileUri.path))

        }
    }

    @SuppressLint("InflateParams")
    private fun displayUpload(file: File) {
        val view = layoutInflater
        val layout = view.inflate(R.layout.view_upload, null)

        val btn_upload: Button = layout.findViewById(R.id.btn_upload)
        val btn_ambilGambar: Button = layout.findViewById(R.id.btn_ambilGambar)
        val image: ImageView = layout.findViewById(R.id.image)
//        val progressBar : ProgressBar = layout.findViewById(R.id.pb_bar)

        Picasso
            .get()
            .load(file)
            .into(image)

        btn_upload.setOnClickListener {
            upload(file)
        }

        btn_ambilGambar.setOnClickListener {
            imagePicker()
        }

//        menampilkan alert dialog pilih kirim atau kembali pilih gambar

        alertDialog = AlertDialog.Builder(this).create()
        alertDialog!!.setView(layout)
        alertDialog!!.setCancelable(true)
        alertDialog!!.show()
    }

    private fun upload(file: File) {

//      //     sweet alert dialog
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Loading"
        pDialog.setCancelable(false)
        pDialog.show()

        val fileMultipart = file.toMultipartBody("gambar")

        ApiConfig.instanceRetrofit.uploadGambar(id_user, fileMultipart!!).enqueue(object : retrofit2.Callback<ResponModel>{


            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

                if(response.isSuccessful){
                    if(response.body()!!.success == 1){

                        //                    menghilangkan progress bar
                        pDialog.dismiss()

                        SweetAlertDialog(this@UploadGambar, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Success!")
                            .setContentText("Upload Properti Berhasil.")
                            .setConfirmClickListener {
                                it.dismissWithAnimation()

                                alertDialog!!.dismiss()

//                              mengambil data dari server
                                var respon = response.body()
                                propertis = respon!!.propertis
                                properti = propertis[0]
                                jumlahGambar = respon!!.jumlahGambar

                                //                                menampilkan gambar kembali setelah upload berhasil
                                displayGambar()


                                // auto back dengan onbackpressed
//                                onBackPressed()
                            }
                            .show()

                    }else{
                        SweetAlertDialog(this@UploadGambar, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Success!")
                            .setContentText(response.body()!!.message)
                            .setConfirmClickListener {
                                it.dismissWithAnimation()

//                              menghilangkan alert dialog pilih gambar
                                alertDialog!!.dismiss()

                                // auto back dengan onbackpressed
//                                onBackPressed()
                            }
                            .show()
                    }

                }else{
                    SweetAlertDialog(this@UploadGambar, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Gagal Upload!")
                        .setContentText(response.message())
                        .setConfirmClickListener {
                            it.dismissWithAnimation()

//                              menghilangkan alert dialog pilih gambar
                            alertDialog!!.dismiss()

                            // auto back dengan onbackpressed
//                            onBackPressed()
                        }
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {

                SweetAlertDialog(this@UploadGambar, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Gagal!")
                    .setContentText(t.message!!)
                    .setConfirmClickListener {
                        it.dismissWithAnimation()

//                              menghilangkan alert dialog pilih gambar
                        alertDialog!!.dismiss()

                        // auto back dengan onbackpressed
//                        onBackPressed()
                    }
                    .show()
            }

        })

    }

    //    fungsi ini digunakan untuk mengambil gambar
    private fun imagePicker(){
        ImagePicker.with(this)
            .crop()
            .maxResultSize(512, 512, true)
            .provider(ImageProvider.BOTH) //Or bothCameraGallery()
            .createIntentFromDialog { launcher.launch(it) }//sudah mendapatkan gambar dan mengirimkannya

    }


    private fun displayGambar(){

        if(jumlahGambar <=5){
            btn_tambahGambar.visibility = View.VISIBLE
        }else{
            btn_tambahGambar.visibility = View.GONE
        }

        if(properti.get_gambar.isEmpty()){
            div_kosong.visibility = View.VISIBLE
        }else{
            div_kosong.visibility = View.GONE
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL;

        rv_gambar.adapter = AdapterGambarUpload(this,properti.get_gambar, object :AdapterGambarUpload.Listeners{
            override fun onClicked(data: Gambar) {

//      //     sweet alert dialog
                val pDialog = SweetAlertDialog(this@UploadGambar, SweetAlertDialog.PROGRESS_TYPE)
                pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
                pDialog.titleText = "Loading"
                pDialog.setCancelable(false)
                pDialog.show()

                ApiConfig.instanceRetrofit.deleteGambar(data.gambar!!, id_user ).enqueue(object : retrofit2.Callback<ResponModel>{


                    override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

                        if(response.isSuccessful){
                            if(response.body()!!.success == 1){

                                //                    menghilangkan progress bar
                                pDialog.dismiss()

                                SweetAlertDialog(this@UploadGambar, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success!")
                                    .setContentText("Delete Properti Berhasil.")
                                    .setConfirmClickListener {
                                        it.dismissWithAnimation()


////                              mengambil data dari server
                                        var respon = response.body()

                                        if(respon!!.propertis[0].isNotNull()){
                                            propertis = respon!!.propertis
                                            properti = propertis[0]
                                            jumlahGambar = respon!!.jumlahGambar
                                        }else{
                                            propertis.clear()
                                            properti = Properti()
                                            jumlahGambar = 0
                                        }

                                        //                                menampilkan gambar kembali setelah upload berhasil
                                        displayGambar()

//


                                        // auto back dengan onbackpressed
//                                        onBackPressed()
                                    }
                                    .show()


                            }else{
                                SweetAlertDialog(this@UploadGambar, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Success!")
                                    .setContentText(response.body()!!.message)
                                    .setConfirmClickListener {
                                        it.dismissWithAnimation()


                                        // auto back dengan onbackpressed
//                                        onBackPressed()
                                    }
                                    .show()
                            }

                        }else{
                            SweetAlertDialog(this@UploadGambar, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Gagal Upload!")
                                .setContentText(response.message())
                                .setConfirmClickListener {
                                    it.dismissWithAnimation()

//                              menghilangkan alert dialog pilih gambar
                                    alertDialog!!.dismiss()

                                    // auto back dengan onbackpressed
//                                    onBackPressed()
                                }
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<ResponModel>, t: Throwable) {

                        SweetAlertDialog(this@UploadGambar, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Gagal!")
                            .setContentText(t.message!!)
                            .setConfirmClickListener {
                                it.dismissWithAnimation()

//                              menghilangkan alert dialog pilih gambar
                                alertDialog!!.dismiss()

                                // auto back dengan onbackpressed
//                                onBackPressed()
                            }
                            .show()
                    }

                })

            }

        })
        rv_gambar.layoutManager = layoutManager;
    }

    //    display alamat aditaruh didalam on resume
//    karena ketika pertama kali buka alamat belum diperlukan
//    saat kembali ke alamat baru akan dijalankan
    override fun onResume() {
        displayGambar()
        super.onResume()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}