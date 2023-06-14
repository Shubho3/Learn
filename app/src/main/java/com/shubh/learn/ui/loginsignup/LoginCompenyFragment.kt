package com.shubh.learn.ui.loginsignup

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.shubh.learn.R
import com.shubh.learn.databinding.FragmentLoginCompenyBinding
import com.shubh.learn.dto.LoginDto
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class LoginCompenyFragment : Fragment() {
    private lateinit var binding: FragmentLoginCompenyBinding
    lateinit var sharedPref: SharedPref
    lateinit var apiInterface: ProviderInterface
    lateinit var comp_id :String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login_compeny,
            container, false
        )
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        binding.btnSubmit.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val pass = binding.edtPass.text.toString()
            if (email == "") binding.edtEmail.errorSnack(getString(R.string.empty_fields))
            else if (pass == "") binding.edtPass.errorSnack(getString(R.string.empty_fields))
            else //if (Utils.hasInternetConnection(requireActivity()))
                loginApi(email, pass)
           // else binding.root.errorSnack(getString(R.string.no_internet_connection))
        }
        return binding.root
    }
    private fun loginApi(email: String, pass: String) {
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.getting_user))
        val map = HashMap<String, String>()
        map["email"] = email
        map["password"] = pass
        //map["register_id"] = "123"
         map["register_id"] = sharedPref.getStringValue(Constant.FIREBASETOKEN).toString()
        map["type"] = sharedPref.getStringValue(Constant.USER_TYPE).toString()
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.login(map).enqueue(object : Callback<LoginDto?> {
            override fun onResponse(
                call: Call<LoginDto?>,
                response: Response<LoginDto?>
            ) {
                DataManager.instance.hideProgressMessage()
                try {
                    val res: LoginDto = response.body()!!

                    if (response.body() != null && response.body()!!.status == "1") {
                        sharedPref.setStringValue(Constant.USER_ID, res.result.id)
                        sharedPref.setStringValue(Constant.AUTH_TOKEN, res.result.token)
                        sharedPref.setStringValue(Constant.USER_NAME, res.result.first_name+" "+ res.result.last_name)
                        //  sharedPref.setStringValue(Constant.USER_TYPE, res.result.)
                        sharedPref.setBooleanValue(Constant.IS_LOGIN, true)

                        if (sharedPref.getStringValue(Constant.USER_TYPE).toString()
                                .equals("Company", true)
                        ) {
                            if (res.result.qr_status.toString() =="true"){

                            }else{
                                comp_id  =  res.result.id
                                uploadQrCode(comp_id)

                            }

                        }


                        binding.root.successSnack(res.message)

                         Navigation.findNavController(binding.root).navigate(R.id.action_loginCompenyFragment_nav_to_compenyHomeFragment)

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
        var bitmap: Bitmap = qrgEncoder.getBitmap(0)
        //saveMediaToStorage(bitmap)

        saveImgToCache(bitmap,"Sebatekscode=" + id)?.let { UploadQR(it) }
    }
    fun saveImgToCache(bitmap: Bitmap, name: String): File? {
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
                /*try {
                    val bundle = Bundle()
                    bundle.putSerializable("Result", model)
                    Navigation.findNavController(binding.root).navigate(
                        R.id.action_compenyFriendsListFragment_nav_to_compenyFriendsDetailsFragment,
                        bundle
                    )
                } catch (e: Exception) {
                }*/
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