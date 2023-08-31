package com.musfiul.uas.bakulproperti.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.musfiul.uas.bakulproperti.R
import com.musfiul.uas.bakulproperti.adapter.AdapterGambarJual
import com.musfiul.uas.bakulproperti.app.ApiConfig
import com.musfiul.uas.bakulproperti.helper.Helper
import com.musfiul.uas.bakulproperti.helper.SharedPref
import com.musfiul.uas.bakulproperti.model.Properti
import com.musfiul.uas.bakulproperti.model.ResponModel
import com.musfiul.uas.bakulproperti.model.User
import retrofit2.Call
import retrofit2.Response

class DataPropertiTambahActivity : AppCompatActivity() {
    lateinit var toolbar:Toolbar
    lateinit var btn_tambahGambar:ImageView
    lateinit var btn_tambahDataProperti:ImageView
    lateinit var rv_gambar:RecyclerView
    lateinit var tv_nama:TextView
    lateinit var tv_Harga:TextView
    lateinit var tvDeskripsi:TextView
    lateinit var tv_phone:TextView
    lateinit var tv_email:TextView
    lateinit var tv_location:TextView
    lateinit var btn_tambahLokasi:ImageView
    lateinit var btn_simpan:Button

    lateinit var layoutDataProperti:LinearLayout
    lateinit var layout_dataLokasi:LinearLayout

    lateinit var div_kosongGambar:LinearLayout
    lateinit var div_kosongLokasi:LinearLayout
    lateinit var div_kosongData:LinearLayout

    //    mapbox
    var mapView: MapView? = null

    // create object for annotation marker
    var annotationApi : AnnotationPlugin? = null
    lateinit var annotationConfig : AnnotationConfig
    var layerIdd = "map_annotation"

    var pointAnnotationManager : PointAnnotationManager? = null

    var markerList : PointAnnotationOptions? = null

    var long: Double? = null
    var latt: Double? = null

    //    sharedpref
    lateinit var sp: SharedPref
    var user: User? = null
    var id_user : Int =0

    var listProperti : ArrayList<Properti> = ArrayList()
    var properti = Properti()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_properti_tambah)


//        inisialisasi sp
        sp = SharedPref(this)
        user = sp.getUser()!!
        id_user = user!!.id

