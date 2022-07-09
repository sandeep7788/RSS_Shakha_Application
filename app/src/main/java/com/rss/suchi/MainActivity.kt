package com.rss.suchi

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.rss.suchi.model.DashBoardShakaList
import com.rss.suchi.model.ShakaUser
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var context: Activity
    private var TAG = "@@MainActivity"
    private var progressDialog: SweetAlertDialog? = null
    private lateinit var binding: ActivityMainBinding
    private var shakaUserList: ArrayList<ShakaUser> = ArrayList()
    val allShakhaList: ArrayList<DashBoardShakaList> = ArrayList()
    private var exit = false

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
        binding.includeNavigation.isMskUser = MyApplication.readBoolPreferences(ApiContants.isMskUser)


        getDashboardData()

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
            startActivity(Intent(this@MainActivity, ServicesForm::class.java))
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
            startActivity(Intent(this@MainActivity, ShakaUserListActivity::class.java))
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
                            binding.txtTotalSwayamSevakTwo.text = totalSevak
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
}