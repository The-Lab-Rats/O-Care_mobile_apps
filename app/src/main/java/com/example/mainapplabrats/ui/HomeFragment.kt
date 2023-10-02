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
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.mainapplabrats.R
import com.example.mainapplabrats.adapter.NewsAdapter
import com.example.mainapplabrats.databinding.FragmentHomeBinding
import com.example.mainapplabrats.model.ModelArticle
import com.example.mainapplabrats.model.ModelNews
import com.example.mainapplabrats.networking.ApiEndpoint
import com.example.mainapplabrats.networking.ApiInterface
import com.example.mainapplabrats.util.Utils
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
    private lateinit var rvListNews: ShimmerRecyclerView
    private lateinit var tvTitle: TextView
    private lateinit var iconNotification : ImageView
    private lateinit var ImageSlider : ImageSlider
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
        val imageList =  ArrayList<SlideModel>()
        imageList.add(SlideModel("https://asset-a.grid.id/crop/0x0:0x0/x/photo/2018/04/17/3267299757.jpg", ScaleTypes.FIT))
        imageList.add(SlideModel("https://d1bpj0tv6vfxyp.cloudfront.net/x-kebiasaan-ini-bisa-bantu-menjaga-kesehatan-mulut-dan-gigi.jpg", ScaleTypes.FIT))
        imageList.add(SlideModel("https://d1vbn70lmn1nqe.cloudfront.net/prod/wp-content/uploads/2021/06/15061933/Kenali-Arti-Warna-Lidah-yang-Perlu-Diperhatikan-1.jpg", ScaleTypes.FIT))
        imageList.add(SlideModel("https://akcdn.detik.net.id/visual/2020/02/12/70b6ddb4-4d04-4de6-93a9-d7f413801c6c_169.jpeg?w=650", ScaleTypes.FIT))

        ImageSlider.setImageList(imageList, ScaleTypes.FIT)

        tvTitle.setText("Berita Kesehatan")
        rvListNews.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvListNews.setHasFixedSize(true)
        rvListNews.showShimmerAdapter()

        imageRefresh.setOnClickListener {
            rvListNews.showShimmerAdapter()
            getListNews()
        }

        //get news
        setToolbar()
        getListNews()

        return root
    }
    private fun setToolbar() {
        binding.toolbar.notificationIcon.setOnClickListener {
            findNavController().navigate(R.id.navigation_reminder)

        }
    }


    private fun getListNews() {
        strCountry = Utils.getCountry()

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
                    rvListNews.hideShimmerAdapter()
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