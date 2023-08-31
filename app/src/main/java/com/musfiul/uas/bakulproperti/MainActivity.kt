package com.musfiul.uas.bakulproperti

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.musfiul.uas.bakulproperti.activity.LoginActivity
import com.musfiul.uas.bakulproperti.fragment.FragmentAddProperty
import com.musfiul.uas.bakulproperti.fragment.FragmentAkun
import com.musfiul.uas.bakulproperti.fragment.FragmentFavorite
import com.musfiul.uas.bakulproperti.fragment.FragmentHome
import com.musfiul.uas.bakulproperti.helper.SharedPref

class MainActivity : AppCompatActivity() {

    private lateinit var sp:SharedPref

//    inisialisasi fragment
    val fragmentHome: Fragment = FragmentHome()
    val fragmentFavorite: Fragment = FragmentFavorite()
    val fragmentAddProperty: Fragment = FragmentAddProperty()
    val fragmentAkun: Fragment = FragmentAkun()
    var fm:FragmentManager = supportFragmentManager

    var active:Fragment = fragmentHome//inisialisasi fragment yang aktif adalah fragment home

//    inisialisasi menu bottomNav
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var menuUtama :Menu
    private lateinit var menuItem :MenuItem


    private var dariDetail : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sp = SharedPref(this)

        checkLogin()

        LocalBroadcastManager.getInstance(this).registerReceiver(message, IntentFilter("event:addproperti"))
    }

    val message : BroadcastReceiver = object: BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            dariDetail = true
        }

    }

    private fun checkLogin(){

//        mengambil data login dari sharedpref
        if(!sp.getStatusLogin()){
            val intent = Intent(this@MainActivity, LoginActivity::class.java)

//                    FLAG_ACTIVITY_CLEAR_TOP untuk menghancurkan semua activity sebelumnya
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }else{
//            masuk kedalam fragment utama
            setUpBottomNav()

        }
    }

    private fun setUpBottomNav() {
        fm.beginTransaction().add(R.id.nav_host_fragment, fragmentHome).show(fragmentHome).commit()
        fm.beginTransaction().add(R.id.nav_host_fragment, fragmentAkun).hide(fragmentAkun).commit()
        fm.beginTransaction().add(R.id.nav_host_fragment, fragmentFavorite).hide(fragmentFavorite).commit()
        fm.beginTransaction().add(R.id.nav_host_fragment, fragmentAddProperty).hide(fragmentAddProperty).commit()

        bottomNavigationView = findViewById(R.id.nav_bar)
        menuUtama = bottomNavigationView.menu
        menuItem = menuUtama.getItem(0)
        menuItem.isChecked = true //mengaktifkan menu yang dipilih

        bottomNavigationView.setOnItemSelectedListener { item ->

            when(item.itemId){
                R.id.navigation_home ->{

                    callFragment(0,fragmentHome);
                }

                R.id.navigation_favorite ->{

                    callFragment(1,fragmentFavorite);
                }

                R.id.navigation_AddProperty ->{

                    callFragment(2,fragmentAddProperty);
                }

                R.id.navigation_akun ->{

                    if(sp.getStatusLogin()){
                        callFragment(3,fragmentAkun);

                    }else{

//                        ketika belum login, maka mengarah ke login activity
                        startActivity(Intent(this, LoginActivity::class.java))

                    }

                }
            }

            false;

        }

    }



    fun callFragment(int:Int, fragment: Fragment){
        menuItem = menuUtama.getItem(int)
        menuItem.isChecked = true

//        menyembunyikan fragment lain yang tidak di klik
        fm.beginTransaction().hide(active).show(fragment).commit() // menyembunyikan akun aktif
        active = fragment // mengubah fragmen yang aktif adalah fragment yang dipilih
    }

    override fun onResume() {
        if(dariDetail){
            dariDetail = false
            callFragment(2,fragmentAddProperty);
        }

        super.onResume()
    }
}