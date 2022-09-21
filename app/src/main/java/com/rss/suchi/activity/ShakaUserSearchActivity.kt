package com.rss.suchi.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.cbi_solar.helper.ApiContants
import com.cbi_solar.helper.ApiContants.PREF_getExcel
import com.cbi_solar.helper.MyApplication
import com.cbi_solar.helper.Utility
import com.rss.suchi.R
import com.rss.suchi.adapter.ShakaUserAdapter
import com.rss.suchi.adapter.ShakaUserSearchAdapter
import com.rss.suchi.databinding.ActivityShakaLisBinding
import com.rss.suchi.databinding.ActivityShakaUserSearchBinding
import com.rss.suchi.helper.ApiInterface
import com.rss.suchi.helper.DownloadTask
import com.rss.suchi.helper.RestClient
import com.rss.suchi.helper.VerticalSpacingItemDecorator
import com.rss.suchi.model.ShakaUser
import com.rss.suchi.model.ShakaUserListSearchModel
import com.rss.suchi.model.ShakaUserSearch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShakaUserSearchActivity : AppCompatActivity() {

    private var adapter: ShakaUserSearchAdapter? = null
    private lateinit var context: Activity
    private var TAG = "@@ShakaLisActivity"
    private var progressDialog: SweetAlertDialog? = null
    private lateinit var binding: ActivityShakaUserSearchBinding
    private var shakaUserList: ArrayList<ShakaUserSearch> = ArrayList()
    private var ss_id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        setContentView(R.layout.activity_shaka_user_search)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shaka_user_search)
        context = this@ShakaUserSearchActivity

        progressDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progressDialog!!.progressHelper.barColor = R.color.theme_color
        progressDialog!!.titleText = "Loading ..."
        progressDialog!!.setCancelable(false)
        binding.toolbar.imgDownload.visibility = View.GONE

        initShakaListRecyclerView()

        setData()
        clickListener()
    }

    fun EditText.onDone(callback: () -> Unit) {
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                callback.invoke()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun clickListener() {
        binding.toolbar.imgBack.setOnClickListener { onBackPressed() }
        binding.toolbar.imgDownload.setOnClickListener {
            downloadFile()
        }
        binding.imgSearch.setOnClickListener {
            if (binding.searchView.text.toString().trim().isNotEmpty())
                getFormList(binding.searchView.text.toString().trim())
        }

        binding.searchView.onDone {
            if (binding.searchView.text.toString().trim().isNotEmpty())
                getFormList(binding.searchView.text.toString().trim())
        }

    }
    private fun downloadFile(){
        val mProgressDialog: ProgressDialog = ProgressDialog(this@ShakaUserSearchActivity)
        mProgressDialog.setMessage("कृपया प्रतीक्षा करें")
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        mProgressDialog.setCancelable(false)

        val downloadTask = DownloadTask(this@ShakaUserSearchActivity, mProgressDialog)
        downloadTask.execute("$PREF_getExcel?shakha_id="+ss_id.toString().trim())

        mProgressDialog.setOnCancelListener {
            downloadTask.cancel(true) //cancel the task
        }
    }

    private fun initShakaListRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val itemDecorator = VerticalSpacingItemDecorator(20)
        binding.recyclerView.addItemDecoration(itemDecorator)
        adapter = ShakaUserSearchAdapter(initGlide()!!,this@ShakaUserSearchActivity)
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

        binding.toolbar.txtTitle.text = "स्वयंसेवक खोजें"
        binding.searchView.setHint("स्वयंसेवक के नाम से खोजे")

            ss_id = if (intent != null && intent.getBooleanExtra("isFormUpdate", false)) {
                intent.getIntExtra("ss_id", 0)
            } else MyApplication.ReadIntPreferences(ApiContants.PREF_USER_SHAKA)!!

    }

    private fun shakhaUserfilter(text: kotlin.String?) {
        val temp: kotlin.collections.ArrayList<ShakaUserSearch> = ArrayList()
        for (d: ShakaUserSearch in shakaUserList) {
            if (d.name.toString().trim().lowercase().contains(text?.toString()!!.trim()?.lowercase())) {
                temp.add(d)
            }
        }
        adapter!!.updateList(temp)
    }

    private fun getFormList(value:String) {
        progressDialog!!.show()
        shakaUserList.clear()

        val apiService: ApiInterface =
            RestClient().getClient(context)!!.create(ApiInterface::class.java)
        val call: Call<ShakaUserListSearchModel> =
            apiService.viewshakhauserBySearch(value)
        call.enqueue(object : Callback<ShakaUserListSearchModel?> {
            override fun onResponse(
                call: Call<ShakaUserListSearchModel?>,
                response: Response<ShakaUserListSearchModel?>
            ) {
                Log.e(TAG, "onResponse:1 " + response.body().toString())
                try {
                    if (response.isSuccessful && response.body() != null && response.body()?.data != null) {

                        for (i in 0 until response.body()?.data?.size!!) {
                            response.body()?.data?.get(i)?.let { shakaUserList.add(it) }!!
                        }
                        if (response.body() == null) {
                            adapter!!.setData(ArrayList<ShakaUserSearch>())
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

            override fun onFailure(call: Call<ShakaUserListSearchModel?>, t: Throwable) {
                adapter?.setData(java.util.ArrayList<ShakaUserSearch>())
                if (Utility.isNetworkAvailable(context)) {
                    Log.e(TAG, "onFailure: " + t.message)
                    Utility.showDialog(
                        context,
                        SweetAlertDialog.WARNING_TYPE,
                        "कोई परिणाम नहीं मिलता! \nपुनः प्रयास करें"
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
    var isUserDeleted = false
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK) {
//            getFormList(ss_id)
//
//            if (data != null && data?.getBooleanExtra(ApiContants.isDeleted,false) != null)
//                isUserDeleted = data?.getBooleanExtra(ApiContants.isDeleted,false) == true
//        }
//    }

    override fun onBackPressed() {

        if (isUserDeleted) {
            val intent = intent
            setResult(RESULT_OK, intent)
        }

        finish()
    }
}