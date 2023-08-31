package com.example.mainapplabrats.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mainapplabrats.R
import com.example.mainapplabrats.adapter.JsonAdapter
//import com.example.mainapplabrats.adapter.JsonAdapter
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
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.Type
import kotlin.collections.ArrayList

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var imageView: ImageView
    private lateinit var button: Button
    private lateinit var buttonLoad: Button
    private lateinit var detailDesc : RecyclerView
    private val TAG : String = "CHECK_RESPONE"
    private val GALLERY_REQUEST_CODE = 123
    var itemsArray : ArrayList<Cell> = ArrayList()
    lateinit var adapter: JsonAdapter
    private val binding get() = _binding!!
    var TandaMasuk : Int = 0
    private val IS_APP_INSTALLED_KEY = "is_app_installed"

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
        //fungsi untuk click to search
//        tvOutput.setOnClickListener {
//            val intent =  Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/search?q=${tvOutput.text}"))
//            startActivity(intent)
//        }

        val isAppInstalled = isAppInstalledBefore()
        if (!isAppInstalled) {
            markAppAsInstalled()
            resetLocalInstalled()
            setToolbar()
            setupRecyclerView()
            Log.d(TAG,"MASUK SCOPE BELUM TERINSTALL")
            TandaMasuk+=1
        }

        if(isAppInstalled){
            Log.d(TAG,"TOTAL TANDA MASUK ${loadLocalInstalled()}")
            if(loadLocalInstalled() >= 2){
                if (itemsArray.isEmpty()) {
                    loadDataDetection()
                    setToolbar()
                    setupRecyclerView()
                    Log.d(TAG,"MASUK SCOPE TANDA MASUK")
                }
            }else{
                setToolbar()
                setupRecyclerView()
                Log.d(TAG,"MASUK SCOPE TANDA TIDAK MASUK")
            }
        }
        return root
    }

    private fun isAppInstalledBefore(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE)
        return sharedPref.getBoolean(IS_APP_INSTALLED_KEY, false)
    }

    private fun markAppAsInstalled() {
        val sharedPref = requireActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(IS_APP_INSTALLED_KEY, true)
        editor.apply()
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
            imageView.setImageBitmap(bitmap)
            outputGenerator(bitmap)
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
        ContextCompat.getDrawable(requireActivity(), R.drawable.line_divider)?.let { drawable ->
            dividerItemDecoration.setDrawable(drawable)
        }
        binding.detailDesc.addItemDecoration(dividerItemDecoration)
    }
    private fun outputGenerator(bitmap : Bitmap){
        var imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(150,150, ResizeOp.ResizeMethod.BILINEAR))
            .build()

        //txt notepad
        val inputStream = requireContext().assets.open("labels.txt")
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val lines = bufferedReader.readLines()

        //sesuaikan ukuran bitmap yang diminta
        val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val model = Model.newInstance(requireActivity())
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1,224, 224, 3),DataType.UINT8)

        val tensorImage = TensorImage(DataType.UINT8)
        tensorImage.load(resized)
        val ByteBuffer = tensorImage.buffer
        inputFeature0.loadBuffer(ByteBuffer)

        val outputs =  model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray
//        var maxIdx = getMax(outputFeature0.floatArray,3)

        var maxIdx = 0
        Log.i(TAG, "RESPON INDEX DETEKSI : ${maxIdx}")
        outputFeature0.forEachIndexed { index, fl ->
            if (outputFeature0[maxIdx] < fl){
                maxIdx =  index
                Log.i(TAG, "RESPON INDEX DETEKSI : ${maxIdx}")
            }
        }

        model.close()
//        tvOutput.setText(lines[maxIdx])
        Log.i(TAG, "RESPON INDEX DETECTION FULLY : ${maxIdx}")
        // Create Service
        val service = getApiJson().create(ApiInterface::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            // Do the GET request and get response
            val response = service.getEmployees()
            // Getter for the property
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
                            Log.d(TAG, "RESPON API SELECTEDITEM (ISI) B ${itemsArray}")
                            adapter = JsonAdapter(itemsArray)
                            adapter.notifyDataSetChanged()
                            TandaMasuk+=1
                            val isAppInstalled = isAppInstalledBefore()
                            if(isAppInstalled){
                                TandaMasuk+=1
                            }
                            saveLocalInstalled(TandaMasuk)
                            saveDataDetection()
                            Log.i(TAG,"ISI TANDA MASUK : ${TandaMasuk}")
                        }
                    }
                    binding.detailDesc.adapter = adapter
                } else {
                    Log.e("RETROFIT_ERROR", response.code().toString())
                }
            }
        }

    }
//    private fun getMax(arr: FloatArray, j: Int): Int{
//        var ind = 0
//        var min = 0.0F
//
//        for (i in 0..j)
//        {
//            if(arr[i] > min){
//                ind = i
//                min = arr[i]
//            }
//        }
//        return ind
//    }
    private fun saveDataDetection() {
        val gson = Gson()
        val sharedPref = requireActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val json = gson.toJson(itemsArray)
        editor.putString("ArrayDeteksi", json)
        editor.putString("TandaMasuk",TandaMasuk.toString())
        editor.apply()
        Log.d(TAG,"ISI SAVE ${json}")
    }

     fun loadDataDetection() {
        val sharedPref = requireActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPref.getString("ArrayDeteksi", null)
        val type: Type = object : TypeToken<ArrayList<Cell>>() {}.type
        itemsArray = gson.fromJson(json, type) ?: ArrayList()
        Log.d(TAG,"ISI LOAD ${itemsArray}")
        adapter = JsonAdapter(itemsArray)
        adapter.notifyDataSetChanged()
        binding.detailDesc.adapter = adapter
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