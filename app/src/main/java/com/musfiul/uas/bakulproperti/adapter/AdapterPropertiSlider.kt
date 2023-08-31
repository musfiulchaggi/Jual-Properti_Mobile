package com.musfiul.uas.bakulproperti.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.viewpager.widget.PagerAdapter
import com.google.gson.Gson
import com.musfiul.uas.bakulproperti.R
import com.musfiul.uas.bakulproperti.activity.DetailPropertiActivity
import com.musfiul.uas.bakulproperti.model.Properti
import com.musfiul.uas.bakulproperti.util.Config
import com.squareup.picasso.Picasso
import java.util.ArrayList

/**
 * Created by: Tisto
 */
class AdapterPropertiSlider(var data: ArrayList<String>, var context: Activity?) : PagerAdapter() {
    lateinit var layoutInflater: LayoutInflater

    override fun getCount(): Int {
        return data.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    @NonNull
    override fun instantiateItem(@NonNull container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.item_slider_properti, container, false)

        //Init
        val imageView: ImageView

        imageView = view.findViewById(R.id.image)


        val imageUri = Config.propertiUrl+data[position]
        Picasso.get().load(imageUri).placeholder(R.drawable.ic_baseline_add_home_24).error(R.drawable.ic_baseline_add_home_24).into(imageView);
        container.addView(view, 0)



        return view
    }

    override fun destroyItem(@NonNull container: ViewGroup, position: Int, @NonNull `object`: Any) {
        container.removeView(`object` as View)
    }
}