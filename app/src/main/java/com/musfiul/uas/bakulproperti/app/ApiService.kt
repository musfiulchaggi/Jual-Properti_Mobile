package com.musfiul.uas.bakulproperti.app


//import com.musfiul.uts.myapplication.model.CheckOut
//import com.musfiul.uts.myapplication.model.rajaongkir.ResponOngkir
import com.musfiul.uas.bakulproperti.model.Properti
import com.musfiul.uas.bakulproperti.model.ResponModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") nomortlp: String,
        @Field("password") password: String,
//        @Field("fcm")fcm: String
    ): Call<ResponModel>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
//        @Field("fcm")fcm: String
    ): Call<ResponModel>

   //upload Gambar properti
    @Multipart
    @POST("properti/upload/{id}")
    fun uploadGambar(
        @Path("id") id: Int,
        @Part image: MultipartBody.Part
    ): Call<ResponModel>

    //delete Gambar properti
    @POST("properti/delete/{id}/{user_id}")
    fun deleteGambar(
        @Path("id") id: String,
        @Path("user_id") user_id: Int
    ): Call<ResponModel>

    //upload lokasi properti
    @FormUrlEncoded
    @POST("properti/lokasi")
    fun uploadLokasi(
        @Field("id") id: Int,
        @Field("user_id") user_id: Int,
        @Field("lattitude") lattitude: String,
        @Field("longitude") longitude: String,
        @Field("provinsi") provinsi: String,
        @Field("kabupaten") kabupaten: String,
        @Field("kecamatan") kecamatan: String,
    ): Call<ResponModel>

    //upload lokasi properti
    @FormUrlEncoded
    @POST("properti/data")
    fun uploadData(
        @Field("id") id: Int,
        @Field("user_id") user_id: Int,
        @Field("nama_properti") nama_properti: String,
        @Field("harga") harga: Int,
        @Field("phone") phone: String,
        @Field("email") email: String,
        @Field("deskripsi") deskripsi: String,
    ): Call<ResponModel>

    //upload lokasi properti
    @POST("delete/{id}/{user_id}")
    fun hapusData(
        @Path("id") id: Int,
        @Path("user_id") user_id: Int,
    ): Call<ResponModel>

    @GET("properti")
    fun getProperti(): Call<ResponModel>

    @GET("getPropertiById/{id}")
    fun getPropertiById(
       @Path("id") id: Int
    ): Call<ResponModel>

//    fungsi @header untuk menggunakan head
//    fungsi @query digunakan untuk permintaan ?id=
    @GET("provinsi")
    fun getProvinsi(
//        @Query("id_provinsi") id:Int,
//        @Header("key") key:String,
    ): Call<ResponModel>

    @GET("kota")
    fun getKota(
//        @Header("key") key:String,
        @Query("id_provinsi") id:Int
    ): Call<ResponModel>

    @GET("kecamatan")
    fun getKecamatan(
        @Query("id_kota") id: Int
    ): Call<ResponModel>

//        @FormUrlEncoded digunakan untuk mengirimkan data satu persatu
//    @FormUrlEncoded
//    @POST("cost")
//    fun ongkir(
//        @Header("key") key: String,
//        @Field("origin") origin: String,
//        @Field("destination") destination: String,
//        @Field("weight") weight: Int,
//        @Field("courier") courier: String
//    ): Call<ResponOngkir>
//
//    //    tidak memakai     @FormUrlEncoded karena akan mengirimkan data berupa sebuah objek secara utuh
//    @POST("checkout")
//    fun checkOut(
//        @Body data: CheckOut
//    ): Call<ResponModel>
//
//    @GET("history/{id}")
//    fun getRiwayat(
////        @Path digunakan untuk mengisi nilai path menjadi nilai tertentu
//        @Path("id") id: Int
//    ): Call<ResponModel>
//
//
//    @POST("checkout/batal/{id}")
//    fun batalChekout(
//        @Path("id") id: Int
//    ): Call<ResponModel>
//

}