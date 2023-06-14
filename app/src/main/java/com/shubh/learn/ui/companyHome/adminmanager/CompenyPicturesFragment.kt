package com.shubh.learn.ui.companyHome.adminmanager

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.shubh.learn.R
import com.shubh.learn.adapter.AdapterPicturesList
import com.shubh.learn.databinding.FragmentCompenyPicturesBinding
import com.shubh.learn.dto.PicturesListDto
import com.shubh.learn.retrofit.ApiClient
import com.shubh.learn.retrofit.ProviderInterface
import com.shubh.learn.utills.*
import com.vilborgtower.user.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class CompenyPicturesFragment : Fragment(), onPictureItemClick {

    private lateinit var binding: FragmentCompenyPicturesBinding
    lateinit var sharedPref: SharedPref
    lateinit var apiInterface: ProviderInterface
    var managerListDto: ArrayList<PicturesListDto.Result> = ArrayList()
    lateinit var adapterRideOption: AdapterPicturesList
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_compeny_pictures,
            container, false
        )
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)

        binding.header.tvLogo.text = getString(R.string.pictures)
        binding.header.imgHeader.setOnClickListener {
            requireActivity().onBackPressed()
        }
        getmanagerlist()
        return binding.root
    }


    private fun getmanagerlist() {
        managerListDto.clear()
        binding.managerList.showShimmerAdapter()
        val userid = sharedPref.getStringValue(Constant.USER_ID).toString()
        val auth_token = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
        val map = HashMap<String, String>()
        map["token"] = auth_token
        map["user_id"] = userid
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_gallery(map).enqueue(object : Callback<PicturesListDto?> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<PicturesListDto?>,
                response: Response<PicturesListDto?>
            ) {
                binding.managerList.hideShimmerAdapter()
                try {
                    val res: PicturesListDto = response.body()!!
                    if (response.body() != null && response.body()!!.status == "1") {
                        //     binding.root.successSnack(res.message)
                        managerListDto = res.result
                        if (managerListDto.size >= 1) {
                            binding.noData.visibility = View.GONE
                            adapterRideOption = AdapterPicturesList(
                                requireActivity(),
                                managerListDto,
                                this@CompenyPicturesFragment
                            )
                            binding.managerList.layoutManager =
                                GridLayoutManager(requireContext(), 3)
                            binding.managerList.adapter = adapterRideOption
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

            override fun onFailure(call: Call<PicturesListDto?>, t: Throwable) {
                binding.managerList.hideShimmerAdapter()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                binding.root.errorSnack(t.message.toString())
                binding.noData.visibility = View.VISIBLE


            }

        })

    }

    override fun onItemClick(model: PicturesListDto.Result) {
        Log.e("TAG", "onItemClick: " + model.toString())
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.attributes?.windowAnimations =
            android.R.style.Widget_Material_ListPopupWindow
        dialog.setContentView(R.layout.fullscreen_image)
        val lp = WindowManager.LayoutParams()
        val window: Window = dialog.window!!
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.GRAY))
        val img_header: ImageView = dialog.findViewById(R.id.img_header)
        val imageView: TouchImageView = dialog.findViewById(R.id.img)
        Glide.with(requireActivity())
            .load(model.image)
            .into(imageView)
        img_header.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

        /*   val bundle = Bundle()
           bundle.putSerializable("Result", model)
           Navigation.findNavController(binding.root).navigate(
               R.id.action_compenyFriendsListFragment_nav_to_compenyFriendsDetailsFragment,
               bundle
           )*/

    }

}