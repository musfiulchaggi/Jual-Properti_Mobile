package com.musfiul.uas.bakulproperti.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.*
import com.google.gson.Gson
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*

import com.mapbox.search.ResponseInfo
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
import com.mapbox.search.SearchOptions
import com.mapbox.search.SearchSelectionCallback
import com.mapbox.search.common.AsyncOperationTask
import com.mapbox.search.result.SearchResult
import com.mapbox.search.result.SearchSuggestion



import com.musfiul.uas.bakulproperti.R
import com.musfiul.uas.bakulproperti.app.ApiConfig
import com.musfiul.uas.bakulproperti.app.ApiConfigAlamat
import com.musfiul.uas.bakulproperti.helper.Helper
import com.musfiul.uas.bakulproperti.helper.SharedPref
import com.musfiul.uas.bakulproperti.model.ModelAlamat
import com.musfiul.uas.bakulproperti.model.Properti
import com.musfiul.uas.bakulproperti.model.ResponModel
import com.musfiul.uas.bakulproperti.model.User
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import retrofit2.Call
import retrofit2.Response

class UploadLokasi : AppCompatActivity() {


    lateinit var toolbar : Toolbar

    lateinit var div_provinsi: RelativeLayout
    lateinit var div_kota: RelativeLayout
    lateinit var div_kecamatan: RelativeLayout
    lateinit var div_mapView: RelativeLayout

    lateinit var spn_provinsi : Spinner
    lateinit var spn_kecamatan : Spinner
    lateinit var spn_kota : Spinner

    lateinit var pb:ProgressBar

    lateinit var btn_simpan:Button

    lateinit var properti: Properti

    var provinsi = ModelAlamat()
    var kota = ModelAlamat()
    var kecamatan = ModelAlamat()

//    inisialisai sp
    lateinit var sp:SharedPref
    var user = User()
    var user_id = User().id

    //    mendapatkan lokasi

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var long: String? = null
    private var latt: String? = null

    //    mapbox
    var mapView: MapView? = null

    // create object for annotation marker
    var annotationApi : AnnotationPlugin? = null
    lateinit var annotationConfig : AnnotationConfig
    var layerIdd = "map_annotation"

    var pointAnnotationManager : PointAnnotationManager? = null

