package com.rss.suchi.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.cbi_solar.helper.ApiContants
import com.cbi_solar.helper.MyApplication
import com.cbi_solar.helper.Utility
import com.rss.suchi.R
import com.rss.suchi.adapter.ShakaUserAdapter
import com.rss.suchi.databinding.ActivityShakaLisBinding
import com.rss.suchi.helper.ApiInterface
import com.rss.suchi.helper.RestClient
import com.rss.suchi.helper.VerticalSpacingItemDecorator
import com.rss.suchi.model.ShakaUser
import com.rss.suchi.model.ShakaUserListModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShakaUserListActivity : AppCompatActivity() {

    private var adapter: ShakaUserAdapter? = null
    private lateinit var context: Activity
    private var TAG = "@@ShakaLisActivity"
    private var progressDialog: SweetAlertDialog? = null
    private lateinit var binding: ActivityShakaLisBinding
    private var shakaUserList: ArrayList<ShakaUser> = ArrayList()
    private var ss_id = 0
    val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        setContentView(R.layout.activity_shaka_lis)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shaka_lis)
        context = this@ShakaUserListActivity

        progressDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progressDialog!!.progressHelper.barColor = R.color.theme_color
        progressDialog!!.titleText = "Loading ..."
        progressDialog!!.setCancelable(false)

        initShakaListRecyclerView()

        setData()
        clickListener()
    }

    private fun clickListener() {
        binding.toolbar.imgBack.setOnClickListener { finish() }

        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                shakhaUserfilter(s.toString())
            }
        })

    }

    private fun initShakaListRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val itemDecorator = VerticalSpacingItemDecorator(20)
        binding.recyclerView.addItemDecoration(itemDecorator)
        adapter = ShakaUserAdapter(initGlide()!!,this@ShakaUserListActivity)
        binding.recyclerView.adapter = adapter
    }

    private fun initGlide(): RequestManager? {
        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.circul_white)
            .error(cn.pedant.SweetAlert.R.drawable.red_button_background)
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }

    private fun setData() {

        binding.toolbar.txtTitle.text = getString(R.string.swayamsevak_list)
        binding.searchView.setHint("स्वयंसेवक के नाम से खोजे")

            ss_id = if (intent != null && intent.getBooleanExtra("isFormUpdate", false)) {
                intent.getIntExtra("ss_id", 0)
            } else MyApplication.ReadIntPreferences(ApiContants.PREF_USER_SHAKA)!!
            getFormList(ss_id)
    }

    private fun shakhaUserfilter(text: kotlin.String?) {
        val temp: kotlin.collections.ArrayList<ShakaUser> = ArrayList()
        for (d: ShakaUser in shakaUserList) {
            if (d.name.toString().trim().lowercase().contains(text?.toString()!!.trim()?.lowercase())) {
                temp.add(d)
            }
        }
        adapter!!.updateList(temp)
    }

    private fun getFormList(id:Int) {
        progressDialog!!.show()
        shakaUserList.clear()

        val apiService: ApiInterface =
            RestClient().getClient(context)!!.create(ApiInterface::class.java)
        val call: Call<ShakaUserListModel> =
            apiService.viewshakhauser(id)
        call.enqueue(object : Callback<ShakaUserListModel?> {
            override fun onResponse(
                call: Call<ShakaUserListModel?>,
                response: Response<ShakaUserListModel?>
            ) {
                Log.e(TAG, "onResponse:1 " + response.body().toString())
                try {
                    if (response.isSuccessful && response.body() != null && response.body()?.data != null) {

                        for (i in 0 until response.body()?.data?.size!!) {
                            response.body()?.data?.get(i)?.let { shakaUserList.add(it) }!!
                        }
                        if (response.body() == null) {
                            adapter!!.setData(ArrayList<ShakaUser>())
                            Utility.showDialog(
                                context,
                                SweetAlertDialog.WARNING_TYPE,
                                "सूची खाली है"
                            )
                        } else {
                            adapter!!.setData(shakaUserList)
                        }
                    } else {
                        Utility.showDialog(
                            context,
                            SweetAlertDialog.WARNING_TYPE,
                            "सूची खाली है"
                        )
                    }

                } catch (e: Exception) {
                    Log.d(TAG, "onResponse: " + e.message.toString())
                    showErrorDialog()

                    Toast.makeText(
                        context,
                        " " + resources.getString(R.string.error),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                progressDialog!!.dismiss()
            }

            override fun onFailure(call: Call<ShakaUserListModel?>, t: Throwable) {
                adapter?.setData(java.util.ArrayList<ShakaUser>())
                if (Utility.isNetworkAvailable(context)) {
                    Log.e(TAG, "onFailure: " + t.message)
                    Utility.showDialog(
                        context,
                        SweetAlertDialog.WARNING_TYPE,
                        "सूची खाली है! कृपया बाद में कोशिश करें।"
                    )
                }
                progressDialog!!.dismiss()
            }
        })
    }
    private fun showErrorDialog() {
        Utility.showDialog(
            context,
            SweetAlertDialog.WARNING_TYPE,
            resources.getString(R.string.error)
        )
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            getFormList(ss_id)
        }
    }

}