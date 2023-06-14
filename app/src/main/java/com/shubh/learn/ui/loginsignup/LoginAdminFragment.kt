package com.shubh.learn.ui.loginsignup

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.shubh.learn.R
import com.shubh.learn.databinding.FragmentLoginAdminBinding
import com.shubh.learn.dto.LoginDto
import com.shubh.learn.retrofit.ApiClient
import com.shubh.learn.retrofit.ProviderInterface
import com.shubh.learn.utills.DataManager
import com.shubh.learn.utills.SharedPref
import com.shubh.learn.utills.errorSnack
import com.shubh.learn.utills.successSnack
import com.vilborgtower.user.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class LoginAdminFragment : Fragment() {
    private lateinit var binding: FragmentLoginAdminBinding
    lateinit var sharedPref: SharedPref
    lateinit var apiInterface: ProviderInterface

    /*  private lateinit var mGoogleSignInClient:GoogleSignInClient
      private val RC_SIGN_IN = 7
      protected val activityLauncher: BetterActivityResult<Intent, ActivityResult> =
          BetterActivityResult.registerActivityForResult(this)*/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login_admin,
            container, false
        )
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        /*  val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
              .requestEmail()
              .build()*/
        /* mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
         binding.google.setOnClickListener { v ->
             signOut()
             googleSignIn()
         }*/

        //add_error_msg("1213")
        binding.btnSubmit.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val pass = binding.edtPass.text.toString()
            if (email == "") binding.edtEmail.errorSnack(getString(R.string.empty_fields))
            else if (pass == "") binding.edtPass.errorSnack(getString(R.string.empty_fields))
//            else if (Utils.hasInternetConnection(requireActivity())) loginApi(email, pass)
            else loginApi(email, pass)
            //binding.root.errorSnack(getString(R.string.no_internet_connection))
        }
        binding.signupLay.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_loginAdminFragment_to_signupAdminFragment)
        }
        binding.forget.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_loginAdminFragment_to_forgetPasswordFragment)
        }

        return binding.root
    }
//
//    private fun googleSignIn() {
//        val signInIntent = mGoogleSignInClient.signInIntent
//        activityLauncher.launch(signInIntent) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                val data: Intent? = result.data
//                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//                handleSignInResult(task)
//            }
//        }
//    }

    /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
         if (requestCode == RC_SIGN_IN) {
             val task = GoogleSignIn.getSignedInAccountFromIntent(data)
             handleSignInResult(task)
         } else if (requestCode == 64206) {
             //callbackManager.onActivityResult(requestCode, resultCode, data);
         } else {
 //            AuthManager.getInstance().onActivityResult(requestCode, resultCode, data);
         }
     }*/
/*
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(java.lang.Exception::class.java)
            Log.e("TAG", "display GoogleSignInAccount:getDisplayName()) " + account.displayName)
            Log.e("TAG", "display GoogleSignInAccount:getEmail()); " + account.email)
            Log.e("TAG", "display GoogleSignInAccount:getPhotoUrl()); " + account.photoUrl)
            Log.e("TAG", "display GoogleSignInAccount:getFamilyName()); " + account.familyName)
            Log.e("TAG", "display GoogleSignInAccount:getGivenName()); " + account.givenName)
            Log.e("TAG", "display GoogleSignInAccount:getId()); " + account.id)
            val personName = account.displayName
            val email = account.email
            val socialId = account.id
           socialLoginApi(personName, personName, email, socialId, "Google")
        } catch (e: java.lang.Exception) {
            Log.e("TAG", "signInResult:failed code=" + e.message) //StatusCode());
        }
    }

    private fun socialLoginApi(personName: String?, personName1: String?,
                               email: String?, socialId: String?, s: String)
        {
            DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.getting_user))
            val map = HashMap<String, String>()
            map["email"] = email.toString()
            map["register_id"] = sharedPref.getStringValue(Constant.FIREBASETOKEN).toString()
            map["type"] = "Administrator"
            map["first_name"] = personName.toString()
            map["social_id"] = socialId.toString()
            Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
            apiInterface.social_login(map).enqueue(object : Callback<LoginDto?> {
                override fun onResponse(
                    call: Call<LoginDto?>,
                    response: Response<LoginDto?>
                ) {
                    DataManager.instance.hideProgressMessage()
                    try {
                        val res: LoginDto = response.body()!!

                        if (response.body() != null && response.body()!!.status == "1") {
                            binding.root.successSnack(res.message)
                            sharedPref.setStringValue(Constant.USER_ID, res.result.id)
                            sharedPref.setStringValue(Constant.AUTH_TOKEN, res.result.token)
                            sharedPref.setStringValue(Constant.USER_TYPE, "Administrator")
                            sharedPref.setBooleanValue(Constant.IS_LOGIN, true)
                            Navigation.findNavController(binding.root)
                                .navigate(R.id.action_loginAdminFragment_to_compenyHomeFragment)
                        } else {
                            binding.root.errorSnack(res.message)
                        }


                    } catch (e: Exception) {
                        DataManager.instance.hideProgressMessage()
                        Toast.makeText(
                            context,
                            "Exception = " + e.message,
                            Toast.LENGTH_SHORT
                        ).show()
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


    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(requireActivity(),
                { task: Task<Void?>? -> })
    }*/


    /*   private fun add_error_msg( pass: String) {
           val map = HashMap<String, String>()
           map["message"] = pass
           Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
           apiInterface.add_error_msg(map).enqueue(object : Callback<ResponseBody?> {
               override fun onResponse(
                   call: Call<ResponseBody?>,
                   response: Response<ResponseBody?>
               ) {

               }
               override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                   Timber.tag("ContentValues.TAG").e("onFailure: %s", t.printStackTrace())
                   Timber.tag("ContentValues.TAG").e("onFailure: %s", t.stackTrace)
                   Timber.tag("ContentValues.TAG").e("onFailure: %s", t.suppressed)
                   Timber.tag("ContentValues.TAG").e("onFailure: %s", t.localizedMessage)
                   Timber.tag("ContentValues.TAG").e("onFailure: %s", t.cause.toString())
                   Timber.tag("ContentValues.TAG").e("onFailure: %s", t.message.toString())



               }

           })

       }*/
    private fun loginApi(email: String, pass: String) {
        DataManager.instance.showProgressMessage(
            requireActivity(),
            getString(R.string.getting_user)
        )
        val map = HashMap<String, String>()
        map["email"] = email
        map["password"] = pass
        // map["register_id"] = "123"
        map["register_id"] = sharedPref.getStringValue(Constant.FIREBASETOKEN).toString()
        map["type"] = "Administrator"
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
                        binding.root.successSnack(res.message)
                        sharedPref.setStringValue(Constant.USER_ID, res.result.id)
                        sharedPref.setStringValue(Constant.AUTH_TOKEN, res.result.token)
                        sharedPref.setStringValue(Constant.USER_TYPE, "Administrator")
                        sharedPref.setBooleanValue(Constant.IS_LOGIN, true)
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.action_loginAdminFragment_to_compenyHomeFragment)

                    } else {
                        binding.root.errorSnack(res.message)
                        //   add_error_msg("res.message"+res.message)
                    }


                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    //  add_error_msg(" e.message"+ e.message)

                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<LoginDto?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                binding.root.errorSnack(t.message.toString())
                //  add_error_msg(" e.message"+t.message.toString()+"  cause"+t.cause.toString()+"  t.localizedMessage"+t.localizedMessage.toString())

            }

        })

    }

}




