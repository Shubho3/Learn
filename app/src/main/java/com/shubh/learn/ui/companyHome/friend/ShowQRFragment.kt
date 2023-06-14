package com.shubh.learn.ui.companyHome.friend

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.print.PrintHelper
import com.shubh.learn.R
import com.shubh.learn.databinding.FragmentShowQrBinding
import com.shubh.learn.retrofit.ApiClient
import com.shubh.learn.retrofit.ProviderInterface
import com.shubh.learn.utills.DataManager
import com.shubh.learn.utills.QRGEncoder
import com.shubh.learn.utills.SharedPref
import com.vilborgtower.user.utils.Constant
import com.vilborgtower.user.utils.RealPathUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File

class ShowQRFragment : Fragment() {
    lateinit var bitmap: Bitmap
    var profileImage: File? = null
    lateinit var apiInterface: ProviderInterface
    private lateinit var binding: FragmentShowQrBinding
    lateinit var sharedPref: SharedPref
    private val MY_PERMISSION_CONSTANT = 5

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_show_qr,
            container, false
        )
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        sharedPref = SharedPref(requireContext())
        binding.header.tvLogo.text = getString(R.string.qr_code)
        binding.header.imgMenu.visibility = View.VISIBLE
        binding.header.imgMenu.setImageDrawable(requireActivity().getDrawable(R.drawable.image_stared))
        binding.header.imgHeader.setOnClickListener {
            requireActivity().onBackPressed()
        }

           try {
               val qrgEncoder = QRGEncoder(
                    "Sebatekscode= " + sharedPref.getStringValue(
                       Constant.USER_ID
                   ), null, "TEXT", 2048
               )
               qrgEncoder.colorBlack = Color.parseColor("#000000")
               qrgEncoder.colorWhite = Color.WHITE
               // Getting QR-Code as Bitmap
            bitmap = qrgEncoder.getBitmap(0)
            // Setting Bitmap to ImageView
            binding.scann.setImageBitmap(bitmap)
               binding.tvLogo.text =   sharedPref.getStringValue(Constant.USER_ID)
               binding.tvUserName.text =   sharedPref.getStringValue(Constant.USER_NAME)
        } catch (e:Exception) {
            Log.e(ContentValues.TAG, e.toString())
        }
        binding.saveCard.setOnClickListener {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ){
                    if (checkPermissionForReadStorage13()) {
                        DataManager.instance.showProgressMessage(
                            requireActivity(),
                            getString(R.string.getting_user)
                        )
                        val bitmap = Bitmap.createBitmap(
                            binding.qrLay.width,
                            binding.qrLay.height,
                            Bitmap.Config.ARGB_8888
                        )
                        val canvas = Canvas(bitmap)
                        binding.qrLay.draw(canvas)
                        saveMediaToStorage(bitmap)
                    } else {

                        requestPermissions(
                            arrayOf(
                                Manifest.permission.READ_MEDIA_IMAGES),
                            MY_PERMISSION_CONSTANT
                        )
                    }

                }else {

                    if (checkPermissionForReadStorage()) {
                        DataManager.instance.showProgressMessage(
                            requireActivity(),
                            getString(R.string.getting_user)
                        )
                        val bitmap = Bitmap.createBitmap(
                            binding.qrLay.width,
                            binding.qrLay.height,
                            Bitmap.Config.ARGB_8888
                        )
                        val canvas = Canvas(bitmap)
                        binding.qrLay.draw(canvas)
                        saveMediaToStorage(bitmap)
                    } else {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ),
                            MY_PERMISSION_CONSTANT
                        )
                    }

                }
            } catch (e: Exception) {
                Log.e("TAG", "onCreateView: "+e.message )
                e.printStackTrace()
            }
        }
        binding.printQr.setOnClickListener{
            doPhotoPrint()
        }
        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissionForReadStorage13(): Boolean  {
        return if ((ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED)
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.CAMERA
                )
                ||
                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.READ_MEDIA_IMAGES
                )

            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_MEDIA_IMAGES,
                    ),
                    MY_PERMISSION_CONSTANT
                )
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ),
                    MY_PERMISSION_CONSTANT
                )
            }
            false
        } else {

            true
        }
    }
    private fun doPhotoPrint() {
        activity?.also { context ->
            PrintHelper(context).apply {
                scaleMode = PrintHelper.SCALE_MODE_FIT
            }.also { printHelper ->
               // val bitmap = binding.scann.drawable.toBitmap()
                val bitmap = Bitmap.createBitmap(binding.qrLay.width, binding.qrLay.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                binding.qrLay.draw(canvas)
                val id:String =sharedPref.getStringValue(Constant.USER_ID).toString()
                printHelper.printBitmap("Sebsteck Qr$id", bitmap)
            }
        }
    }
    private fun checkPermissionForReadStorage(): Boolean {
        return if ((ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
                    != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
                    != PackageManager.PERMISSION_GRANTED)
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                ||
                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    MY_PERMISSION_CONSTANT
                )
            } else {
                requestPermissions(
                    arrayOf(
                         Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    MY_PERMISSION_CONSTANT
                )
            }
            false
        } else {
            true
        }
    }
    private fun saveMediaToStorage(bitmap: Bitmap) {
            val tempUri: Uri = getImageUri(requireContext(), bitmap)!!
            val imag = RealPathUtil.getRealPath(requireContext(), tempUri)
          val  profileImage = File(imag)
        UploadQR(profileImage)


    }



    private fun UploadQR(profileImage: File) {
        val attachmentEmpty: RequestBody
        val profileFilePart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "image",
            profileImage.name,
            profileImage.asRequestBody("image/*".toMediaTypeOrNull())
        )
        val user_id = sharedPref.getStringValue(Constant.USER_ID).toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
     val user_id2 = sharedPref.getStringValue(Constant.USER_ID).toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val authtoken = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        apiInterface.add_gallery(
            user_id2,      user_id, authtoken, profileFilePart
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                DataManager.instance.hideProgressMessage()
                Toast.makeText(requireContext(), getString(R.string.saved_to_g), Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
            }

        })


    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title" + System.currentTimeMillis(),
            null
        )
        return Uri.parse(path)
    }

}