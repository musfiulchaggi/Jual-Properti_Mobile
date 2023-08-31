package com.musfiul.uas.bakulproperti.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.musfiul.uas.bakulproperti.MainActivity
import com.musfiul.uas.bakulproperti.R
import com.musfiul.uas.bakulproperti.activity.Help
import com.musfiul.uas.bakulproperti.activity.LoginActivity
import com.musfiul.uas.bakulproperti.activity.Tentang
import com.musfiul.uas.bakulproperti.helper.SharedPref
import com.musfiul.uas.bakulproperti.model.User

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentAkun.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentAkun : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var tv_nama:TextView
    lateinit var tv_phone:TextView
    lateinit var tv_email:TextView

    lateinit var btn_tentang: RelativeLayout
    lateinit var btn_help: RelativeLayout

    lateinit var btn_logout:TextView

    //    sp
    private lateinit var sp: SharedPref

    lateinit var user: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_akun, container, false)
        sp = SharedPref(requireActivity())
        user = sp.getUser()!!

        init(view)

        setData()

        return view
    }

    fun setData(){
//      mengambil data dari class SharedPref.user
//        !! digunakan untuk null exception


        if(user == null){

            val intent = Intent(activity, LoginActivity::class.java)

            //                    FLAG_ACTIVITY_CLEAR_TOP untuk menghancurkan semua activity sebelumnya
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

            return
        }

        val user = sp.getUser()!!

        tv_nama.text = user.name
        tv_email.text = user.email
        tv_phone.text = user.phone
    }

    fun init(view:View){
        sp = SharedPref(requireActivity())

        btn_logout = view.findViewById(R.id.btn_logout)
        btn_tentang = view.findViewById(R.id.btn_tentang)
        btn_help = view.findViewById(R.id.btn_help)
        tv_nama = view.findViewById(R.id.tv_nama)
        tv_email = view.findViewById(R.id.tv_email)
        tv_phone = view.findViewById(R.id.tv_phone)


        btn_logout.setOnClickListener(View.OnClickListener {
            sp.setStatusLogin(false)

            val intent = Intent(activity, MainActivity::class.java)

            //                    FLAG_ACTIVITY_CLEAR_TOP untuk menghancurkan semua activity sebelumnya
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        })

        btn_tentang.setOnClickListener {
            val intent = Intent(activity, Tentang::class.java)

            startActivity(intent)
        }

        btn_help.setOnClickListener {
            val intent = Intent(activity, Help::class.java)

            startActivity(intent)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentAkun.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentAkun().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}