package com.rss.suchi

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.cbi_solar.helper.ApiContants
import com.cbi_solar.helper.MyApplication
import com.cbi_solar.helper.Utility
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonObject
import com.rss.suchi.activity.*
import com.rss.suchi.databinding.ActivityMainBinding
import com.rss.suchi.helper.ApiInterface
import com.rss.suchi.helper.RestClient
import com.rss.suchi.model.ShakaUserListModel
import org.json.JSONObject
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var context: Activity
    private var TAG = "@@MainActivity"
    private var progressDialog: SweetAlertDialog? = null
    private lateinit var binding: ActivityMainBinding
    private var exit = false
    private var REQUEST_CODE = 101

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.drawerLayout.bringToFront()
        context = this@MainActivity

        progressDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progressDialog!!.progressHelper.barColor = R.color.theme_color
        progressDialog!!.titleText = "Loading ..."
        progressDialog!!.setCancelable(false)

        setData()
        clickListener()
        checkAccess()
        checkForUpdate()
    }

    private fun checkForUpdate() {
        try {
            GetLatestVersion(this@MainActivity).execute()
        }catch (e:Exception) {

        }
    }

    private fun closeDrawerBar() {
        var mDrawerLayout: DrawerLayout = binding.drawerLayout

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers()
        }
    }

    private fun setData() {
//        binding.includeNavigation.txtNameAndId.text =
//            "" + MyApplication.ReadStringPreferences(ApiContants.PREF_USER_NAME)
        binding.txtName.text = "" + MyApplication.ReadStringPreferences(ApiContants.PREF_USER_NAME)
        binding.isMskUser = MyApplication.readBoolPreferences(ApiContants.isMskUser)
        binding.includeNavigation.isMskUser =
            MyApplication.readBoolPreferences(ApiContants.isMskUser)


        if (MyApplication.readBoolPreferences(ApiContants.isMskUser)) {
            getFormListSize(MyApplication.ReadIntPreferences(ApiContants.PREF_USER_SHAKA)!!)
        } else getDashboardData()

    }

    @SuppressLint("WrongConstant")
    private fun clickListener() {

        var mDrawerLayout: DrawerLayout = binding.drawerLayout
        binding.imgMenu.setOnClickListener {
            if (!mDrawerLayout.isDrawerOpen(Gravity.START)) mDrawerLayout.openDrawer(Gravity.START)
            else mDrawerLayout.closeDrawer(Gravity.END)
        }

        binding.includeNavigation.layoutSignOut.setOnClickListener {
            closeDrawerBar()
            AlertDialog.Builder(this)
                .setMessage("क्या आप द्वारा साइन आउट किया जाना सुनिश्चित है?")
                .setCancelable(false)
                .setPositiveButton(
                    getString(R.string.yes)
                ) { dialog, id -> MyApplication.logout(true) }
                .setNegativeButton(getString(R.string.no), null)
                .show()
        }

        binding.bottomNev.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    closeDrawerBar()
//                    setProductList()
//                    setBanner_list()
                }
                R.id.winners -> {
//                    startActivity(Intent(this@MainActivity, DiscountActivity::class.java))
                    Utility.showSnackBar(this, getString(R.string.please_try_later))
                }
