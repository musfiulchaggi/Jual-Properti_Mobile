package com.musfiul.uas.bakulproperti.helper

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Helper {

    fun gantiRupiah(angka:Int):String{
        return NumberFormat.getCurrencyInstance( Locale("in","ID")).format(angka)
    }

    fun setToolbar(activity: Activity, toolbar: Toolbar, title:String){
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        activity.supportActionBar!!.title = title
        activity.supportActionBar!!.setDisplayShowHomeEnabled(true)
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    fun convertTanggal(tgl : String, formatBaru :String, formatLama:String):String{
//        val formatLama ="yyyy-MM-dd"
//        val formatBaru ="d MMM yyyy"

        val dateFormat = SimpleDateFormat(formatLama)
        val convert = dateFormat.parse(tgl.substring(0,10))
        dateFormat.applyPattern(formatBaru)
        val tanggalBaru = dateFormat.format(convert)
        return tanggalBaru
    }
}