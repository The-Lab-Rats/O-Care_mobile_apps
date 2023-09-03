package com.example.mainapplabrats.ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mainapplabrats.R.*
import com.example.mainapplabrats.adapter.JsonAdapter
import com.example.mainapplabrats.data.DataLocal
import com.example.mainapplabrats.databinding.FragmentDashboardBinding
import com.example.mainapplabrats.ml.Model
import com.example.mainapplabrats.ml.ModelF
import com.example.mainapplabrats.model.Cell
import com.example.mainapplabrats.networking.ApiEndpoint.getApiJson
//import com.example.mainapplabrats.model.ModelJson
import com.example.mainapplabrats.networking.ApiInterface
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.image.TensorImage
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.Type
import kotlin.collections.ArrayList

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var imageView: ImageView
    private lateinit var button: Button
    private lateinit var btn_more_info: Button
    private lateinit var buttonLoad: Button
    private lateinit var detailDesc : RecyclerView
    private val TAG : String = "CHECK_RESPONE"
    private val GALLERY_REQUEST_CODE = 123
    var itemsArray : ArrayList<Cell> = ArrayList()
    lateinit var adapter: JsonAdapter
    private lateinit var btn_indikasi: TextView
    private val binding get() = _binding!!
    var TandaMasuk : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        imageView = binding.imageView
        button = binding.btnTakeImage
        detailDesc = binding.detailDesc
        buttonLoad = binding.btnLoadImage
        btn_more_info = binding.btnMoreInfo
        btn_indikasi = binding.btnIndikasi

        button.setOnClickListener{
            if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
                takePicturePreview.launch(null)
            }else{
                requestPermission.launch(android.Manifest.permission.CAMERA)
            }
        }
        buttonLoad.setOnClickListener{
            if (ContextCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                val mimeTypes = arrayOf("image/jpg","image/png","image/jpg")
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                onresult.launch(intent)
            }else{
                requestPermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        btn_indikasi.visibility = View.GONE
        btn_more_info.setText("Klik Untuk Informasi Lebih Lanjut ")
        btn_more_info.setOnClickListener {
            val searchUrl = "https://www.google.com/search?q=cara+mengatasi+${btn_indikasi.text}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl))
            startActivity(intent)
        }


        val isAppInstalled = DataLocal.isAppInstalledBefore(requireActivity())
        if (!isAppInstalled) {
            DataLocal.markAppAsInstalled(requireActivity())
            resetLocalInstalled()
            setToolbar()
            setupRecyclerView()
            TandaMasuk+=1
        }

        if(isAppInstalled){
            if(loadLocalInstalled() >= 2){
                if (itemsArray.isEmpty()) {
                    loadDataDetection()
                    imageView.setImageBitmap(DataLocal.loadImageFromSharedPreferences(requireActivity()))
                    setToolbar()
                    setupRecyclerView()
                }
            }else{
                setToolbar()
                setupRecyclerView()
            }
        }
        return root
    }

    private fun openCustomDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(layout.loading_dialog)
        dialog.show()
        val dialogDisplayDuration = 3000L
        val handler = Handler()
        handler.postDelayed({
            dialog.dismiss()
        }, dialogDisplayDuration)
    }

    private fun setToolbar() {
        binding.toolbar.tvToolbarTitle.text = "Deteksi Kesehatan Mulut"
    }

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){ granted->
        if(granted){
            takePicturePreview.launch(null)
        }else{
            Toast.makeText(requireActivity(),"Permission Danied !! Try Again", Toast.LENGTH_SHORT).show()
        }
    }
    private val takePicturePreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){ bitmap->
        if(bitmap != null){
            DataLocal.bitmapToBase64(bitmap)
            DataLocal.saveImageToSharedPreferences(bitmap,requireActivity())
            imageView.setImageBitmap(bitmap)
            outputGenerator(bitmap)
            openCustomDialog()
        }
    }
    private val onresult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        Log.i("TAG", "This is result : ${result.data} ${result.resultCode}")
        onResultReceived(GALLERY_REQUEST_CODE, result)
    }

    private fun onResultReceived(requestCode : Int, result: ActivityResult?){
        when(requestCode){
            GALLERY_REQUEST_CODE ->{
                if (result?.resultCode == Activity.RESULT_OK){
                    result.data?.data?.let { uri ->
                        Log.i("TAG", "onResultReveived: $uri")
                        val bitmap = BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(uri))
                        imageView.setImageBitmap(bitmap)
                        outputGenerator(bitmap)
                        openCustomDialog()
                        DataLocal.bitmapToBase64(bitmap)
                        DataLocal.saveImageToSharedPreferences(bitmap,requireActivity())
                    }
                }else{
                    Log.e("TAG", "onActivityResult : Error in selecting image")
                }
            }
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.detailDesc.layoutManager = layoutManager
        binding.detailDesc.setHasFixedSize(true)
        val dividerItemDecoration =
            DividerItemDecoration(binding.detailDesc.context, layoutManager.orientation)
        ContextCompat.getDrawable(requireActivity(), drawable.line_divider)?.let { drawable ->
            dividerItemDecoration.setDrawable(drawable)
        }
        binding.detailDesc.addItemDecoration(dividerItemDecoration)
    }
    private fun outputGenerator(bitmap : Bitmap){
        //txt notepad
        val inputStream = requireContext().assets.open("labels.txt")
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val lines = bufferedReader.readLines()

        //sesuaikan ukuran bitmap yang diminta
        val resized = Bitmap.createScaledBitmap(bitmap, 150, 150, true)
        val model = Model.newInstance(requireActivity())
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1,150, 150, 3),DataType.FLOAT32)

        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(resized)
        val ByteBuffer = tensorImage.buffer
        inputFeature0.loadBuffer(ByteBuffer)

        val outputs =  model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

        var maxIdx = 0
        outputFeature0.forEachIndexed { index, fl ->
            if (outputFeature0[maxIdx] < fl){
                maxIdx =  index
            }
        }
        btn_indikasi.setText(lines[maxIdx])

        model.close()
        val service = getApiJson().create(ApiInterface::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getEmployees()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val items = response.body()
                    if (items != null) {
                        val selectedItem = items.find { it.id == maxIdx }
                        if (selectedItem != null) {
                            val Id = selectedItem.id
                            val Nama = selectedItem.nama
                            val Penyebab = selectedItem.penyebab
                            val Rekomendasi = selectedItem.rekomendasi
                            val model = Cell(Id , Nama,  Penyebab, Rekomendasi)
                            itemsArray.add(model)
                            itemsArray.reverse()
                            adapter = JsonAdapter(itemsArray)
                            adapter.notifyDataSetChanged()
                            TandaMasuk+=1
                            val isAppInstalled = DataLocal.isAppInstalledBefore(requireActivity())
                            if(isAppInstalled){
                                TandaMasuk+=1
                            }
                            saveLocalInstalled(TandaMasuk)
                            saveDataDetection()
                        }
                    }
                    val delayMillis = 3000L
                    val handler = Handler()
                    handler.postDelayed({
                        binding.detailDesc.adapter = adapter
                    }, delayMillis)
                } else {
                    Log.e("RETROFIT_ERROR", response.code().toString())
                }
            }
        }

    }
    private fun saveDataDetection() {
        val gson = Gson()
        val sharedPref = requireActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val json = gson.toJson(itemsArray)
        editor.putString("ArrayDeteksi", json)
        editor.putString("hasilDeteksi", btn_indikasi.text.toString())
        editor.putString("TandaMasuk", TandaMasuk.toString())
        editor.apply()
    }

     fun loadDataDetection() {
        val sharedPref = requireActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPref.getString("ArrayDeteksi", null)
        val type: Type = object : TypeToken<ArrayList<Cell>>() {}.type
        itemsArray = gson.fromJson(json, type) ?: ArrayList()
        adapter = JsonAdapter(itemsArray)
        adapter.notifyDataSetChanged()
        binding.detailDesc.adapter = adapter
         val hasilD = sharedPref.getString("hasilDeteksi",null)
         binding.btnIndikasi.text = hasilD

    }
   fun saveLocalInstalled(DataVar : Int){
        val sharedPref = requireActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("TANDA MASUK",DataVar)
        editor.apply()
    }
   fun loadLocalInstalled(): Int {
        val sharedPref = requireActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE)
        return sharedPref.getInt("TANDA MASUK", 0)
    }
    fun resetLocalInstalled() {
        val sharedPref = requireActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("TANDA MASUK", 0)
        editor.apply()
    }

    private fun deleteDetectionTemp() : Boolean{
        val sharedPref = requireActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE)
        sharedPref.edit().clear().commit();
        return sharedPref.getBoolean("ArrayDeteksi", false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}