package com.example.mainapplabrats.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.mainapplabrats.MainActivity
import com.example.mainapplabrats.R
import com.example.mainapplabrats.adapter.NewsAdapter
import com.example.mainapplabrats.databinding.FragmentHomeBinding
import com.example.mainapplabrats.model.ModelArticle
import com.example.mainapplabrats.model.ModelNews
import com.example.mainapplabrats.networking.ApiEndpoint
import com.example.mainapplabrats.networking.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    companion object {
        const val API_KEY = "0ede1bdd12b74f87917d2a037eb88bb7"
    }
    private lateinit var imageRefresh: ImageView
    private lateinit var rvListNews: RecyclerView
    private lateinit var tvTitle: TextView
    private lateinit var tvToolbarTitle : Toolbar
    private lateinit var ImageSlider : ImageSlider
    private lateinit var btnPeriksa : Button
    private val TAG : String = "CHECK_RESPONE"

    var strCategory = "health"
    var strCountry: String? = null
    var modelArticle: MutableList<ModelArticle> = ArrayList()
    var newsAdapter: NewsAdapter? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        ImageSlider = binding.ImageSlider
        rvListNews = binding.rvListNews
        tvTitle = binding.tvTitle
        imageRefresh = binding.imageRefresh
        btnPeriksa = binding.btnPeriksa

        btnPeriksa.setOnClickListener {
            findNavController().navigate(R.id.navigation_dashboard)
        }

        //image slider
        val imageList =  ArrayList<SlideModel>()
        imageList.add(SlideModel("https://asset-a.grid.id/crop/0x0:0x0/x/photo/2018/04/17/3267299757.jpg", ScaleTypes.FIT))
        imageList.add(SlideModel("https://d1bpj0tv6vfxyp.cloudfront.net/x-kebiasaan-ini-bisa-bantu-menjaga-kesehatan-mulut-dan-gigi.jpg", ScaleTypes.FIT))

        ImageSlider.setImageList(imageList, ScaleTypes.FIT)

        tvTitle.setText("Berita Kesehatan")
        rvListNews.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvListNews.setHasFixedSize(true)
        rvListNews.adapter

        imageRefresh.setOnClickListener {
            rvListNews.adapter
            getListNews()
        }

        //get news
        setToolbar()
        getListNews()


        return root
    }
    private fun setToolbar() {
        binding.toolbar.tvToolbarTitle.text = "Home"
    }


    private fun getListNews() {
//        strCountry = Utils.getCountry()
        strCountry = "us".toString()

        //set api
        val apiInterface = ApiEndpoint.getApiClient().create(ApiInterface::class.java)
        val call = apiInterface.getHealth(strCountry, strCategory, API_KEY)
        call.enqueue(object : Callback<ModelNews> {
            override fun onResponse(call: Call<ModelNews>, response: Response<ModelNews>) {
                if (response.isSuccessful && response.body() != null) {
                    modelArticle = response.body()?.modelArticle as MutableList<ModelArticle>
                    newsAdapter = NewsAdapter(modelArticle, context!!)
                    rvListNews.adapter = newsAdapter
                    Log.i(TAG,"Responnya Article : ${modelArticle}")
                    newsAdapter?.notifyDataSetChanged()
//                    rvListNews.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ModelNews>, t: Throwable) {
                Toast.makeText(context, "Oops, jaringan kamu bermasalah.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}