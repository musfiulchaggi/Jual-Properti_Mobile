package com.musfiul.uas.bakulproperti.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.ContentInfoCompat.Flags
import com.musfiul.uas.bakulproperti.activity.SplashScreen
import com.musfiul.uas.bakulproperti.R
import com.musfiul.uas.bakulproperti.app.ApiConfig
import com.musfiul.uas.bakulproperti.helper.SharedPref
import com.musfiul.uas.bakulproperti.model.ResponModel
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
//    inisialisasi sharedpref
    lateinit var sp:SharedPref

//    botton
    lateinit var btn_login:Button
    lateinit var edt_email:EditText
    lateinit var edt_pass:EditText
    lateinit var btn_daftar:TextView
    lateinit var pb:ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        inisialisasi shared pref
        sp = SharedPref(this)

//        inisialisasi button dll
        init()
    }

    private fun init(){
        btn_daftar = findViewById(R.id.btn_daftar)
        btn_login = findViewById(R.id.btn_login)

        edt_email = findViewById(R.id.edt_email)
        edt_pass = findViewById(R.id.edt_password)

        pb = findViewById(R.id.pb)

        btn_login.setOnClickListener {

//        prosesLogin()
            prosesLogin()
        }

        btn_daftar.setOnClickListener{
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            //                    FLAG_ACTIVITY_CLEAR_TOP untuk menghancurkan semua activity sebelumnya
//            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(intent)
        }
    }

    private fun prosesLogin(){
        if(edt_email.text.isEmpty()){
            edt_email.error ="Kolom harus diisi."
            edt_email.requestFocus()
            return
        }else if(edt_pass.text.isEmpty()){
            edt_pass.error ="Kolom harus diisi."
            edt_pass.requestFocus()
            return
        }

        pb.visibility = View.VISIBLE

        //        Request http menggunakan retrovit dan callback menngunakan class ResponModel yang mempunyai 3 buah parameter success,message,user()
        ApiConfig.instanceRetrofit.login(edt_email.text.toString(),edt_pass.text.toString()).enqueue(object : retrofit2.Callback<ResponModel>{


            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

                val respon = response.body();

//                .body() memanggil semua isinya
                if(respon!!.success == 1){
//                    mengubah nilai shared preference menjadi true
                    sp.setStatusLogin(true)

                    sp.setUser(respon.user)
//                    sp.setString(sp.nama, respon.user.name)
//                    sp.setString(sp.phone, respon.user.phone)
//                    sp.setString(sp.email, respon.user.email)

                    val intent = Intent(this@LoginActivity, SplashScreen::class.java)

//                    FLAG_ACTIVITY_CLEAR_TOP untuk menghancurkan semua activity sebelumnya
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()

                    pb.visibility = View.GONE
                    Toast.makeText(this@LoginActivity, "Selamat Datang "+respon.user.name , Toast.LENGTH_LONG ).show()
                }else{
                    pb.visibility = View.GONE
                    Toast.makeText(this@LoginActivity, respon.message, Toast.LENGTH_LONG ).show()
                }
            }

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                pb.visibility = View.GONE
                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_LONG ).show()
            }

        })


    }
}