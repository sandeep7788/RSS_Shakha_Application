package com.rss.suchi.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
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
import com.rss.suchi.adapter.NkkListAdapter
import com.rss.suchi.databinding.ActivityNkkListBinding
import com.rss.suchi.helper.ApiInterface
import com.rss.suchi.helper.DownloadTask
import com.rss.suchi.helper.RestClient
import com.rss.suchi.helper.VerticalSpacingItemDecorator
import com.rss.suchi.model.NkkListModel
import com.rss.suchi.model.NkkMainModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NkkListActivity : AppCompatActivity() {

    private var adapter: NkkListAdapter? = null
    private lateinit var context: Activity
    private var TAG = "@@NkkListActivity"
    private var progressDialog: SweetAlertDialog? = null
    private lateinit var binding: ActivityNkkListBinding
    private val list: ArrayList<NkkListModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        setContentView(R.layout.activity_shaka_lis)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nkk_list)
        context = this@NkkListActivity

        progressDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progressDialog!!.progressHelper.barColor = R.color.theme_color
        progressDialog!!.titleText = "Loading ..."
        progressDialog!!.setCancelable(false)
        binding.toolbar.imgDownload.visibility = View.VISIBLE
        initShakaListRecyclerView()

        setData()
        clickListener()


        if (MyApplication.ReadStringPreferences(ApiContants.PREF_role).equals("NKK", true)) {
            binding.toolbar.imgAdd.visibility = View.VISIBLE
        } else binding.toolbar.imgAdd.visibility = View.GONE

        binding.toolbar.imgAdd.setOnClickListener {
            val mainIntent = Intent(context, NKKForm::class.java)
            mainIntent.putExtra("isFormUpdate", false)
//            mainIntent.putExtra("ss_id",mOptionList.get(position).nkkId.toInt())


            val REQUEST_CODE = 1;
            context!!.startActivityForResult(mainIntent, REQUEST_CODE)
        }
    }

    private fun downloadFile() {
        val mProgressDialog: ProgressDialog = ProgressDialog(this@NkkListActivity)
        mProgressDialog.setMessage("कृपया प्रतीक्षा करें")
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        mProgressDialog.setCancelable(false)

        val downloadTask = DownloadTask(this@NkkListActivity, mProgressDialog)
        var ngId = if (MyApplication.ReadIntPreferences(ApiContants.PREF_nager).toString().trim().isEmpty()) {
            "0"
        } else MyApplication.ReadIntPreferences(ApiContants.PREF_nager).toString().trim()

        var docYrl = "${ApiContants.PREF_getNkkDoc}?nager_id=" + ngId;
//        var docYrl = "${ApiContants.PREF_getExcel}?shakha_id=0"
        Log.e(TAG, "downloadFile: "+docYrl)
        downloadTask.execute(docYrl)

        mProgressDialog.setOnCancelListener {
            downloadTask.cancel(true) //cancel the task
        }
    }

    private fun clickListener() {

        binding.toolbar.imgDownload.visibility = View.VISIBLE
        binding.toolbar.imgBack.setOnClickListener { onBackPressed() }
        binding.toolbar.imgDownload.setOnClickListener {
            downloadFile()
        }

        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                shakhaListfilter(s.toString())
            }
        })

    }

    private fun initShakaListRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val itemDecorator = VerticalSpacingItemDecorator(20)
        binding.recyclerView.addItemDecoration(itemDecorator)
        adapter = NkkListAdapter(initGlide()!!, this@NkkListActivity)
        binding.recyclerView.adapter = adapter
    }

    private fun initGlide(): RequestManager? {
        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.circul_white)
            .error(R.drawable.red_button_background)
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }

    private fun setData() {

        binding.toolbar.txtTitle.text = getString(R.string.nkk)

        if (MyApplication.readBoolPreferences(ApiContants.isMskUser)) {
            finish()
        } else {
            getNkkList()
        }
    }

    private fun shakhaListfilter(text: kotlin.String?) {
        val temp: kotlin.collections.ArrayList<NkkListModel> = ArrayList()
        for (d: NkkListModel in list) {
            if (d.nkkName.toString().trim().lowercase()
                    .contains(text?.toString()!!.trim().lowercase())
            ) {
                temp.add(d)
            }
        }
        adapter!!.updateList(temp)
    }

    private fun getNkkList() {
        progressDialog!!.show()
        list.clear()

//        if (intent.extras == null) {
//            return
//        }
        val apiService: ApiInterface =
            RestClient().getClient(context)!!.create(ApiInterface::class.java)
        val call: Call<NkkMainModel> =
            apiService.getNkk(1)

        call.enqueue(object : Callback<NkkMainModel?> {
            override fun onResponse(
                call: Call<NkkMainModel?>,
                response: Response<NkkMainModel?>
            ) {
                Log.e(TAG, "onResponse:1 " + response.body().toString())
                progressDialog!!.dismiss()
                try {
                    if (response.isSuccessful && response.body() != null) {
//
//                        var jsonResponse = JSONObject(response.body().toString())
//                        val cast: JSONArray = jsonResponse.getJSONArray("data")
//                        for (i in 0 until cast.length()) {
//
//                        }
                        list.addAll(response.body()!!.data)
                        if (response.body() == null || list.isEmpty()) {
                            adapter?.setData(ArrayList())
                            Utility.showDialog(
                                context,
                                SweetAlertDialog.WARNING_TYPE,
                                "सूची खाली है!"
                            )
                        } else {
                            adapter!!.setData(list)
                            adapter?.notifyDataSetChanged()
                        }
                    } else {
                        showErrorDialog()
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

            override fun onFailure(call: Call<NkkMainModel?>, t: Throwable) {
                adapter?.setData(java.util.ArrayList<NkkListModel>())
                if (Utility.isNetworkAvailable(context)) {
                    Toast.makeText(
                        context,
                        " " + resources.getString(R.string.error),
                        Toast.LENGTH_LONG
                    )
                        .show()
                    showErrorDialog()
                }
                progressDialog!!.dismiss()
            }
        })
    }

    private fun downloadExcel() {
        progressDialog!!.show()

        val apiService: ApiInterface =
            RestClient().getClient(context)!!.create(ApiInterface::class.java)
        val call: Call<ResponseBody> =
            apiService.getExcel(0)
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                Log.e(TAG, "onResponse:1 " + response.body().toString())
                try {
                    if (response.isSuccessful && response.body() != null) {
                        Utility.showSnackBar(this@NkkListActivity, "file downloaded.")
                    } else {
                        showErrorDialog()
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

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                adapter?.setData(java.util.ArrayList<NkkListModel>())
                if (Utility.isNetworkAvailable(context)) {
                    Toast.makeText(
                        context,
                        " " + resources.getString(R.string.error),
                        Toast.LENGTH_LONG
                    )
                        .show()
                    showErrorDialog()
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

    val REQUEST_CODE = 1
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            getNkkList()
        }
    }

    var IS_FROM_ADVANCE_DASHBOARD = 1111
    override fun onBackPressed() {
        if (!MyApplication.readBoolPreferences(ApiContants.isMskUser)) {
            val intent = intent
//            intent.putExtra(IS_FROM_ADVANCE_DASHBOARD , true)
            setResult(RESULT_OK, intent)
            finish()
        } else
            super.onBackPressed()

    }
}