package com.musfiul.uas.bakulproperti.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
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
import com.musfiul.uas.bakulproperti.adapter.AdapterPropertiSlider
import com.musfiul.uas.bakulproperti.helper.Helper
import com.musfiul.uas.bakulproperti.model.Properti

class DetailPropertiActivity : AppCompatActivity() {
    lateinit var vp_slider:ViewPager
    lateinit var tv_namaProperti:TextView
    lateinit var tv_location:TextView
    lateinit var tv_Harga:TextView
    lateinit var tvDeskripsi:TextView
    lateinit var tv_phone:TextView
    lateinit var tv_email:TextView
    lateinit var btn_email: ImageView
    lateinit var btn_phone: ImageView

//    mapView
//    lateinit var mapView: FrameLayout

//    toolbar
    lateinit var toolbar: Toolbar
    lateinit var btn_tambah: RelativeLayout

//    data Properti
    lateinit var properti:Properti

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_properti)
        init()

//        get Data properti from AdapterProperti
        val dataProperti = intent.getStringExtra("extra")
        properti = Gson().fromJson<Properti>(dataProperti, Properti::class.java)

        if(properti.longitude == null||properti.lattitude == null){
            long = 122.00
            latt = 10.00
        }else{
            long = properti.longitude!!.toDouble()
            latt  = properti.lattitude!!.toDouble()
        }

        displayProperti()
        displayMap()
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

    private fun displayProperti() {
        var ArrayListSrc = ArrayList<String>()
        for(gambar in properti.get_gambar){
//            null exception !!
            ArrayListSrc.add(gambar.gambar!!)
        }
        val adapaterSlider = AdapterPropertiSlider(ArrayListSrc, this)
        vp_slider.adapter = adapaterSlider

        tv_namaProperti.text = properti.nama_properti
        tvDeskripsi.text = properti.deskripsi
        tv_Harga.text = Helper().gantiRupiah(properti.harga)
        tv_location.text = properti.provinsi+", "+properti.kabupaten+", "+properti.kecamatan
        tv_email.text = properti.email
        tv_phone.text = properti.phone

    }

    private fun init(){
        vp_slider = findViewById(R.id.vp_slider)
        tv_namaProperti = findViewById(R.id.tv_namaProperti)
        tv_location = findViewById(R.id.tv_location)
        tv_Harga = findViewById(R.id.tv_Harga)
        tvDeskripsi = findViewById(R.id.tvDeskripsi)
        btn_email = findViewById(R.id.btn_email)
        btn_phone = findViewById(R.id.btn_phone)
        tv_phone = findViewById(R.id.tv_phone)
        tv_email = findViewById(R.id.tv_email)
//
//        mapview
        mapView = findViewById(R.id.mapView)

//        toolbar
        toolbar = findViewById(R.id.toolbar)
        Helper().setToolbar(this, toolbar, "Detail Properti")
        btn_tambah = findViewById(R.id.btn_tambah)

        btn_email.setOnClickListener {
            val email = Intent(Intent.ACTION_SEND)
            email.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(properti.email.toString()))

            //need this to prompts email client only

            email.type = "message/rfc822"

            startActivity(Intent.createChooser(email, "Choose an Email client :"))
        }

        btn_phone.setOnClickListener {
            val installed: Boolean = appInstalledOrNot("com.whatsapp")
            val phone = "62"+properti.phone

            if (installed) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data =
                    Uri.parse("http://api.whatsapp.com/send?phone=" + phone)
                startActivity(intent)
            } else {
                Toast.makeText(this@DetailPropertiActivity,
                    "Whatsapp not installed on your device",
                    Toast.LENGTH_SHORT)
            }
        }
    }

    private fun appInstalledOrNot(url: String): Boolean {
        val packageManager = packageManager
        val app_installed: Boolean
        app_installed = try {
            packageManager.getPackageInfo(url, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }


}