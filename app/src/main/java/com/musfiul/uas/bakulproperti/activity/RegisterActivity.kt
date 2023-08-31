package com.musfiul.uas.bakulproperti.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.musfiul.uas.bakulproperti.MainActivity
import com.musfiul.uas.bakulproperti.R
import com.musfiul.uas.bakulproperti.app.ApiConfig
import com.musfiul.uas.bakulproperti.model.ResponModel
import retrofit2.Call
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    lateinit var etNama: EditText
    lateinit var etTelp: EditText
    lateinit var etEmail: EditText
    lateinit var etPass: EditText

    lateinit var btnRegis: Button
    lateinit var pb: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        init()
    }

    private fun init(){
        etNama = findViewById(R.id.edt_nama)
        etTelp = findViewById(R.id.edt_phone)
        etEmail = findViewById(R.id.edt_email)
        etPass = findViewById(R.id.edt_password)
        btnRegis = findViewById(R.id.btn_register)
        pb = findViewById(R.id.pb)

        btnRegis.setOnClickListener {
            prosesRegis()
        }
    }

    private fun prosesRegis() {
        if(etNama.text.isEmpty()){
            etNama.error = "Kolom Nama Harus Diisi."
            etNama.requestFocus();
            return;
        }else if(etTelp.text.isEmpty()){
            etTelp.error = "Kolom Nama Harus Diisi."
            etTelp.requestFocus();
            return;
        }else if(etEmail.text.isEmpty()){
            etEmail.error = "Kolom Nama Harus Diisi."
            etEmail.requestFocus();
            return;
        }else if(etPass.text.isEmpty()){
            etPass.error = "Kolom Nama Harus Diisi."
            etPass.requestFocus();
            return;
        }else if(etTelp.text.isEmpty()){
            etTelp.error = "Kolom Nama Harus Diisi."
            etTelp.requestFocus();
            return;
        }

        pb.visibility = View.VISIBLE

//        Request http menggunakan retrovit
        ApiConfig.instanceRetrofit.register(etNama.text.toString(),etEmail.text.toString(),etTelp.text.toString(),etPass.text.toString()).enqueue(object : retrofit2.Callback<ResponModel>{


            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

                val respon = response.body();

//                .body() memanggil semua isinya
                if(respon!!.success == 1){
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()

                    pb.visibility = View.GONE
                    Toast.makeText(this@RegisterActivity, "Pendaftaran berhasil, Silahkan login "+respon.user.name , Toast.LENGTH_LONG ).show()
                }else{
                    pb.visibility = View.GONE
                    Toast.makeText(this@RegisterActivity, respon.message, Toast.LENGTH_LONG ).show()
                }
            }

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                pb.visibility = View.GONE
                Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_LONG ).show()
            }

        })
    }
}