package com.shubh.learn.ui.companyHome.friend
import android.app.Dialog
import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.shubh.learn.R
import com.shubh.learn.adapter.AdapterNotesList
import com.shubh.learn.databinding.FragmentCompenyNoteBinding
import com.shubh.learn.dto.NotesDto
import com.shubh.learn.retrofit.ApiClient
import com.shubh.learn.retrofit.ProviderInterface
import com.shubh.learn.utills.*
import com.vilborgtower.user.utils.Constant
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class CompenyNoteFragment : Fragment(), OnNoteClickListener {

    private lateinit var binding: FragmentCompenyNoteBinding
    lateinit var sharedPref: SharedPref
    lateinit var apiInterface: ProviderInterface


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_compeny_note,
            container, false
        )
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        sharedPref = SharedPref(requireContext())
        binding.header.tvLogo.text = getString(R.string.notes)
        binding.header.imgHeader.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.tvLogo.setOnClickListener {
            SuccessDialog()

        }

        getmanagerlist()
        return binding.root
    }

    fun SuccessDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.attributes?.windowAnimations =
            android.R.style.Widget_Material_ListPopupWindow
        dialog.setContentView(R.layout.add_note_item)
        val lp = WindowManager.LayoutParams()
        val window: Window = dialog.window!!
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        val edtTopic: EditText = dialog.findViewById(R.id.edt_topic)
        val edtMessage: EditText = dialog.findViewById(R.id.edt_message)
        val btnSubmit: Button = dialog.findViewById(R.id.btn_submit)
        btnSubmit.setOnClickListener {
            val topic = edtTopic.text.toString()
            val note = edtMessage.text.toString()
            if (topic == "") edtTopic.error = getString(R.string.empty)
            else if (note == "") edtMessage.error = getString(R.string.empty)
            else {
                dialog.dismiss()
                add_note(topic, note)
            }
        }
        dialog.show()
    }

    private fun getmanagerlist() {
        binding.managerList.showShimmerAdapter()
        // managerListDto.clear()
        val userid = sharedPref.getStringValue(Constant.USER_ID).toString()
        val usertype = sharedPref.getStringValue(Constant.USER_TYPE).toString()
        val auth_token = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
        val map = HashMap<String, String>()
        // map["token"] = auth_token
        map["user_type"] = usertype
        map["user_id"] = userid
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_notes(map).enqueue(object : Callback<NotesDto?> {
            override fun onResponse(
                call: Call<NotesDto?>,
                response: Response<NotesDto?>
            ) {
                binding.managerList.hideShimmerAdapter()
                try {
                    val res: NotesDto = response.body()!!
                    if (response.body() != null && response.body()!!.status == "1") {
                        //     binding.root.successSnack(res.message)
                        var managerListDto: ArrayList<NotesDto.Result> = ArrayList()
                        lateinit var adapterRideOption: AdapterNotesList
                        managerListDto = res.result
                        if (managerListDto.size >= 1) {
                            binding.noData.visibility = View.GONE
                            adapterRideOption = AdapterNotesList(
                                requireActivity(),
                                managerListDto, this@CompenyNoteFragment
                            )
                            binding.managerList.adapter = adapterRideOption
                            binding.managerList.setHasFixedSize(true)
                        } else {
                            binding.noData.visibility = View.VISIBLE
                        }

                    } else {
                        //   binding.root.errorSnack(res.message)
                        binding.noData.visibility = View.VISIBLE

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    binding.noData.visibility = View.VISIBLE

                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<NotesDto?>, t: Throwable) {
                binding.managerList.hideShimmerAdapter()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                binding.root.errorSnack(t.message.toString())
                binding.noData.visibility = View.VISIBLE


            }

        })
    }


    private fun add_note(topic: String, note: String) {
        DataManager.instance.showProgressMessage(
            requireActivity(),
            getString(R.string.getting_user)
        )
        val userid = sharedPref.getStringValue(Constant.USER_ID).toString()
        val auth_token = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
        val map = HashMap<String, String>()
        map["token"] = auth_token
        map["user_id"] = userid
        map["topic"] = topic
        map["notes"] = note
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        var apiInterface: ProviderInterface =
            ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        apiInterface.add_notes(map).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                DataManager.instance.hideProgressMessage()
                try {
                    val responseString = response.body()!!.string()
                    val jsonObject = JSONObject(responseString)
                    val message = jsonObject.getString("message")
                    val status = jsonObject.getString("status")
                    if (response.body() != null && status == "1") {
                        //     binding.root.successSnack(res.message)
                        binding.root.successSnack(message)
                        getmanagerlist()
                    } else {
                        binding.root.errorSnack(message)

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                    Timber.tag("Exception").e("Exception = %s", e.printStackTrace())
                    Timber.tag("Exception").e("Exception = %s", e.localizedMessage)
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                binding.root.errorSnack(t.message.toString())
            }

        })
    }

    override fun onItemClick(model: NotesDto.Result) {

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.attributes?.windowAnimations =
            android.R.style.Widget_Material_ListPopupWindow
        dialog.setContentView(R.layout.delete_child)
        val lp = WindowManager.LayoutParams()
        val window: Window = dialog.window!!
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp

        val yes_btn: TextView = dialog.findViewById(R.id.btn_yes)
        val no_btn: TextView = dialog.findViewById(R.id.btn_no)
        no_btn.setOnClickListener {
            dialog.dismiss()
        }
        yes_btn.setOnClickListener {
            delete_child_api(model.id.toString())
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun delete_child_api(toString: String) {
        DataManager.instance.showProgressMessage(
            requireActivity(),
            getString(R.string.getting_user)
        )
        val userid = sharedPref.getStringValue(Constant.USER_ID).toString()
        val auth_token = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
        val map = HashMap<String, String>()
        map["token"] = auth_token
        map["user_id"] = userid
        map["nodes_id"] = toString
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        var apiInterface: ProviderInterface =
            ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        apiInterface.delete_notes(map).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                DataManager.instance.hideProgressMessage()
                try {
                    val responseString = response.body()!!.string()
                    val jsonObject = JSONObject(responseString)
                    val message = jsonObject.getString("message")
                    val status = jsonObject.getString("status")
                    if (response.body() != null && status == "1") {
                        //     binding.root.successSnack(res.message)
                        binding.root.successSnack(message)
                        getmanagerlist()
                    } else {
                        binding.root.errorSnack(message)

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                    Timber.tag("Exception").e("Exception = %s", e.printStackTrace())
                    Timber.tag("Exception").e("Exception = %s", e.localizedMessage)
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                binding.root.errorSnack(t.message.toString())
            }

        })
    }
}