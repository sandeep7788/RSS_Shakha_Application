package com.rss.rss_application.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.cbi_solar.helper.ApiContants
import com.cbi_solar.helper.MyApplication
import com.cbi_solar.helper.Utility
import com.google.gson.JsonObject
import com.rss.rss_application.MainActivity
import com.rss.rss_application.R
import com.rss.rss_application.databinding.ActivityAttendanceFormBinding
import com.rss.rss_application.helper.ApiInterface
import com.rss.rss_application.helper.RestClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AttendanceFormActivity : AppCompatActivity() {
    lateinit var binding: ActivityAttendanceFormBinding
    var progressDialog: SweetAlertDialog? = null
    var date: Calendar = Calendar.getInstance()
    var thisAYear = date.get(Calendar.YEAR).toInt()
    var thisAMonth = date.get(Calendar.MONTH).toInt()
    var thisADay = date.get(Calendar.DAY_OF_MONTH).toInt()
    private lateinit var context: Activity
    private var TAG = "@@AttendanceFormActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_form)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_attendance_form)
        context = this@AttendanceFormActivity

        binding.toolbar.txtTitle.text = getString(R.string.attendance)
        progressDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progressDialog!!.progressHelper.barColor = R.color.theme_color
        progressDialog!!.titleText = "लोड हो रहा है...."
        progressDialog!!.setCancelable(false)
        binding.showTravelName = true

        setUpRadioButton()
        clickListener()
        binding.txtDate.setOnClickListener { setDate(binding.txtDate) }
    }

    private fun setUpRadioButton() {
        binding.radioTravel.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { arg0, id ->
            when (id) {
                R.id.radioTravelYes -> binding.showTravelName = true
                R.id.radioTravelNo -> binding.showTravelName = false
            }
        })
    }

    fun clickListener() {

        binding.btnSubmit.setOnClickListener {
            binding.layoutDate.setBackgroundResource(R.drawable.edit_txtbg)
            binding.layoutNumber.setBackgroundResource(R.drawable.edit_txtbg)
            binding.edtTravelName.setBackgroundResource(R.drawable.edit_txtbg)

            when {
                binding.txtDate.text.isEmpty() -> {
                    Utility.showSnackBar(this, "कृपया तिथि चुनें")
                    binding.layoutDate.setBackgroundResource(R.drawable.edit_txt_error)
                }
                binding.edtNumber.text.isEmpty() -> {
                    Utility.showSnackBar(this, "कृपया सांख्य दर्ज करें")
                    binding.layoutNumber.setBackgroundResource(R.drawable.edit_txt_error)
                }
                binding.edtTravelName.text.isEmpty() && binding.radioTravelYes.isChecked -> {
                    Utility.showSnackBar(this, "कृपया प्रवासी दर्ज करें")
                    binding.layoutTravel.setBackgroundResource(R.drawable.edit_txt_error)
                }

                else -> {
                    sendRequest()
                }
            }
        }

        binding.toolbar.imgBack.setOnClickListener { finish() }
    }

    private fun showDialog() {
        val pDialog =
            SweetAlertDialog(this@AttendanceFormActivity, SweetAlertDialog.SUCCESS_TYPE)
        pDialog.titleText = "उपस्थिति सफलतापूर्वक दर्ज की गई" + ""
        pDialog.confirmText = "OK"
        pDialog.progressHelper.barColor = Color.parseColor("#FFE8560D")
        pDialog.setCancelable(false)
        pDialog.setConfirmClickListener {
            pDialog.dismiss()
            startActivity(Intent(this@AttendanceFormActivity, MainActivity::class.java))
            finish()
        }
        pDialog.show()
    }
    fun setDate(txtView: TextView) {
        Utility.hideKeyboard(this)
        val dpd = DatePickerDialog(
            this,
            R.style.DialogTheme,
            DatePickerDialog.OnDateSetListener { view2, thisYear, thisMonth, thisDay ->
                thisAMonth = thisMonth + 1
                thisADay = thisDay
                thisAYear = thisYear

                txtView.text = thisDay.toString() + "-" + thisAMonth + "-" + thisYear
                val newDate: Calendar = Calendar.getInstance()
                newDate.set(thisYear, thisMonth, thisDay)
//                mh.entryDate = newDate.timeInMillis // setting new date
//                    Log.e("@@date1", newDate.timeInMillis.toString() + " ")
            },
            thisAYear,
            thisAMonth,
            thisADay
        )
        dpd.datePicker.spinnersShown = true
        dpd.datePicker.calendarViewShown = false
        dpd.show()
    }

    private fun sendRequest() {
        progressDialog!!.show()

        val radioTravel = if (binding.radioTravelYes.isChecked) 1 else 0

        var request = JSONObject()
        request.put("date",binding.txtDate.text.toString().trim())
        request.put("shakha",binding.edtNumber.text.toString().trim())
        request.put("pravas",radioTravel)
        request.put("pravashi_name",binding.edtTravelName.text.toString().trim())
        request.put("sankhya",MyApplication.ReadIntPreferences(ApiContants.PREF_USER_SHAKA))
        request.put("flag","u")

        val apiService: ApiInterface =
            RestClient().getClient(context)!!.create(ApiInterface::class.java)
        val call: Call<JsonObject> = apiService.attandance(request.toString())
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.e(TAG, "onResponse:1 " + response.body().toString())
                try {
                    if (response.isSuccessful) {

                        Log.d(TAG, "onResponse: " + response.body().toString())
                        val jsonObject =
                            JSONObject(response.body().toString()).getJSONObject("data")

                        if (jsonObject.has("response")) {
                            var msg = jsonObject.getString("response")
                            Toast.makeText(
                                context,
                                " " + msg,
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        if (jsonObject.has("status") && jsonObject.getInt("status") == 200) {
                            showDialog()
                        } else {
                            Utility.showDialog(
                                context,
                                SweetAlertDialog.WARNING_TYPE,
                                resources.getString(R.string.error)
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
                    Log.d(TAG, "onResponse: " + e.message.toString())
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
                    Log.e(TAG, "onFailure: " + t.message)
                }
                progressDialog!!.dismiss()
            }
        })
    }

}