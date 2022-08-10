package com.rss.suchi.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.cbi_solar.helper.ApiContants
import com.cbi_solar.helper.MyApplication
import com.cbi_solar.helper.RetrofitManager
import com.cbi_solar.helper.Utility
import com.google.gson.JsonObject
import com.rss.suchi.R
import com.rss.suchi.databinding.ActivitySignInBinding
import com.rss.suchi.MainActivity
import com.rss.suchi.helper.ApiInterface
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivitySignInBinding
    private var progressDialog: SweetAlertDialog? = null
    private var TAG = "@@SignInActivity"
    private var status_sakha = false
    private lateinit var context:Activity

    private fun clickListener() {

        binding.btnSignIn.setOnClickListener {
            binding.l1.setBackgroundResource(R.drawable.edit_txtbg)
            binding.l2.setBackgroundResource(R.drawable.edit_txtbg)
            when {
                binding.txtUsername.text.isEmpty() -> {
                    Utility.showSnackBar(this, "कृपया उपयोगकर्ता नाम दर्ज करें")
                    binding.txtUsername.setBackgroundResource(R.drawable.edit_txt_error)
                    binding.l1.setBackgroundResource(R.drawable.edit_txt_error)
                }
//                !status_sakha -> {
//                    Utility.showSnackBar(this, "कृपया शाखा चुनें")
//                    binding.layoutSakha.setBackgroundResource(R.drawable.edit_txt_error)
//                }
                binding.txtPassword.text.isEmpty() -> {
                    Utility.showSnackBar(this, "कृपया उपयोगकर्ता पासवर्ड दर्ज करें")
                    binding.l2.setBackgroundResource(R.drawable.edit_txt_error)
                }
                else -> {
                    signIn()
                }
            }
        }

        binding.btnSignUp.setOnClickListener {
            Utility.showSnackBar(this, "बाद में कोशिश करें।")
        }

        binding.btnPrivacyPolicy.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(ApiContants.PREF_privacypolicy)
                )
            )
        }
    }

    private fun signIn() {
        progressDialog!!.show()
        val apiInterface: ApiInterface? =
            RetrofitManager().instanceNew(context)?.create(ApiInterface::class.java)

        apiInterface!!.signIn(
            binding.txtUsername.text.toString().trim(),
            binding.txtPassword.text.toString().trim(),
        ).enqueue(object :
            Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                if (Utility.isNetworkAvailable(context)) {
                    Toast.makeText(
                        context,
                        " " + resources.getString(R.string.error),
                        Toast.LENGTH_LONG
                    )
                        .show()
                    Log.e(TAG, "onFailure: " + t.message)
                }
                progressDialog!!.dismiss()
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                progressDialog!!.dismiss()
                var msg: String? = null;
                try {
                    if (response.isSuccessful) {

                        Log.d(TAG, "onResponse: " + response.body().toString())
                        val jsonObject =
                            JSONObject(response.body().toString()).getJSONObject("data")
                        try {
                            MyApplication.writeStringPreference(
                                ApiContants.PREF_STATUS,
                                JSONObject(response.body().toString()).getInt("status").toString()
                            )
                        }catch (e:Exception) {

                        }
                        if (jsonObject.has("response")) {
                            msg = jsonObject.getString("response")
                            Toast.makeText(
                                context,
                                " " + msg,
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        if (jsonObject.has("username")) {

                            var data = jsonObject

                            MyApplication.writeStringPreference(
                                ApiContants.PREF_USER_NAME,
                                data.getString("username")
                            )
                            MyApplication.writeIntPreference(
                                ApiContants.PREF_USER_ID,
                                data.getInt("userid")
                            )
                            MyApplication.writeIntPreference(
                                ApiContants.PREF_USER_SHAKA,
                                data.getInt("shaka")
                            )
                            MyApplication.writeStringPreference(ApiContants.login, "true")
                            MyApplication.writeBoolPreference(ApiContants.isMskUser, data.getInt("shaka") > 0)



                            startActivity(Intent(context, MainActivity::class.java))
                            finish()
                        } else {
                            Utility.showDialog(
                                context,
                                SweetAlertDialog.WARNING_TYPE,
                                "कोई उपयोगकर्ता नहीं मिला"
                            )
                        }
                    } else {
                        Utility.showDialog(
                            context,
                            SweetAlertDialog.WARNING_TYPE,
                            resources.getString(R.string.error)
                        )
                    }

                } catch (e: Exception) {
                    Utility.showDialog(
                        context,
                        SweetAlertDialog.WARNING_TYPE,
                        resources.getString(R.string.error)
                    )

                    Toast.makeText(
                        context,
                        " " + resources.getString(R.string.error),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        context = this@SignInActivity
/*
        val widthDp = resources.displayMetrics.run { widthPixels / density }
        val heightDp = resources.displayMetrics.run { heightPixels / density }

        var width:Int= widthDp.roundToInt()
        binding.l1.setMargins(top = (widthDp+100).roundToInt())*/

/*        val params: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(this.binding.l1.getWidth(), this.binding.l1.getHeight())
        params.setMargins(left, top, right, bottom)
        this.binding.l1.setLayoutParams(params)*/

        progressDialog = SweetAlertDialog(this@SignInActivity, SweetAlertDialog.PROGRESS_TYPE)
        progressDialog!!.progressHelper.barColor = R.color.theme_color
        progressDialog!!.titleText = "Loading ..."
        progressDialog!!.setCancelable(false)

        clickListener()
//        setSakhaList(binding.spinnerSakha)

    }

    fun setSakhaList(spinner: Spinner) {
        val item = resources?.getStringArray(R.array.sakha_array)

        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                R.layout.simple_spinner_item, item!!
            )
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    binding.spinnerHint.visibility = View.GONE
                    /*   Toast.makeText(
                           this@SignUpActivity,"selected gender" +
                                   "" + item[position], Toast.LENGTH_SHORT
                       ).show()*/
                    status_sakha = true
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                    binding.spinnerHint.visibility = View.VISIBLE
                    status_sakha = false
                }
            }
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}