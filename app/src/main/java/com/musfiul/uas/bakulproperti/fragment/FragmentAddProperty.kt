package com.musfiul.uas.bakulproperti.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.musfiul.uas.bakulproperti.R
import com.musfiul.uas.bakulproperti.activity.DataPropertiTambahActivity
import com.musfiul.uas.bakulproperti.adapter.AdapterPropertiJual
import com.musfiul.uas.bakulproperti.app.ApiConfig
import com.musfiul.uas.bakulproperti.helper.SharedPref
import com.musfiul.uas.bakulproperti.model.Properti
import com.musfiul.uas.bakulproperti.model.ResponModel
import com.musfiul.uas.bakulproperti.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentAddProperty.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentAddProperty : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var btn_tambahProperti: Button
    lateinit var layout_tambahProperti: LinearLayout
    lateinit var rvProperti : RecyclerView

//    sharedpref
    lateinit var sp:SharedPref
    var user:User? = null
    var id_user : Int =0

    lateinit var adapter: AdapterPropertiJual
    var listProperti : ArrayList<Properti> = ArrayList()

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
        val view = inflater.inflate(R.layout.fragment_add_property, container, false)

//        inisialisasi sp
        sp = SharedPref(requireActivity())
        user = sp.getUser()!!
        id_user = user!!.id

        init(view)


//        displayProperti()
        getDataOwnProperti(id_user)

        return view
    }



    private fun getDataOwnProperti(id: Int):ArrayList<Properti> {

        ApiConfig.instanceRetrofit.getPropertiById(id).enqueue(object : retrofit2.Callback<ResponModel>{
            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

                val respon = response.body()!!

                if(respon.success== 1){
                    listProperti.clear()//penting untuk membuat data benar

                    //                    memasukkan kedalam daftar produk
                        listProperti.add(respon.properti)

                    displayProperti()

                    layout_tambahProperti.visibility = View.GONE

                }else{
                    listProperti.clear()//penting untuk membuat data benar
                    displayProperti()
               }


            }

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {

//                pb.visibility = View.GONE


            }

        })
        return listProperti
    }


    private fun displayProperti() {
        layout_tambahProperti.visibility = View.VISIBLE

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL;

        rvProperti.layoutManager = layoutManager;

        rvProperti.adapter=AdapterPropertiJual(requireActivity(),listProperti, object :AdapterPropertiJual.Listeners{
            override fun onClicked() {
                id_user = user!!.id
                getDataOwnProperti(id_user)
            }

        })





    }

    private fun init(view: View) {

        btn_tambahProperti = view.findViewById(R.id.btn_tambahProperti)
        rvProperti = view.findViewById(R.id.rv_propertiJual)
        layout_tambahProperti = view.findViewById(R.id.layout_Tambahproperti)

        btn_tambahProperti.setOnClickListener {
            var adaProperti=false

            for(p in listProperti){
                adaProperti= true
            }
            if(!adaProperti){
                val intent = Intent(requireActivity(), DataPropertiTambahActivity::class.java)
                startActivity(intent)
            }
//
//            val intent =Intent(requireActivity(),DataPropertiTambahActivity::class.java)
//            startActivity(intent)
        }


    }

    override fun onResume() {
        id_user = user!!.id
        getDataOwnProperti(id_user)
//        displayProperti()

        super.onResume()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentAddProperty.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentAddProperty().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }




}