    var markerList : PointAnnotationOptions? = null
    val accessToken : String =  "pk.eyJ1IjoibXVzZml1bGNoYWdnaTAxIiwiYSI6ImNsanBib2FqMTFzanczcW51NDlveWpzOGQifQ.Gx0PV-RqP2h3xzc_weEiMw"

//    mapbox Annotations


//    mapbox search
    private lateinit var searchEngine: SearchEngine
    private lateinit var searchRequestTask: AsyncOperationTask

// mapbox search function
private val searchCallback = object : SearchSelectionCallback {

    override fun onSuggestions(suggestions: List<SearchSuggestion>, responseInfo: ResponseInfo) {
        if (suggestions.isEmpty()) {
            Log.i("SearchApiExample", "No suggestions found")
        } else {
            Log.i("SearchApiExample", "Search suggestions: $suggestions.\nSelecting first suggestion...")
            searchRequestTask = searchEngine.select(suggestions.first(), this)
        }
    }

    override fun onResult(
        suggestion: SearchSuggestion,
        result: SearchResult,
        responseInfo: ResponseInfo
    ) {
        Log.i("SearchApiExample", "Search result: $result")
    }

    override fun onResults(
        suggestion: SearchSuggestion,
        results: List<SearchResult>,
        responseInfo: ResponseInfo
    ) {
        Log.i("SearchApiExample", "Category search results: $results")
    }

    override fun onError(e: Exception) {
        Log.i("SearchApiExample", "Search error", e)
    }
}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_lokasi)

        init()

        sp = SharedPref(this)
        user = sp.getUser()!!
        user_id = user.id

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        var intent= intent

        if(intent.hasExtra("extra")){
            val data= intent.getStringExtra("extra")
            properti = Gson().fromJson<Properti>(data, Properti::class.java)

//            untuk menghilangkan inten
            intent.removeExtra("extra")
        }

        //        setToolBar
        Helper().setToolbar(this, toolbar , "Lokasi Properti")

        getProvinsi()
    }

    fun init(){
        btn_simpan = findViewById(R.id.btn_simpan)

        spn_provinsi = findViewById(R.id.spn_provinsi)
        spn_kota = findViewById(R.id.spn_kota)
        spn_kecamatan = findViewById(R.id.spn_kecamatan)

        pb = findViewById(R.id.pb)
        div_provinsi = findViewById(R.id.div_provinsi)
        div_kota = findViewById(R.id.div_kota)
        div_kecamatan = findViewById(R.id.div_kecamatan)

        div_mapView = findViewById(R.id.div_mapView)

        //        mapview
        mapView = findViewById(R.id.mapView)

//        toolbar
        toolbar = findViewById(R.id.toolbar)

        btn_simpan.setOnClickListener{
            simpanAlamat()
        }


    }

    fun simpanAlamat(){


        if(provinsi.id == 0){
            toast("Silahkan Pilih Provinsi")
            return
        }
        if(kota.id == 0){
            toast("Silahkan Pilih Kota")
            return
        }
        if(kecamatan.id == 0){
            toast("Silahkan Pilih Kecamatan")
            return
        }

        if(latt == null){
            toast("Silahkan Pilih Kecamatan")
            return
        }
        if(long == null){
            toast("Silahkan Pilih Kecamatan")
            return
        }

        properti.lattitude = latt
        properti.longitude = long
        properti.provinsi = provinsi.nama
        properti.kabupaten = kota.nama
        properti.kecamatan = kecamatan.nama

        insert(properti)
    }

    private fun toast(string: String){
        Toast.makeText(this,string ,Toast.LENGTH_SHORT).show()
    }

    private fun getProvinsi(){
        ApiConfigAlamat.instanceRetrofit.getProvinsi().enqueue(object : retrofit2.Callback<ResponModel>{


            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

                if(response.isSuccessful){
                    val respon = response.body()!!
                    pb.visibility = View.GONE
                    div_provinsi.visibility = View.VISIBLE


                    var arrayString = ArrayList<String>()
                    arrayString.add("Pilih Provinsi ")

                    val listProfinsi = respon.provinsi

                    for(prov in listProfinsi){
                        arrayString.add(prov.nama!!)
                    }



                    Log.d("Isi list",arrayString.toString() )



                    val adapter = ArrayAdapter<Any>(this@UploadLokasi, R.layout.item_spinner, arrayString.toTypedArray())
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spn_provinsi.adapter = adapter

//                    spn_provinsi.setOnItemClickListener { adapterView, view, position, id ->
//                        if(position !=0 ){
//                            val idProv = listProfinsi[position-1].id
//                            Log.d("respon", "Provinsi id:"+ listProfinsi[position-1].id+" "+ "name: "+ listProfinsi[position-1].nama)
//
//                            getKota(idProv)
//                        }
//                    }

                    spn_provinsi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            position: Int,
                            id: Long,
                        ) {
                            if(position != 0){
                                provinsi = listProfinsi[position-1]
                                val idProv = provinsi.id

                                Log.d("respon", "kota id:"+ listProfinsi[position-1].id_provinsi+" "+ "name: "+ listProfinsi[position-1].id_provinsi+" "+ "position: "+ position)

                                getKota(idProv!!)
                            }

                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }

                    }
                }else{
                    Log.d("Error", "gagal memuat data:" + response.message())
                }


            }

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
//                pb.visibility = View.GONE
//                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_LONG ).show()
            }

        })

    }

    fun getKota(id:Int){
        pb.visibility = View.VISIBLE
        ApiConfigAlamat.instanceRetrofit.getKota(id).enqueue(object : retrofit2.Callback<ResponModel>{


            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

                if(response.isSuccessful){
                    val respon = response.body()!!
                    pb.visibility = View.GONE
                    div_kota.visibility = View.VISIBLE


                    var arrayString = ArrayList<String>()
                    arrayString.add("Pilih Kota")
                    var listKota = respon.kota_kabupaten

                    for(kota_kabupaten in listKota){
                        arrayString.add(kota_kabupaten.nama!!)
                    }

                    val adapter = ArrayAdapter<Any>(this@UploadLokasi, R.layout.item_spinner, arrayString.toTypedArray())
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spn_kota.adapter = adapter

//                    spn_kota.setOnItemClickListener { adapterView, view, position, id ->
//                        if(position-1 != 0){
//                            val idKota = listKota[position-1].id
//                            Log.d("respon", "kota id:"+ listKota[position-1].id+" "+ "name: "+ listKota[position-1].nama)
//
//                            getKecamatan(idKota)
//                        }
//                    }

                    spn_kota.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            position: Int,
                            id: Long,
                        ) {
                            if(position != 0){
                                kota = listKota[position-1]
                                val idKota = kota.id
                                Log.d("respon", "kota id:"+ kota.id_kota+" "+ "name: "+ kota.id_kota)

                                getKecamatan(idKota!!)



                            }

                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }

                    }

                }else{
                    Log.d("Error", "gagal memuat data:" + response.message())
                }


            }

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
//                pb.visibility = View.GONE
//                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_LONG ).show()
            }

        })

    }

    fun getKecamatan(id:Int){
        pb.visibility = View.VISIBLE
        ApiConfigAlamat.instanceRetrofit.getKecamatan(id).enqueue(object : retrofit2.Callback<ResponModel>{


            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

                if(response.isSuccessful){
                    val respon = response.body()!!
                    pb.visibility = View.GONE
                    div_kecamatan.visibility = View.VISIBLE


                    var arrayString = ArrayList<String>()
                    arrayString.add("Pilih Kecamatan")
                    var listKecamatan = respon.kecamatan

                    for(kecamatan in listKecamatan){
                        arrayString.add(kecamatan.nama!!)
                    }

                    val adapter = ArrayAdapter<Any>(this@UploadLokasi, R.layout.item_spinner, arrayString.toTypedArray())
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spn_kecamatan.adapter = adapter

//                    spn_kecamatan.setOnItemClickListener { adapterView, view, position, id ->
//                        if(position-1!= 0){
//                            val idKecamatan = listKecamatan[position-1]
//                            Log.d("respon", "kota id:"+ listKecamatan[position-1].id+" "+ "name: "+ listKecamatan[position-1].nama)
//                        }
//                    }

                    spn_kecamatan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            position: Int,
                            id: Long,
                        ) {
                            if(position != 0){
                                kecamatan = listKecamatan[position-1]
                                val idKecamatan = listKecamatan[position-1].id
                                Log.d("respon", "kota id:"+ listKecamatan[position-1].id+" "+ "name: "+ listKecamatan[position-1].nama)

                                tampilMap()
                            }

                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }

                    }

                }else{
                    Log.d("Error", "gagal memuat data:" + response.message())

                }


            }

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
//                pb.visibility = View.GONE
//                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_LONG ).show()
            }

        })

    }

    private fun tampilMap() {
        pb.visibility = View.VISIBLE


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

//      cek permissions
        if (ActivityCompat.checkSelfPermission(this@UploadLokasi,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {

            getCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(this@UploadLokasi,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                44)

            getCurrentLocation()
        }


    }

    private fun displayMap(latt: String,long: String) {


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

        searchEngine = SearchEngine.createSearchEngineWithBuiltInDataProviders(
            SearchEngineSettings(accessToken)
        )

        searchRequestTask = searchEngine.search(
            "Turen ",
            SearchOptions(limit = 5),
            searchCallback
        )

    }


    fun createMarker(){

        //        add markerList
        var bitmap = convertDrawableToBitmap(AppCompatResources.getDrawable(this, R.drawable.ic_location))!!

        markerList = PointAnnotationOptions()
            .withPoint(Point.fromLngLat(long!!.toDouble(),latt!!.toDouble()))
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
            CameraOptions.Builder().center(Point.fromLngLat(long!!.toDouble(),latt !!.toDouble()))
                .zoom(15.0)
                .build()
        )
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this@UploadLokasi,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                44)
        }


        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

            override fun isCancellationRequested() = false
        })
            .addOnSuccessListener { location: Location? ->

                if (location == null) {
//                    Toast.makeText(this, "Cannot get location.", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this, "Hidupkan layanan lokasi anda.", Toast.LENGTH_LONG).show()
                    latt = "8.00"
                    long = "188.0"

//              menampilkan map
                    pb.visibility = View.GONE
                    div_mapView.visibility = View.VISIBLE

                    displayMap(latt!!,long!!)

                }else {

                    latt = location.latitude.toString()
                    long = location.longitude.toString()

//              menampilkan map
                    pb.visibility = View.GONE
                    div_mapView.visibility = View.VISIBLE

                    displayMap(latt!!,long!!)

                }

            }


    }



    //    insert data alamat properti to database
    private fun insert(data: Properti){

        //      //     sweet alert dialog
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Loading"
        pDialog.setCancelable(false)
        pDialog.show()


        ApiConfig.instanceRetrofit.uploadLokasi(data.id , user_id, data.lattitude!! ,data.longitude!! , data.provinsi!!,data.kabupaten!! ,data.kecamatan!! ).enqueue(object : retrofit2.Callback<ResponModel>{


            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

                if(response.isSuccessful){
                    if(response.body()!!.success == 1){

                        //                    menghilangkan progress bar
                        pDialog.dismiss()

                        SweetAlertDialog(this@UploadLokasi, SweetAlertDialog.SUCCESS_TYPE)
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
                        SweetAlertDialog(this@UploadLokasi, SweetAlertDialog.ERROR_TYPE)
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
                    SweetAlertDialog(this@UploadLokasi, SweetAlertDialog.ERROR_TYPE)
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

                SweetAlertDialog(this@UploadLokasi, SweetAlertDialog.ERROR_TYPE)
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

    //  digunakan untuk memunculkan fungsi onbackPressed
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

//    mapbox search on destroy
    override fun onDestroy() {
        searchRequestTask.cancel()
        super.onDestroy()
    }
}