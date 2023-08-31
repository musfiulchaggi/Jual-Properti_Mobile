package com.musfiul.uas.bakulproperti.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.musfiul.uas.bakulproperti.R
import com.musfiul.uas.bakulproperti.adapter.AdapterProperti
import com.musfiul.uas.bakulproperti.adapter.AdapterSlider
import com.musfiul.uas.bakulproperti.app.ApiConfig
import com.musfiul.uas.bakulproperti.model.Properti
import com.musfiul.uas.bakulproperti.model.ResponModel
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentHome.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentHome : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var vpSlider : ViewPager;
    lateinit var rvProperti: RecyclerView;

    private var listProperti: ArrayList<Properti> = ArrayList()

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
        val view: View = inflater.inflate(R.layout.fragment_home, container, false);

        init(view);

        getProperti();
        return view;
    }

    fun init(view: View){
        //        membuat tampilan slider dengan ViewPager
        //        semua gambar yang berada di drawable bernilai integer

        vpSlider = view.findViewById(R.id.vp_slider);

        val arraySlider = ArrayList<Int>();
        arraySlider.add(R.drawable.images2);
        arraySlider.add(R.drawable.images3);
        arraySlider.add(R.drawable.images);

        val adapterSlider = AdapterSlider(arraySlider, activity);
        vpSlider.adapter =adapterSlider;

//                membuat tampilan produk dengan recycle view
        rvProperti = view.findViewById(R.id.rv_properti);
    }

    fun displayProperti(){
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL;

        rvProperti.layoutManager = layoutManager;
        rvProperti.adapter = AdapterProperti(requireActivity(),listProperti)

    }

    fun getProperti(){
        ApiConfig.instanceRetrofit.getProperti().enqueue(object : retrofit2.Callback<ResponModel>{


            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

                val respon = response.body()!!

                if(respon.success== 1){

//                    memasukkan kedalam daftar produk
                    runBlocking {
                        listProperti = respon.propertis
                        displayProperti()
                    }

                }


            }

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {

//                pb.visibility = View.GONE
//                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_LONG ).show()

            }

        })
    }

    override fun onResume() {
        getProperti()

        super.onResume()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentHome.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentHome().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}