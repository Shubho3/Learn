package com.shubh.learn.ui.companyHome

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.blikoon.qrcodescanner.QrCodeActivity
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog
import com.shubh.learn.R
import com.shubh.learn.databinding.BottemSheeetLanguageBinding
import com.shubh.learn.databinding.FragmentCompenyHomeBinding
import com.shubh.learn.dto.LoginDto
import com.shubh.learn.retrofit.ApiClient
import com.shubh.learn.retrofit.ProviderInterface
import com.shubh.learn.utills.DataManager
import com.shubh.learn.utills.SharedPref
import com.shubh.learn.utills.errorSnack
import com.vilborgtower.user.utils.Constant
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

class CompenyHomeFragment : Fragment() {
    private lateinit var binding: FragmentCompenyHomeBinding
    lateinit var sharedPref: SharedPref
    private val MY_PERMISSION_CONSTANT = 5
    private val REQUEST_CODE_QR_SCAN = 101
    private var ff = "https://unec.edu.az/application/uploads/2014/12/pdf-sample.pdf"
    lateinit var apiInterface: ProviderInterface
    lateinit var res: LoginDto
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_compeny_home,
            container, false
        )
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)

        sharedPref = SharedPref(requireContext())
        Log.e("TAG", "onCreateView: " + sharedPref.getStringValue(Constant.USER_ID).toString())
        Log.e("TAG", "onCreateView: " + sharedPref.getStringValue(Constant.AUTH_TOKEN).toString())
        Log.e(
            "TAG",
            "onCreateView: " + sharedPref.getStringValue(Constant.FIREBASETOKEN).toString()
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ){
            if (checkPermissionForReadStorage13()) {

            } else {
                Toast.makeText(
                    context,
                    getString(R.string.please_allow_permission),
                    Toast.LENGTH_SHORT
                ).show()

            }

        }else{
            if (checkPermissionForReadStorage()) {

            } else {
                Toast.makeText(
                    context,
                    getString(R.string.please_allow_permission),
                    Toast.LENGTH_SHORT
                ).show()

            }
        }



        if (sharedPref.getStringValue(Constant.USER_TYPE).toString()
                .equals("Company", true)
        ) {

            binding.layoutCompeny.visibility = View.VISIBLE
            binding.layoutManager.visibility = View.GONE
            binding.layoutAdmin.visibility = View.GONE
        } else if (sharedPref.getStringValue(Constant.USER_TYPE).toString()
                .equals("Manager", true)
        ) {
            binding.layoutManager.visibility = View.VISIBLE
            binding.layoutCompeny.visibility = View.GONE
            binding.layoutAdmin.visibility = View.GONE
        } else if (sharedPref.getStringValue(Constant.USER_TYPE).toString()
                .equals("Administrator", true)
        ) {
            binding.layoutAdmin.visibility = View.VISIBLE
            binding.layoutManager.visibility = View.GONE
            binding.layoutCompeny.visibility = View.GONE
        }

        binding.logout.setOnClickListener {

            val alertDialog: AlertDialog = AlertDialog.Builder(requireActivity()).create()
            alertDialog.setTitle(getString(R.string.logout))
            alertDialog.setMessage(getString(R.string.arey_you_sure_to_logout))
            alertDialog.setButton(
                AlertDialog.BUTTON_POSITIVE, getString(R.string.yes)
            ) { dialog, _ ->
                dialog.dismiss()
                sharedPref.clearAllPreferences()
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_compenyHomeFragment_nav_to_login_type_nav)
            }
            alertDialog.setButton(
                AlertDialog.BUTTON_NEGATIVE, getString(R.string.no)
            ) { dialog, _ ->
                dialog.dismiss()

            }

            alertDialog.show()


        }
        binding.selectLanguage.setOnClickListener {
            openLanguageBottomSheet()

        }
        binding.btnPoints.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_compenyHomeFragment_nav_to_compenyPointsFragment)
        }
        binding.btnRqNId.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_compenyHomeFragment_nav_to_showQRFragment)
        }
        binding.btnScanRq.setOnClickListener {
            /* Navigation.findNavController(binding.root)
                 .navigate(R.id.action_compenyHomeFragment_nav_to_showQRFragment)*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ){
                if (checkPermissionForReadStorage13()) {
                    val i = Intent(requireContext(), QrCodeActivity::class.java)
                    startActivityForResult(i, REQUEST_CODE_QR_SCAN)
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.please_allow_permission),
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }else{
                if (checkPermissionForReadStorage()) {
                    val i = Intent(requireContext(), QrCodeActivity::class.java)
                    startActivityForResult(i, REQUEST_CODE_QR_SCAN)
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.please_allow_permission),
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        }
        binding.btnScanRqAdmin.setOnClickListener {
            /* Navigation.findNavController(binding.root)
                 .navigate(R.id.action_compenyHomeFragment_nav_to_showQRFragment)*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ){
                if (checkPermissionForReadStorage13()) {
                    val i = Intent(requireContext(), QrCodeActivity::class.java)
                    startActivityForResult(i, REQUEST_CODE_QR_SCAN)
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.please_allow_permission),
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }else{
                if (checkPermissionForReadStorage()) {
                    val i = Intent(requireContext(), QrCodeActivity::class.java)
                    startActivityForResult(i, REQUEST_CODE_QR_SCAN)
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.please_allow_permission),
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

        }
        binding.btnToCompeny.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_compenyHomeFragment_nav_to_contactToCompanyFragment)
        }
        binding.btnToPictures.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_compenyHomeFragment_nav_to_compenyPicturesFragment)
        }
        binding.btnToPictures2.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_compenyHomeFragment_nav_to_compenyPicturesFragment)
        }
        binding.btnToPictures3.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_compenyHomeFragment_nav_to_compenyPicturesFragment)
        }
        binding.btnToPrice.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(
                "Result", ff
            )
            // bundle.putString("Result", "https://sebateks.pl//sebateks////uploads//images//QR_IMG_75045.png")
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_compenyHomeFragment_nav_to_compenyPriceListFragment, bundle)

            Log.e("TAG", "onCreateView:   fdgdfhdfjdf0000 " + ff)
        }
        binding.btnCompenyFrnd.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_compenyHomeFragment_nav_to_compenyFriendsListFragment)
        }
        binding.btnCompenyFrndAdmin.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_compenyHomeFragment_nav_to_compenyFriendsListFragment)
        }
        binding.btnAdminSetting2.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_compenyHomeFragment_nav_to_compenyFriendsListFragment)
        }
        binding.btnToNote.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_compenyHomeFragment_nav_to_ccompenyNoteFragment)
        }
        binding.btnToNoteAdmin.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_compenyHomeFragment_nav_to_ccompenyNoteFragment)
        }
        binding.btnManager.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_compenyHomeFragment_nav_to_compenyManagerListFragment)
        }
        binding.btnAdminSetting.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_compenyHomeFragment_nav_to_compenysettingFragment)
        }

        getProfile()
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
    private fun openLanguageBottomSheet() {
        val bottomSheetBinding: BottemSheeetLanguageBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(requireActivity()), R.layout.bottem_sheeet_language,
                null, false
            )
        val bottomSheetDialog = RoundedBottomSheetDialog(requireActivity())
        bottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
        if (sharedPref.getStringValue(Constant.LANGUAGE) == "pl") {
            bottomSheetBinding.pol.isChecked = true
        } else {
            bottomSheetBinding.eng.isChecked = true

        }
        bottomSheetBinding.englishRela.setOnClickListener {
            sharedPref.setStringValue(Constant.LANGUAGE, "en")
            setLocale(sharedPref.getStringValue(Constant.LANGUAGE))
            change_languageapi("English")
            bottomSheetDialog.dismiss()
            activity?.recreate()
        }
        bottomSheetBinding.polishRela.setOnClickListener {
            sharedPref.setStringValue(Constant.LANGUAGE, "pl")
            setLocale(sharedPref.getStringValue(Constant.LANGUAGE))
            change_languageapi("Polish")
            bottomSheetDialog.dismiss()
            activity?.recreate()

        }
    }

    private fun change_languageapi(lang: String) {
        val userid = sharedPref.getStringValue(Constant.USER_ID).toString()
        val auth_token = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
        val map = HashMap<String, String>()
        map["token"] = auth_token
        map["user_id"] = userid
        map["lang"] = lang
        Timber.tag(ContentValues.TAG).e("add_rewardsadd_rewardsadd_rewards= %s", map)
        apiInterface.add_update_lang(map).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {

            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())

            }

        })

    }


    fun setLocale(lang: String?) {
        val myLocale = Locale(lang)
        val res = resources
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, res.displayMetrics)
    }

    private fun checkPermissionForReadStorage(): Boolean {
        return if ((ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
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
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    MY_PERMISSION_CONSTANT
                )
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        try {


            if (resultCode != Activity.RESULT_OK) {
                Log.e(ContentValues.TAG, "COULD NOT GET A GOOD RESULT.")
                if (data == null) return
                //Getting the passed result
                val result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image")
                if (result != null) {
                    val alertDialog: AlertDialog = AlertDialog.Builder(requireActivity()).create()
                    alertDialog.setTitle(getString(R.string.scan_errer))
                    alertDialog.setMessage(getString(R.string.qr_cannot_be_scanned))
                    alertDialog.setButton(
                        AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok)
                    ) { dialog, _ -> dialog.dismiss() }

                    alertDialog.show()
                }
                return
            } else
                if (requestCode == REQUEST_CODE_QR_SCAN) {
                    if (data == null) {
                        binding.root.errorSnack(getString(R.string.invalid_qr))
                        return
                    }
                    //Getting the passed result
                    val result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult")
                    Log.e(ContentValues.TAG, "Have scan result in your app activity :$result")
                    if (result != null) {
                        if (result.contains("Sebateks", true)) {
                            val oldValue = "Sebatekscode="
                            val output = result.replace(oldValue, " ")
                            Log.e(ContentValues.TAG, "onActivityResult: " + output.trim())
                            add_rewards(output.trim())

                        } else {
                            binding.root.errorSnack(getString(R.string.invalid_qr))
                        }
                    }

                }
        } catch (e: Exception) {
            binding.root.errorSnack(e.message.toString(), 4)

        }
    }

    private fun add_rewards(output: String) {
        DataManager.instance.showProgressMessage(
            requireActivity(),
            getString(R.string.getting_user)
        )
        val userid = sharedPref.getStringValue(Constant.USER_ID).toString()
        val auth_token = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
        val map = HashMap<String, String>()
        map["token"] = auth_token
        map["user_id"] = userid
        map["other_id"] = output
        Timber.tag(ContentValues.TAG).e("add_rewardsadd_rewardsadd_rewards= %s", map)
        apiInterface.add_rewards(map).enqueue(object : Callback<LoginDto?> {
            override fun onResponse(call: Call<LoginDto?>, response: Response<LoginDto?>) {
                DataManager.instance.hideProgressMessage()
                try {
                    res = response.body()!!
                    if (response.body() != null && response.body()!!.status == "1") {
                        SuccessDialog()
                    } else {
                        binding.root.errorSnack(getString(R.string.invalid_qr))

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()

                    Timber.tag("Exception").e("Exception = %s", e.message)
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

    fun SuccessDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.attributes?.windowAnimations =
            android.R.style.Widget_Material_ListPopupWindow
        dialog.setContentView(R.layout.success_msg)
        val lp = WindowManager.LayoutParams()
        val window: Window = dialog.window!!
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            val bundle = Bundle()
            bundle.putSerializable("Result", res.result)
            Navigation.findNavController(binding.root).navigate(
                R.id.action_compenyFriendsListFragment_nav_to_compenyFriendsDetailsFragment,
                bundle
            )
            dialog.dismiss()
            //   activity?.onBackPressed()
        }, 3000)

    }

    private fun getProfile() {
        DataManager.instance.showProgressMessage(
            requireActivity(),
            getString(R.string.getting_user)
        )
        val userid = sharedPref.getStringValue(Constant.USER_ID).toString()
        val auth_token = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
        val map = HashMap<String, String>()
        map["token"] = auth_token
        map["user_id"] = userid
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_profile(map).enqueue(object : Callback<LoginDto?> {
            override fun onResponse(
                call: Call<LoginDto?>,
                response: Response<LoginDto?>
            ) {
                DataManager.instance.hideProgressMessage()

                try {
                    val res: LoginDto = response.body()!!
                    if (response.body() != null && response.body()!!.status == "1") {
                        ff = res.result.price_image

                    } else {
                        if (res.message == "Auth Failed") {
                            binding.root.errorSnack(getString(R.string.auth_errer))
                            Thread.sleep(2000)
                            sharedPref.clearAllPreferences()
                            Navigation.findNavController(binding.root)
                                .navigate(R.id.action_compenyHomeFragment_nav_to_login_type_nav)
                        } else {
                            binding.root.errorSnack(res.message)
                        }

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()

                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<LoginDto?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                call.cancel()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                binding.root.errorSnack(t.message.toString())
            }

        })

    }

}