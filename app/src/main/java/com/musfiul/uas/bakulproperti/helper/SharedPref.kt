package com.musfiul.uas.bakulproperti.helper

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.musfiul.uas.bakulproperti.model.User

class SharedPref(activity: Activity) {
    val login = "login"
    val nama = "nama"
    val phone = "phone"
    val email = "email"
    val user = "user"

    val mypref = "MAIN_PRF"
    val sp:SharedPreferences



    init {
        sp = activity.getSharedPreferences(mypref, Context.MODE_PRIVATE)
    }

    //    membuat fungsi set Status
//    mengubah nilai dari sp menjadi true or false
    fun setStatusLogin(status:Boolean){
        sp.edit().putBoolean(login, status).commit()

    }

    //    membuat fungsi get status
//    mendapatkan nilai dari sp
    fun getStatusLogin():Boolean{

//    nilai default dari sp adalah false
        return sp.getBoolean(login,false)
    }

//    membuat fungsi untuk menampung seluruh data dari class user dan merubahnya
//    kedalam format json

    fun setUser(value: User){
//      merubah data class user menjadi format json
        val data: String = Gson().toJson(value, User::class.java)

        sp.edit().putString(user, data).apply()
    }

//    membuat fungsi untuk mengambil data sp: string
//     menjadi format class user
//    fungsi tanda ? digunakan untuk pengembalian nullable

    fun getUser(): User?{
//        mengambil data dari user dengan fungsi ?: if else > null
        val data:String = sp.getString(user, null) ?: return null
        val user: User = Gson().fromJson<User>(data, User::class.java)

        return user
    }

    fun setString(key: String,value : String){
        sp.edit().putString(key, value).apply()
    }

    fun getString(key: String):String{
        return sp.getString(key,"").toString()
    }
}