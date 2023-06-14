package com.shubh.learn.ui.companyHome.adminmanager

import android.Manifest
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.print.PrintHelper
import com.shubh.learn.R
import com.shubh.learn.adapter.AdapterRewardsList
import com.shubh.learn.databinding.FragmentCompenyFriendsDetailsBinding
import com.shubh.learn.dto.LoginDto
import com.shubh.learn.dto.PointsDto
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
import java.io.ByteArrayOutputStream
import java.io.File

class CompenyFriendsDetailsFragment : Fragment(), onRewardItemClick {
    private val MY_PERMISSION_CONSTANT = 5
    private lateinit var binding: FragmentCompenyFriendsDetailsBinding
    lateinit var sharedPref: SharedPref
    lateinit var apiInterface: ProviderInterface
    var managerListDto: ArrayList<PointsDto.Result> = ArrayList()
    lateinit  var friendID :String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_compeny_friends_details,
            container,
            false
        )
        binding.header.tvLogo.text = getString(R.string.comp_frnd)
        binding.header.imgHeader.setOnClickListener {
            requireActivity().onBackPressed()
        }
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)


        if (arguments != null) {
            val result = requireArguments().getSerializable("Result") as UserDto?
            Log.e("getuserValidate", result.toString())
            binding.firstName.text = result?.first_name
            binding.lastName.text = result?.last_name
            binding.txtEmail.text = result?.email
            binding.txtPhoneNo.text = "+${result?.country} ${result?.mobile}"
            binding.town.text = result?.town
            binding.objectName.text = result?.object_name
            binding.totalCoins.text = result?.rewards_point.toString()
           /* if (result?.pr_qrcode_status=="Defualt"){
                binding.totalCoinsPer.text ="05"

            }else{*/
            binding.totalCoinsPer.text = result?.pr_qrcode_point.toString()//}
            if (sharedPref.getStringValue(Constant.USER_TYPE).toString()=="Administrator"){
                        binding.editCoin.visibility=View.VISIBLE
                        binding.editCoinPer.visibility=View.VISIBLE
                    }else{
                binding.editCoin.visibility=View.GONE
                binding.editCoinPer.visibility=View.GONE
            }
            try {
                val qrgEncoder = QRGEncoder(
                    "Sebatekscode=" + result?.id, null, "TEXT", 2048
                )
                qrgEncoder.colorBlack = Color.parseColor("#000000")
                qrgEncoder.colorWhite = Color.WHITE
                // Getting QR-Code as Bitmap
                val bitmap = qrgEncoder.getBitmap(0)
                // Setting Bitmap to ImageView
                binding.scann.setImageBitmap(bitmap)
                binding.tvLogo.text = result?.id
                friendID =   result?.id.toString()
                binding.tvUserName.text = result?.first_name + " " + result?.last_name

            } catch (e: Exception) {
                Log.e(ContentValues.TAG, e.toString())
            }

            if (result != null) {
                //   getmanagerlist(result.id)
            }

        }
        binding.editCoin.setOnClickListener{
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.attributes?.windowAnimations =
                android.R.style.Widget_Material_ListPopupWindow
            dialog.setContentView(R.layout.change_coin)
            val lp = WindowManager.LayoutParams()
            val window: Window = dialog.window!!
            lp.copyFrom(window.attributes)
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = lp
            val edt_coin: EditText = dialog.findViewById(R.id.edt_coin)
            edt_coin.setText(binding.totalCoins.text.toString().trim())
            val yes_btn: TextView = dialog.findViewById(R.id.btn_yes)
            val no_btn: TextView = dialog.findViewById(R.id.btn_no)
            no_btn.setOnClickListener {
                dialog.dismiss()
            }
            yes_btn.setOnClickListener {
                var coin = edt_coin.text.toString()
                if (coin.isEmpty()) edt_coin.error= getString(R.string.empty)
                else{
                    delete_child_api(coin)
                    dialog.dismiss()
                }
                //  delete_child_api(model.id.toString())
            }
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

        }
        binding.editCoinPer.setOnClickListener{
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.attributes?.windowAnimations =
                android.R.style.Widget_Material_ListPopupWindow
            dialog.setContentView(R.layout.change_coin)
            val lp = WindowManager.LayoutParams()
            val window: Window = dialog.window!!
            lp.copyFrom(window.attributes)
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = lp
            val edt_coin: EditText = dialog.findViewById(R.id.edt_coin)
            edt_coin.setText(binding.totalCoinsPer.text.toString().trim())
            val yes_btn: TextView = dialog.findViewById(R.id.btn_yes)
            val no_btn: TextView = dialog.findViewById(R.id.btn_no)
            no_btn.setOnClickListener {
                dialog.dismiss()
            }
            yes_btn.setOnClickListener {
                var coin = edt_coin.text.toString()
                if (coin.isEmpty()) edt_coin.error= getString(R.string.empty)
                else{
                    delete_child_api2(coin)
                    dialog.dismiss()
                }
                //  delete_child_api(model.id.toString())
            }
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

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

                }else{



                if (checkPermissionForReadStorage()) {
                    DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.getting_user))
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
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSION_CONSTANT )
                }

                }
            } catch (e: Exception) {
                Log.e("TAG", "onCreateView: " + e.message)
                e.printStackTrace()
            }
        }
        binding.printQr.setOnClickListener {
            doPhotoPrint()
        }
        return binding.root
    }
    private fun delete_child_api(toString: String) {
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.getting_user))
        val userid = sharedPref.getStringValue(Constant.USER_ID).toString()
        val auth_token = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
        val map = HashMap<String, String>()
        map["user_id"] = friendID
        map["rewards_point"] = toString
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        var apiInterface: ProviderInterface =
            ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        apiInterface.update_wallet_point(map).enqueue(object : Callback<LoginDto?> {
            override fun onResponse(
                call: Call<LoginDto?>,
                response: Response<LoginDto?>
            ) {
                DataManager.instance.hideProgressMessage()
                try {
                    if (response.body()?.status == "1") {
                        //     binding.root.successSnack(res.message)
                        binding.totalCoins.text = response.body()?.result?.rewards_point.toString()
                        binding.totalCoinsPer.text = response.body()?.result?.pr_qrcode_point.toString()
                        binding.root.successSnack(response.body()?.message!!)
                        //getmanagerlist()
                    } else {
                        binding.root.errorSnack(response.body()?.message!!)

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                    Timber.tag("Exception").e("Exception = %s", e.printStackTrace())
                    Timber.tag("Exception").e("Exception = %s", e.localizedMessage)
                }
            }

            override fun onFailure(call: Call<LoginDto?>, t: Throwable) {
                call.cancel()
                DataManager.instance.hideProgressMessage()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                binding.root.errorSnack(t.message.toString())
            }

        })
    }
    private fun delete_child_api2(toString: String) {
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.getting_user))
        val userid = sharedPref.getStringValue(Constant.USER_ID).toString()
        val auth_token = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
        val map = HashMap<String, String>()
        map["company_id"] = friendID
        map["user_id"] = userid
        map["token"] = auth_token
        map["pr_qrcode_point"] = toString
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        var apiInterface: ProviderInterface =
            ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        apiInterface.update_per_qrcode(map).enqueue(object : Callback<LoginDto?> {
            override fun onResponse(
                call: Call<LoginDto?>,
                response: Response<LoginDto?>
            ) {
                DataManager.instance.hideProgressMessage()
                try {
                    if (response.body()?.status == "1") {
                        //     binding.root.successSnack(res.message)
                        binding.totalCoinsPer.text = response.body()?.result?.pr_qrcode_point.toString()
                        binding.root.successSnack(response.body()?.message!!)
                        //getmanagerlist()
                    } else {
                        binding.root.errorSnack(response.body()?.message!!)

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                    Timber.tag("Exception").e("Exception = %s", e.printStackTrace())
                    Timber.tag("Exception").e("Exception = %s", e.localizedMessage)
                }
            }

            override fun onFailure(call: Call<LoginDto?>, t: Throwable) {
                call.cancel()
                DataManager.instance.hideProgressMessage()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                binding.root.errorSnack(t.message.toString())
            }

        })
    }

    private fun doPhotoPrint() {
        activity?.also { context ->
            PrintHelper(context).apply {
                scaleMode = PrintHelper.SCALE_MODE_FIT
            }.also { printHelper ->
                // val bitmap = binding.scann.drawable.toBitmap()
                val bitmap = Bitmap.createBitmap(
                    binding.qrLay.width,
                    binding.qrLay.height,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                binding.qrLay.draw(canvas)
                val id: String = sharedPref.getStringValue(Constant.USER_ID).toString()
                printHelper.printBitmap("Sebsteck Qr$id", bitmap)
            }
        }
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
        val profileImage = File(imag)
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
        val friend_id = friendID.toRequestBody("text/plain".toMediaTypeOrNull())
        val authtoken = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        apiInterface.add_gallery(
            friend_id,  user_id, authtoken, profileFilePart
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

    private fun getmanagerlist(id: String) {
        binding.managerList.showShimmerAdapter()
        // managerListDto.clear()
        //  val userid = sharedPref.getStringValue(Constant.USER_ID).toString()
        val auth_token = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
        val map = HashMap<String, String>()
        //  map["token"] = auth_token
        map["user_id"] = id
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_rewards(map).enqueue(object : Callback<PointsDto?> {
            override fun onResponse(
                call: Call<PointsDto?>,
                response: Response<PointsDto?>
            ) {
                binding.managerList.hideShimmerAdapter()
                try {
                    val res: PointsDto = response.body()!!
                    if (response.body() != null && response.body()!!.status == "1") {
                        //     binding.root.successSnack(res.message)
                        managerListDto = res.result
                        if (managerListDto.size >= 1) {
                            binding.noData.visibility = View.GONE
                            val adapterRideOption = AdapterRewardsList(
                                requireActivity(),
                                managerListDto, this@CompenyFriendsDetailsFragment
                            )
                            binding.managerList.adapter = adapterRideOption
                            binding.managerList.setHasFixedSize(true)
                        } else {
                            binding.noData.visibility = View.VISIBLE
                        }

                    } else {
                        binding.root.errorSnack(res.message)
                        binding.noData.visibility = View.VISIBLE

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    binding.noData.visibility = View.VISIBLE

                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<PointsDto?>, t: Throwable) {
                binding.managerList.hideShimmerAdapter()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                binding.root.errorSnack(t.message.toString())
                binding.noData.visibility = View.VISIBLE


            }

        })

    }

    override fun onItemClick(model: PointsDto.Result) {
        val bundle = Bundle()
        // bundle.putSerializable("Result", model)
        // Log.e("TAG", "onItemClick: "+model.toString())
        //   bundle.putParcelable("data", userDto)
        // Navigation.findNavController(binding.root).
        //navigate(R.id.action_compenyManagerListFragment_to_compenyManagerDetailsFragment,bundle)

    }

}