//                R.id.store -> {
////                    startActivity(Intent(this@MainActivity, StoreListActivity::class.java))
//
//                }
//                R.id.profile -> {
////                    startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
//                    Utility.showSnackBar(this, getString(R.string.please_try_later))
//                }
            }
            true
        })

        binding.includeNavigation.layoutTrackStatus.setOnClickListener {
            closeDrawerBar()
//            startActivity(Intent(this@MainActivity, TrackStatusActivity::class.java))
        }

        binding.includeNavigation.layout1.setOnClickListener {
            closeDrawerBar()
            setData()
        }

        binding.includeNavigation.layoutChangePassword.setOnClickListener {
            closeDrawerBar()
//            startActivity(Intent(this@MainActivity, ChangePasswordsActivity::class.java))
        }
        binding.includeNavigation.layoutForm.setOnClickListener {
            closeDrawerBar()
            startActivityForResult(
                Intent(this@MainActivity, ServicesForm::class.java),
                REQUEST_CODE
            )
        }
        binding.includeNavigation.layoutAttendanceForm.setOnClickListener {
            closeDrawerBar()
            startActivity(Intent(this@MainActivity, AttendanceFormActivity::class.java))
        }

        binding.includeNavigation.layoutPrivacyPolicy.setOnClickListener {
            closeDrawerBar()
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(ApiContants.PREF_privacypolicy)
                )
            )
        }

        binding.imgNotifaction.setOnClickListener {
            closeDrawerBar()
            startActivity(Intent(this@MainActivity, NotificationListActivity::class.java))
        }

        binding.layoutShakhaList.setOnClickListener {
            closeDrawerBar()
            startActivityForResult(
                Intent(this@MainActivity, ShakaUserListActivity::class.java),
                REQUEST_CODE
            )
        }

        binding.layoutTotalShaka.setOnClickListener {
            startActivity(Intent(this@MainActivity, ShakaLisActivity::class.java))
        }
    }

    override fun onBackPressed() {
        closeDrawerBar()
        if (exit) {
            finish() // finish activity
        } else {
            Toast.makeText(
                this, " बाहर निकलने के लिए फिर से दबाएं।",
                Toast.LENGTH_SHORT
            ).show()
            exit = true
            Handler().postDelayed({ exit = false }, 3 * 1000)

        }
    }


    private fun getDashboardData() {
        progressDialog!!.show()

        val apiService: ApiInterface =
            RestClient().getClient(context)!!.create(ApiInterface::class.java)
        val call: Call<JsonObject> =
            apiService.dashboard(1)
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(
                call: Call<JsonObject?>,
                response: Response<JsonObject?>
            ) {
                Log.e(TAG, "onResponse:1 " + response.body().toString())
                try {
                    if (response.isSuccessful && response.body() != null) {

                        Log.d(TAG, "onResponse: " + response.body().toString())
                        val jsonObject =
                            JSONObject(response.body().toString()).getJSONObject("data")

                        if (jsonObject.has("total_shaka")) {
                            var totalSevak = jsonObject.getInt("toatl_swayam_sevak").toString()
                            binding.txtTotalShaka.text = jsonObject.getInt("total_shaka").toString()
                            binding.txtTotalSwayamSevak.text = totalSevak
                            binding.txtPresent.text = jsonObject.getInt("present").toString()
                        } else showErrorDialog()

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

    private fun checkAccess() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
            } else {
                requestPermissionForReadExternalStorage()
            }
        }

    }

    @Throws(java.lang.Exception::class)
    fun requestPermissionForReadExternalStorage() {
        try {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                101
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw e
        }
    }

    private fun getFormListSize(id: Int) {
        progressDialog!!.show()
        binding.txtTotalSwayamSevakTwo.setText("0")
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
                        binding.txtTotalSwayamSevakTwo.text = response.body()?.data?.size.toString()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            setData()
        }
    }

    inner class GetLatestVersion(var act: Activity) :
        AsyncTask<String?, Void?, String>() {
        var sCurrentVersion: String? = null
        var sLatestVersion: kotlin.String? = null

        override fun onPostExecute(s: String) {
            // Get current version

            sCurrentVersion = BuildConfig.VERSION_NAME
            if (sLatestVersion != null) {
                // Version convert to float
                val cVersion: Float = sCurrentVersion!!.toFloat()
                val lVersion: Float = sLatestVersion!!.toFloat()

                // Check condition(latest version is
                // greater than the current version)
                if (lVersion > cVersion) {
                    // Create update AlertDialog
                    updateAlertDialog()
                }
            }
        }

        override fun doInBackground(vararg p0: String?): String {
            try {
                sLatestVersion = Jsoup
                    .connect(
                        "https://play.google.com//store/apps/details?id="
                                + act.packageName
                    )
                    .timeout(30000)
                    .get()
                    .select(
                        ("div.hAyfc:nth-child(4)>" +
                                "span:nth-child(2) > div:nth-child(1)" +
                                "> span:nth-child(1)")
                    )
                    .first()
                    .ownText()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return sLatestVersion.toString()
        }
    }

    fun updateAlertDialog() {
        // Initialize AlertDialog
        val builder = AlertDialog.Builder(this)
        // Set title
        builder.setTitle(getString(R.string.app_name))
        // set message
        builder.setMessage("Update Available")
        // Set non cancelable
        builder.setCancelable(false)

        // On update
        builder.setPositiveButton(
            "Update"
        ) { dialogInterface, i -> // Open play store
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id$packageName")
                )
            )
            // Dismiss alert dialog
            dialogInterface.dismiss()
        }

        // on cancel
        builder.setNegativeButton(
            "Cancel"
        ) { dialogInterface, i -> // cancel alert dialog
            dialogInterface.cancel()
        }

        // show alert dialog
        builder.show()
    }
}