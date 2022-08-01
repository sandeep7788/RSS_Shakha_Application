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
import com.google.gson.JsonObject
import com.rss.suchi.R
import com.rss.suchi.adapter.ShakhaListAdapter
import com.rss.suchi.databinding.ActivityShakaLisBinding
import com.rss.suchi.helper.ApiInterface
import com.rss.suchi.helper.DownloadTask
import com.rss.suchi.helper.RestClient
import com.rss.suchi.helper.VerticalSpacingItemDecorator
import com.rss.suchi.model.DashBoardShakaList
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ShakaLisActivity : AppCompatActivity() {

    private var adapter: ShakhaListAdapter? = null
    private lateinit var context: Activity
    private var TAG = "@@ShakaLisActivity"
    private var progressDialog: SweetAlertDialog? = null
    private lateinit var binding: ActivityShakaLisBinding
    val allShakhaList: ArrayList<DashBoardShakaList> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        setContentView(R.layout.activity_shaka_lis)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shaka_lis)
        context = this@ShakaLisActivity

        progressDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progressDialog!!.progressHelper.barColor = R.color.theme_color
        progressDialog!!.titleText = "Loading ..."
        progressDialog!!.setCancelable(false)
        binding.toolbar.imgDownload.visibility = View.VISIBLE
        initShakaListRecyclerView()

        setData()
        clickListener()
    }


    private fun downloadFile() {
        val mProgressDialog: ProgressDialog = ProgressDialog(this@ShakaLisActivity)
        mProgressDialog.setMessage("A message")
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        mProgressDialog.setCancelable(true)

        val downloadTask = DownloadTask(this@ShakaLisActivity, mProgressDialog)
        downloadTask.execute("${ApiContants.PREF_getExcel}?shakha_id=0")

        mProgressDialog.setOnCancelListener {
            downloadTask.cancel(true) //cancel the task
        }
    }

    private fun clickListener() {

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
        adapter = ShakhaListAdapter(initGlide()!!)
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

        binding.toolbar.txtTitle.text = getString(R.string.shakha_list)

        if (MyApplication.readBoolPreferences(ApiContants.isMskUser)) {
            finish()
        } else {
            getDashboardList()
        }
    }

    private fun shakhaListfilter(text: kotlin.String?) {
        val temp: kotlin.collections.ArrayList<DashBoardShakaList> = ArrayList()
        for (d: DashBoardShakaList in allShakhaList) {
            if (d.shakha_name.toString().trim().lowercase()
                    .contains(text?.toString()!!.trim().lowercase())
            ) {
                temp.add(d)
            }
        }
        adapter!!.updateList(temp)
    }

    private fun getDashboardList() {
        progressDialog!!.show()
        allShakhaList.clear()

        if (intent.extras == null) {
            return
        }
        val apiService: ApiInterface =
            RestClient().getClient(context)!!.create(ApiInterface::class.java)
        val call: Call<JsonObject> =
            apiService.dashboardShakaList(
                2,
                MyApplication.ReadIntPreferences(ApiContants.PREF_USER_ID),
                intent.getStringExtra("flagType"),
                intent.getIntExtra("flag_id", 0).toInt()
            )

        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(
                call: Call<JsonObject?>,
                response: Response<JsonObject?>
            ) {
                Log.e(TAG, "onResponse:1 " + response.body().toString())
                try {
                    if (response.isSuccessful && response.body() != null) {

                        var jsonResponse = JSONObject(response.body().toString())
                        val cast: JSONArray = jsonResponse.getJSONArray("data")
                        for (i in 0 until cast.length()) {
                            val data = cast.getJSONObject(i)
                            allShakhaList.add(
                                DashBoardShakaList(
                                    data.getLong("shakhaid"),
                                    data.getString("shakha_name"),
                                    data.getLong("total")
                                )
                            )
                        }
                        if (response.body() == null || allShakhaList.isEmpty()) {
                            adapter?.setData(ArrayList<DashBoardShakaList>())
                            Utility.showDialog(
                                context,
                                SweetAlertDialog.WARNING_TYPE,
                                "सूची खाली है!"
                            )
                        } else {
                            adapter!!.setData(allShakhaList)
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

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                adapter?.setData(java.util.ArrayList<DashBoardShakaList>())
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
                        Utility.showSnackBar(this@ShakaLisActivity, "file downloaded.")
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
                adapter?.setData(java.util.ArrayList<DashBoardShakaList>())
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
            getDashboardList()
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