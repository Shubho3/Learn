package com.shubh.learn.ui.companyHome.adminmanager

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.shubh.learn.R
import com.shubh.learn.databinding.FragmentCompenyAddFriendsBinding
import com.shubh.learn.dto.LoginDto
import com.shubh.learn.dto.UserDto
import com.shubh.learn.retrofit.ApiClient
import com.shubh.learn.retrofit.ProviderInterface
import com.shubh.learn.utills.*
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
import timber.log.Timber
import java.io.*


class CompenyAddFriendsFragment : Fragment() {
    private lateinit var binding: FragmentCompenyAddFriendsBinding
    lateinit var sharedPref: SharedPref
    lateinit var apiInterface: ProviderInterface
    lateinit var comp_id: String
    lateinit var model :UserDto
    private val CHILD_DIR = "images"
    private val TEMP_FILE_NAME = "img"
    private val FILE_EXTENSION = ".png"

    private val COMPRESS_QUALITY = 100
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_compeny_add_friends,
            container, false
        )
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        binding.header.tvLogo.text = getString(R.string.add_com_frnd)
        binding.header.imgHeader.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.btnAdd.setOnClickListener {
            //  requireActivity().onBackPressed()
            val email = binding.edtEmail.text.toString()
            val firstname = binding.firstName.text.toString()
            val lastname = binding.lastName.text.toString()
            val pass = binding.edtPass.text.toString()
            val no = binding.editNo.text.toString()
            val obj = binding.objectName.text.toString()
            val town = binding.town.text.toString()
            val code = binding.ccp.selectedCountryCode.toString()
            if (firstname == "") binding.firstName.error = (getString(R.string.empty))
            if (lastname == "") binding.lastName.error = (getString(R.string.empty))
            if (obj == "") binding.objectName.error = (getString(R.string.empty))
            if (town == "") binding.town.error = (getString(R.string.empty))
            if (email == "" && !EmailValidation.isEmailValid(email)) binding.edtEmail.error =
                (getString(R.string.invalid_email_address))
            if (pass == "") binding.edtPass.error = (getString(R.string.empty))
            else if (no == "") binding.editNo.error = (getString(R.string.empty))
            else if (Utils.hasInternetConnection(requireActivity())) addManagerFriend(
                email,
                firstname,
                lastname,
                no,
                code,
                pass,
                town,
                obj
            )
            else binding.root.errorSnack(getString(R.string.no_internet_connection))

        }

        return binding.root
    }

    private fun addManagerFriend(
        email: String,
        firstname: String,
        lastname: String, no: String,
        cCode: String, pass: String, town: String, obj: String
    ) {
        DataManager.instance.showProgressMessage(
            requireActivity(),
            getString(R.string.getting_user)
        )
        val userid = sharedPref.getStringValue(Constant.USER_ID).toString()
        val auth_token = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
        val map = HashMap<String, String>()
        map["email"] = email
        map["first_name"] = firstname
        map["last_name"] = lastname
        map["mobile"] = no
        map["country"] = cCode
        map["password"] = pass
        map["token"] = auth_token
        map["user_id"] = userid
        map["town"] = town
        map["object_name"] = obj
        Log.e("TAG", "addManagerFriend: " + map)
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.add_company(map).enqueue(object : Callback<LoginDto?> {
            override fun onResponse(call: Call<LoginDto?>, response: Response<LoginDto?>) {
               // DataManager.instance.hideProgressMessage()
                try {
                    val res: LoginDto = response.body()!!
                    if (response.body() != null && response.body()!!.status == "1") {
                        comp_id = response.body()!!.result.id
                        model = response.body()!!.result
                        uploadQrCode(comp_id)
                      //  binding.root.successSnack(res.message)
                       // requireActivity().onBackPressed()
                    } else {
                        binding.root.errorSnack(res.message)
                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                       
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<LoginDto?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                binding.root.errorSnack(t.message.toString())

            }

        })

    }

    private fun uploadQrCode(id: String) {
        val qrgEncoder = QRGEncoder(
            "Sebatekscode=" + id, null, "TEXT", 2048)
        qrgEncoder.colorBlack = Color.parseColor("#000000")
        qrgEncoder.colorWhite = Color.WHITE
        // Getting QR-Code as Bitmap
       var bitmap:Bitmap  = qrgEncoder.getBitmap(0)
     //saveMediaToStorage(bitmap)

        saveImgToCache(bitmap,"Sebatekscode=" + id)?.let { UploadQR(it) }
    }
    fun saveImgToCache(bitmap: Bitmap,  name: String): File? {
        val imagesDir = requireActivity().cacheDir
        val image = File(imagesDir, name)
        val fos = FileOutputStream(image)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        val tempUri: Uri = getImageUri(requireContext(), bitmap)!!
        val imag = RealPathUtil.getRealPath(requireContext(), tempUri)
        fos.flush()
        fos.close()
        val  profileImage = File(imag)
        return profileImage
    }


    @SuppressLint("SuspiciousIndentation")
    private fun saveMediaToStorage(bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Toast.makeText(requireContext(), " saved to Gallery", Toast.LENGTH_SHORT)
                .show()
            requireActivity().contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (imageUri != null) {
                }
                fos = imageUri?.let { resolver.openOutputStream(it) }
                val imag = RealPathUtil.getRealPath(requireContext(), imageUri)
                val  profileImage = File(imag)
                  UploadQR(profileImage)

            }
        } else {
            /*Toast.makeText(requireContext(), " saved to Gallery", Toast.LENGTH_SHORT)
                .show()*/
            val imagesDir =
                Environment.getDownloadCacheDirectory()
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
            val tempUri: Uri = getImageUri(requireContext(), bitmap)!!
            val imag = RealPathUtil.getRealPath(requireContext(), tempUri)
            val  profileImage = File(imag)
            UploadQR(profileImage)
        }



    }

    private fun UploadQR(profileImage: File)
        {
            val attachmentEmpty: RequestBody
            val profileFilePart: MultipartBody.Part = MultipartBody.Part.createFormData("qr_image", profileImage.name, profileImage.asRequestBody("image/*".toMediaTypeOrNull()))
            val code  ="Sebatekscode=" + comp_id
            val namedata = comp_id.toRequestBody("text/plain".toMediaTypeOrNull())
            val codess = code.toRequestBody("text/plain".toMediaTypeOrNull())
            val user_id = sharedPref.getStringValue(Constant.USER_ID).toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
            val authtoken = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
            apiInterface.add_qr_image(
                user_id, authtoken,codess,namedata, profileFilePart
            ).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    DataManager.instance.hideProgressMessage()
                    try {
                        val bundle = Bundle()
                        bundle.putSerializable("Result", model)
                        Navigation.findNavController(binding.root).navigate(
                            R.id.action_compenyFriendsListFragment_nav_to_compenyFriendsDetailsFragment,
                            bundle
                        )
                    } catch (e: Exception) {
                    }
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