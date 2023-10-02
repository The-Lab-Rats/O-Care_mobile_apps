package com.example.mainapplabrats.ui

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mainapplabrats.R
import com.example.mainapplabrats.R.*
import com.example.mainapplabrats.activities.ImagePickerActivity
import com.example.mainapplabrats.adapter.JsonAdapter
import com.example.mainapplabrats.data.DataLocal
import com.example.mainapplabrats.databinding.FragmentDashboardBinding
import com.example.mainapplabrats.ml.Model
import com.example.mainapplabrats.model.Cell
import com.example.mainapplabrats.networking.ApiEndpoint.getApiJson
import com.example.mainapplabrats.networking.ApiInterface
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.Type

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var imageView: ImageView
    private lateinit var button: Button
    private lateinit var btn_more_info: Button
    private lateinit var buttonLoad: Button
    private lateinit var detailDesc : RecyclerView
    var itemsArray : ArrayList<Cell> = ArrayList()
    private val PREF_NAME = "MyPrefs"
    private val KEY_ARRAY_LIST = "arrayListKey"
    lateinit var adapter: JsonAdapter
    private lateinit var btnIndikasi: TextView
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
        btnIndikasi = binding.btnIndikasi


        buttonLoad.setOnClickListener {
            onProfileImageClick()

        }
        btnIndikasi.visibility = View.GONE
        btn_more_info.text = "Klik Untuk Informasi Lebih Lanjut "
        btn_more_info.setOnClickListener {
            val searchUrl = "https://www.google.com/search?q=cara+mengatasi+${btnIndikasi.text}"
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

        binding.btnAddData.setOnClickListener {
            findNavController().navigate(R.id.navigation_reminder)
        }
        return root
    }

    private fun openCustomDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(layout.loading_dialog)
        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            // Kode Anda di sini
            dialog.dismiss()
        }, 3000)
    }

    private fun setToolbar() {
        binding.toolbar.tvToolbarTitle.text = "Deteksi Kesehatan Mulut"
        binding.toolbar.btnBackToolbar.visibility =  View.GONE
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

    private fun loadProfile(url: String) {
        Log.d(TAG, "Image cache path: $url")
        imageView = binding.imageView
        Glide.with(requireActivity())
            .load(url)
            .into(imageView)
        imageView.setColorFilter(ContextCompat.getColor(requireActivity(), android.R.color.transparent))
    }

    private fun onProfileImageClick() {
        Dexter.withActivity(requireActivity())
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        showImagePickerOptions()
                    }

                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            })
            .check()
    }

    private fun showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(requireActivity(),
            object : ImagePickerActivity.PickerOptionListener {
                override fun onTakeCameraSelected() {
                    launchCameraIntent()
                }

                override fun onChooseGallerySelected() {
                    launchGalleryIntent()
                }
            })
    }

    private fun launchCameraIntent() {
        val intent = Intent(requireActivity(), ImagePickerActivity::class.java)
        intent.putExtra(
            ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION,
            ImagePickerActivity.REQUEST_IMAGE_CAPTURE
        )

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1)

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000)

        startActivityForResult(intent, REQUEST_IMAGE)
    }

    private fun launchGalleryIntent() {
        val intent = Intent(requireActivity(), ImagePickerActivity::class.java)
        intent.putExtra(
            ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION,
            ImagePickerActivity.REQUEST_GALLERY_IMAGE
        )

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1)
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = data!!.getParcelableExtra<Uri>("path")
                try {
                    // You can update this bitmap to your server
                     val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)
                    outputGenerator(bitmap)
                    openCustomDialog()
                    DataLocal.bitmapToBase64(bitmap)
                    DataLocal.saveImageToSharedPreferences(bitmap,requireActivity())
                    // loading profile image from local cache
                    loadProfile(uri!!.toString())
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(string.dialog_permission_title))
        builder.setMessage(getString(string.dialog_permission_message))
        builder.setPositiveButton(getString(string.go_to_settings)) { dialog, /*which*/_ ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(
            getString(android.R.string.cancel)
        ) { dialog, /*which*/_ -> dialog.cancel() }
        builder.show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity?.packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }
    fun saveArrayList(context: Context, list: ArrayList<Cell>) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(KEY_ARRAY_LIST, json)
        editor.apply()
    }


    companion object {
        private val TAG = DashboardFragment::class.java.simpleName
        const val REQUEST_IMAGE = 100

    }
    private fun outputGenerator(bitmap : Bitmap){
        val inputStream = requireContext().assets.open("labels.txt")
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val lines = bufferedReader.readLines()

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
        btnIndikasi.text = lines[maxIdx]

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
                            val id = selectedItem.id
                            val nama = selectedItem.nama
                            val penjelasan = selectedItem.penjelasan
                            val tanda = selectedItem.tanda
                            val penyebab = selectedItem.penyebab
                            val pencegahan = selectedItem.pencegahan
                            val rekomendasi = selectedItem.rekomendasi
                            itemsArray.add(Cell(id ,nama ,penjelasan, tanda ,penyebab ,pencegahan ,rekomendasi))
                            itemsArray.reverse()
                            adapter = JsonAdapter(itemsArray)
                            adapter.notifyDataSetChanged()
                            val ArrayListHistory: ArrayList<Cell> = ArrayList()
                            for (cell in itemsArray) {
                                ArrayListHistory.add(cell)
                            }
                            Log.i("CEK ISI ARRAY",ArrayListHistory.toString())
                            saveArrayList(requireContext(),ArrayListHistory)
                            TandaMasuk+=1
                            val isAppInstalled = DataLocal.isAppInstalledBefore(requireActivity())
                            if(isAppInstalled){
                                TandaMasuk+=1
                            }
                            saveLocalInstalled(TandaMasuk)
                            saveDataDetection()
                        }
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        // Kode Anda di sini
                        binding.detailDesc.adapter = adapter
                    }, 3000)

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
        editor.putString("hasilDeteksi", btnIndikasi.text.toString())
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}