//        init
        init()

        //        setToolBar
        Helper().setToolbar(this, toolbar , "Jual Properti")

        getDataOwnProperti(id_user)

    }

    private fun getDataOwnProperti(id: Int) {

        val intent = intent

        if (intent.hasExtra("extra")) {

            //        get Data properti from AdapterProperti
            val dataProperti = intent.getStringExtra("extra")
            properti = Gson().fromJson<Properti>(dataProperti, Properti::class.java)

            listProperti.clear()
            //                    memasukkan kedalam daftar produk
            listProperti.add(properti)

            displayProperti()

            //            untuk menghilangkan inten
            intent.removeExtra("extra")

        } else {
            // Do something else
            ApiConfig.instanceRetrofit.getPropertiById(id).enqueue(object : retrofit2.Callback<ResponModel>{
                override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

                    val respon = response.body()!!

                    if(respon.success== 1){
//                    membersihkan semua data array
                        listProperti.clear()
                        //                    memasukkan kedalam daftar produk

                        properti = respon.properti
                        listProperti.add(properti)

                        displayProperti()

                    }else{

                        //                    membersihkan semua data array
                        listProperti.clear()

                        displayProperti()
                    }

                }

                override fun onFailure(call: Call<ResponModel>, t: Throwable) {

//                pb.visibility = View.GONE
//                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_LONG ).show()

                }

            })
        }

    }

    private fun displayProperti() {

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL;

        rv_gambar.layoutManager = layoutManager;
        rv_gambar.adapter=AdapterGambarJual(this,properti.get_gambar)

        if(properti.get_gambar.isEmpty()){
            div_kosongGambar.visibility = View.VISIBLE
            rv_gambar.visibility = View.GONE
        }else{
            div_kosongGambar.visibility = View.GONE
            rv_gambar.visibility = View.VISIBLE
        }

        if(properti.email == null){
            layoutDataProperti.visibility = View.GONE
            div_kosongData.visibility = View.VISIBLE
        }else{
            layoutDataProperti.visibility = View.VISIBLE
            div_kosongData.visibility = View.GONE
        }

        if(properti.lattitude == null){
            layout_dataLokasi.visibility = View.GONE
            mapView!!.visibility = View.GONE

            div_kosongLokasi.visibility = View.VISIBLE
        }else{
            layout_dataLokasi.visibility = View.VISIBLE
            div_kosongLokasi.visibility = View.GONE

            mapView!!.visibility = View.VISIBLE

            long = properti.longitude!!.toDouble()
            latt = properti.lattitude!!.toDouble()
            displayMap()
        }

        tv_nama.text = properti.nama_properti
        tvDeskripsi.text = properti.deskripsi
        tv_phone.text = properti.phone
        tv_email .text = properti.email
        tv_location.text = properti.provinsi+", "+properti.kabupaten+", "+properti.kecamatan
        tv_Harga.text = Helper().gantiRupiah(properti.harga)


    }

    private fun init(){
        toolbar = findViewById(R.id.toolbar)
        btn_tambahGambar = findViewById(R.id.btn_tambahGambar)
        btn_tambahDataProperti = findViewById(R.id.btn_tambahDataProperti)
        rv_gambar = findViewById(R.id.rv_gambar)
        tv_nama = findViewById(R.id.tv_namaProperti)
        tvDeskripsi = findViewById(R.id.tvDeskripsi)
        tv_phone = findViewById(R.id.tv_phone)
        tv_email = findViewById(R.id.tv_email)
        tv_location = findViewById(R.id.tv_location)
        btn_tambahLokasi = findViewById(R.id.btn_tambahDataLokasi)
        btn_simpan = findViewById(R.id.btn_simpan)
        tv_Harga = findViewById(R.id.tv_Harga)

        layoutDataProperti = findViewById(R.id.layout_dataProperti)
        layout_dataLokasi = findViewById(R.id.layout_dataLokasi)

        div_kosongGambar = findViewById(R.id.div_kosongGambar)
        div_kosongLokasi = findViewById(R.id.div_kosongLokasi)
        div_kosongData= findViewById(R.id.div_kosongData)

        mapView = findViewById(R.id.mapView)



        btn_tambahGambar.setOnClickListener {
            val intent = Intent(this@DataPropertiTambahActivity, UploadGambar::class.java)
            intent.putExtra("extra", Gson().toJson(properti, Properti::class.java))
            startActivity(intent)
        }

        btn_tambahDataProperti.setOnClickListener {
            val intent = Intent(this@DataPropertiTambahActivity, UploadDataProperty::class.java)
            intent.putExtra("extra", Gson().toJson(properti, Properti::class.java))
            startActivity(intent)
        }

        btn_tambahLokasi.setOnClickListener {
            val intent = Intent(this@DataPropertiTambahActivity, UploadLokasi::class.java)
            intent.putExtra("extra", Gson().toJson(properti, Properti::class.java))
            startActivity(intent)
        }


    }

    fun displayMap(){
        //        display map
        mapView = findViewById(R.id.mapView)
        mapView?.getMapboxMap()?.loadStyleUri(
            Style.MAPBOX_STREETS,
            object : Style.OnStyleLoaded{
                override fun onStyleLoaded(style: Style) {

                    zoomCamera()


//                    addmarker
                    annotationApi = mapView?.annotations
                    annotationConfig = AnnotationConfig(
                        layerId = layerIdd
                    )

//                    initialize point anotation manager
                    pointAnnotationManager = annotationApi!!.createPointAnnotationManager(annotationConfig)

                    createMarker()
                }
            }
        )
    }

    fun createMarker(){
        //        add markerList
        var bitmap = convertDrawableToBitmap(AppCompatResources.getDrawable(this, R.drawable.ic_location))!!

        markerList = PointAnnotationOptions()
            .withPoint(Point.fromLngLat(long!!,latt!!))
            .withIconImage(bitmap)

        pointAnnotationManager?.create(markerList!!)
    }


    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }


    private fun zoomCamera(){




        mapView!!.getMapboxMap().setCamera(


            CameraOptions.Builder().center(Point.fromLngLat(long!!,latt !!))
                .zoom(17.0)
                .build()

        )
    }

    override fun onResume() {
        getDataOwnProperti(id_user)

        super.onResume()
    }

    override fun onSupportNavigateUp(): Boolean {
        //    Merubah Data Properti

        val intent = Intent("event:addproperti")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

        onBackPressed()
        return super.onSupportNavigateUp()
    }
}