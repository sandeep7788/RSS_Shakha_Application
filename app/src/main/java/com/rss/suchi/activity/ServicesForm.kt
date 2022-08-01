package com.rss.suchi.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.cbi_solar.helper.ApiContants
import com.cbi_solar.helper.ApiContants.isDeleted
import com.cbi_solar.helper.MyApplication
import com.cbi_solar.helper.Utility
import com.google.gson.JsonObject
import com.rss.suchi.R
import com.rss.suchi.databinding.ActivityServicesFormBinding
import com.rss.suchi.helper.ApiInterface
import com.rss.suchi.helper.RestClient
import com.rss.suchi.model.ViewRegistrationUserModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class ServicesForm : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    lateinit var binding: ActivityServicesFormBinding
    var TAG = "@@TAG"
    var progressDialog: SweetAlertDialog? = null
    var date: Calendar = Calendar.getInstance()
    var thisAYear = date.get(Calendar.YEAR).toInt()
    var thisAMonth = date.get(Calendar.MONTH).toInt()
    var thisADay = date.get(Calendar.DAY_OF_MONTH).toInt()
    private lateinit var context: Activity
    private var ss_id = 0
    private var isFormUpdate = false

    fun setSpinnerBloodGroup(spinner: Spinner) {
        val item = resources?.getStringArray(R.array.bloodGroup_array)

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
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                    binding.spinnerHint.visibility = View.VISIBLE
                }
            }
        }
    }

    fun setSpinnerEducation(spinner: Spinner) {
        val item = resources?.getStringArray(R.array.education_array)

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
                    binding.spinnerHintEducation.visibility = View.GONE
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                    binding.spinnerHintEducation.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services_form)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_services_form)
        context = this@ServicesForm

        binding.toolbar.txtTitle.text = "पंजीकरण"
        progressDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progressDialog!!.progressHelper.barColor = R.color.theme_color
        progressDialog!!.titleText = "लोड हो रहा है...."
        progressDialog!!.setCancelable(false)

        binding.spinnerBloodGroup.onItemSelectedListener = this
        setSpinnerBloodGroup(binding.spinnerBloodGroup)
        setSpinnerEducation(binding.spinnerEducation)

        if (intent != null && intent.getBooleanExtra("isFormUpdate", false)) {
            ss_id = intent.getIntExtra("ss_id", 0)
            isFormUpdate = true
            binding.btnSubmit.text = getString(R.string.update)
            binding.toolbar.imgDelete.visibility = View.VISIBLE
            getFormDetails()

            if (!MyApplication.readBoolPreferences(ApiContants.isMskUser)) {
                binding.btnSubmit.visibility = View.GONE
                binding.toolbar.imgDelete.visibility = View.GONE
                disableAllView()
            }
        }

        clickListener()
    }
    private fun disableEditText(editText: EditText) {
        editText.isFocusable = false
        editText.isEnabled = false
        editText.isCursorVisible = false
        editText.keyListener = null
        editText.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun disableText(view: TextView) {
        view.isFocusable = false
        view.isEnabled = false
        view.isCursorVisible = false
        view.keyListener = null
        view.setBackgroundColor(Color.TRANSPARENT)
        view.isClickable = false
    }

    private fun disableSpinner(view: Spinner) {
        view.isFocusable = false
        view.isEnabled = false
        view.setBackgroundColor(Color.TRANSPARENT)
        view.isClickable = false
    }

    private fun disableRadioButton(view : RadioButton) {
        view.isFocusable = false
        view.isEnabled = false
        view.setBackgroundColor(Color.TRANSPARENT)
        view.isClickable = false
    }
    private fun disableAllView(){

        disableEditText(binding.edtName)
        disableEditText(binding.edtLastName)
        disableEditText(binding.edtBusiness)
        disableEditText(binding.edtAddress)
        disableEditText(binding.edtMobileNumber)
        disableEditText(binding.edtWhatsAppMobileNumber)
        disableText(binding.txtDateOfBirth)
        disableSpinner(binding.spinnerBloodGroup)
        disableSpinner(binding.spinnerEducation)
        disableEditText(binding.edtEducationYear)
        disableEditText(binding.edtCurrentLiabilities)
        disableRadioButton(binding.radioPledgedYes)
        disableRadioButton(binding.radioPledgedNo)
        disableRadioButton(binding.radioGhoshiYes)
        disableRadioButton(binding.radioGhoshiNo)
        disableEditText(binding.edtPresent)
        disableEditText(binding.edtEast)
        disableEditText(binding.edtOther)

    }
    private fun getIndex(spinner: Spinner, myString: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(myString, ignoreCase = true)) {
                return i
            }
        }
        return 0
    }

    private fun getFormDetails() {
        progressDialog!!.show()

        val apiService: ApiInterface =
            RestClient().getClient(context)!!.create(ApiInterface::class.java)
        val call: Call<ViewRegistrationUserModel> = apiService.viewuser(ss_id)
        call.enqueue(object : Callback<ViewRegistrationUserModel?> {
            override fun onResponse(
                call: Call<ViewRegistrationUserModel?>,
                response: Response<ViewRegistrationUserModel?>
            ) {
                Log.e(TAG, "onResponse:1 " + response.body().toString())
                try {
                    if (response.isSuccessful && response.body() != null && response.body()?.data != null) {

                        var data = response.body()!!.data
                        binding.edtName.setText(data.name)
                        binding.edtLastName.setText(data.lastname)
                        binding.edtBusiness.setText(data.occupation)
                        binding.edtAddress.setText(data.address)
                        binding.edtMobileNumber.setText(data.mobile)
                        binding.edtWhatsAppMobileNumber.setText(data.whatsapp)
                        binding.txtDateOfBirth.text = data.dob
                        binding.spinnerBloodGroup.setSelection(
                            getIndex(
                                binding.spinnerBloodGroup,
                                data.bloodGroup
                            )
                        )
                        binding.spinnerEducation.setSelection(
                            getIndex(
                                binding.spinnerEducation,
                                data.shikshan
                            )
                        )

                        binding.edtEducationYear.setText(data.shikshanYear.toString())
                        binding.edtCurrentLiabilities.setText(data.preRespon)

                        if (data.oauth.equals(1))
                            binding.radioPledgedYes.isChecked = true
                        else binding.radioPledgedNo.isChecked = true

                        if (data.ghosh.equals(1))
                            binding.radioGhoshiYes.isChecked = true
                        else binding.radioGhoshiNo.isChecked = true

                        binding.edtPresent.setText(data.vidhikPresent)
                        binding.edtEast.setText(data.vidhikPast)
                        binding.edtOther.setText(data.other)

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

            override fun onFailure(call: Call<ViewRegistrationUserModel?>, t: Throwable) {
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

    private fun deleteForm() {
        progressDialog!!.show()

        val apiService: ApiInterface =
            RestClient().getClient(context)!!.create(ApiInterface::class.java)
        val call: Call<JsonObject> = apiService.deletess(ss_id)
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(
                call: Call<JsonObject?>,
                response: Response<JsonObject?>
            ) {
                Log.e(TAG, "onResponse:1 " + response.body().toString())
                try {
                    if (response.isSuccessful && response.body() != null) {
                        var jsonResponse = JSONObject(response.body().toString())
                        val data: JSONObject = jsonResponse.getJSONObject("data")

                        if (data.getInt("status") == 200) {
                            val pDialog =
                                SweetAlertDialog(this@ServicesForm, SweetAlertDialog.SUCCESS_TYPE)
                            pDialog.titleText = "सफलतापूर्वक हटा दिया गया!" + ""
                            pDialog.confirmText = "OK"
                            pDialog.progressHelper.barColor = Color.parseColor("#FFE8560D")
                            pDialog.setCancelable(false)
                            pDialog.setConfirmClickListener {
                                pDialog.dismiss()
                                val intent = intent
                                intent.putExtra(isDeleted,true)
                                setResult(RESULT_OK, intent)
                                finish()
                            }
                            pDialog.show()
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


    fun setDate(txtView: TextView) {
        Utility.hideKeyboard(this)
        val dpd = DatePickerDialog(
            this,
            R.style.DialogTheme,
            DatePickerDialog.OnDateSetListener { view2, thisYear, thisMonth, thisDay ->
//                thisAMonth = thisMonth + 1
//                thisADay = thisDay
//                thisAYear = thisYear
//txtView.text = thisDay.toString() + "-" + thisAMonth + "-" + thisYear
//
                val calendar = Calendar.getInstance()
                calendar[thisYear, thisMonth] = thisDay

                val dateFormat = SimpleDateFormat("dd-MM-yyyy")
                val dateString = dateFormat.format(calendar.getTime())

                txtView.text = dateString
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

    fun isValidPhoneNumber(target: CharSequence): Boolean {
        return if (target.length != 10) {
            false
        } else {
            Patterns.PHONE.matcher(target).matches()
        }
    }

    fun clickListener() {

        binding.btnSubmit.setOnClickListener {
            binding.layoutName.setBackgroundResource(R.drawable.edit_txtbg)
            binding.layoutLastName.setBackgroundResource(R.drawable.edit_txtbg)
            binding.layoutBusiness.setBackgroundResource(R.drawable.edit_txtbg)
            binding.layoutAddress.setBackgroundResource(R.drawable.edit_txtbg)
            binding.edtMobileNumber.setBackgroundResource(R.drawable.edit_txtbg)
            binding.edtWhatsAppMobileNumber.setBackgroundResource(R.drawable.edit_txtbg)
            binding.layoutDob.setBackgroundResource(R.drawable.edit_txtbg)
//            binding.layoutEducation.setBackgroundResource(R.drawable.edit_txtbg)
            binding.layoutEducationYear.setBackgroundResource(R.drawable.edit_txtbg)
            binding.layoutCurrentLiabilities.setBackgroundResource(R.drawable.edit_txtbg)
            binding.layoutPresent.setBackgroundResource(R.drawable.edit_txtbg)
            binding.layoutEast.setBackgroundResource(R.drawable.edit_txtbg)

            when {
                binding.edtName.text.isEmpty() -> {
                    Utility.showSnackBar(this, "कृपया नाम दर्ज करें")
                    binding.layoutName.setBackgroundResource(R.drawable.edit_txt_error)
                }
                binding.edtLastName.text.isEmpty() -> {
                    Utility.showSnackBar(this, "कृपया उपनाम दर्ज करें")
                    binding.layoutLastName.setBackgroundResource(R.drawable.edit_txt_error)
                }
                binding.edtBusiness.text.isEmpty() -> {
                    Utility.showSnackBar(this, "कृपया व्यवसाय दर्ज करें")
                    binding.layoutBusiness.setBackgroundResource(R.drawable.edit_txt_error)
                }
                binding.edtAddress.text.isEmpty() -> {
                    Utility.showSnackBar(this, "कृपया पता दर्ज करें")
                    binding.layoutAddress.setBackgroundResource(R.drawable.edit_txt_error)
                }
                binding.edtMobileNumber.text.isEmpty() || !isValidPhoneNumber(binding.edtMobileNumber.text.toString()) -> {
                    Utility.showSnackBar(this, "कृपया वैध मोबाइल नंबर दर्ज करें")
                    binding.edtMobileNumber.setBackgroundResource(R.drawable.edit_txt_error)
                }
                binding.edtWhatsAppMobileNumber.text.isEmpty() || !isValidPhoneNumber(binding.edtWhatsAppMobileNumber.text.toString()) -> {
                    Utility.showSnackBar(this, "कृपया वैध व्हाट्सएप मोबाइल नंबर दर्ज करें")
                    binding.edtWhatsAppMobileNumber.setBackgroundResource(R.drawable.edit_txt_error)
                }
                binding.txtDateOfBirth.text.isEmpty() -> {
                    Utility.showSnackBar(this, "कृपया जन्म तिथि चुनें")
                    binding.layoutDob.setBackgroundResource(R.drawable.edit_txt_error)
                }
                binding.edtEducationYear.text.isEmpty() &&  binding.spinnerEducation.selectedItemPosition != 0 -> {
                    Utility.showSnackBar(this, "कृपया शिक्षण वर्ष दर्ज करें")
                    binding.layoutEducationYear.setBackgroundResource(R.drawable.edit_txt_error)
                }
                binding.edtCurrentLiabilities.text.isEmpty() -> {
                    Utility.showSnackBar(this, "कृपया वर्तमान देयताएं दर्ज करें")
                    binding.layoutCurrentLiabilities.setBackgroundResource(R.drawable.edit_txt_error)
                }
//                binding.edtPresent.text.isEmpty() -> {
//                    Utility.showSnackBar(this, "कृपया कानूनीसंगठन वर्तमान समय दर्ज करें")
//                    binding.layoutPresent.setBackgroundResource(R.drawable.edit_txt_error)
//                }
//                binding.edtEast.text.isEmpty() -> {
//                    Utility.showSnackBar(this, "कृपया कानूनी संगठन पूर्व समय दर्ज करें")
//                    binding.layoutEast.setBackgroundResource(R.drawable.edit_txt_error)
//                }

                else -> {
                    if (isFormUpdate) {
                        formUpdate()
                    } else formRegistration()
                }
            }
        }

        binding.toolbar.imgBack.setOnClickListener { finish() }
        binding.txtDateOfBirth.setOnClickListener { setDate(binding.txtDateOfBirth) }
        binding.toolbar.imgDelete.setOnClickListener {
            showDialogDeleteUser()
        }
    }
    val REQUEST_CODE = 1
    private fun showDialog(msg : String) {
        val pDialog =
            SweetAlertDialog(this@ServicesForm, SweetAlertDialog.SUCCESS_TYPE)
        pDialog.titleText = msg + ""
        pDialog.confirmText = "OK"
        pDialog.progressHelper.barColor = Color.parseColor("#FFE8560D")
        pDialog.setCancelable(false)
        pDialog.setConfirmClickListener {
            pDialog.dismiss()
            val intent = intent
            setResult(RESULT_OK, intent)
            finish()
        }
        pDialog.show()
    }

    private fun showDialogDeleteUser() {
        val pDialog =
            SweetAlertDialog(this@ServicesForm, SweetAlertDialog.WARNING_TYPE)
        pDialog.titleText = "क्या आप वाकई हटाना चाहते हैं." + ""
        pDialog.confirmText = getString(R.string.yes)
        pDialog.cancelText = context.getString(R.string.no)
        pDialog.progressHelper.barColor = Color.parseColor("#FFE8560D")
        pDialog.setCancelable(false)
        pDialog.setConfirmClickListener {
            pDialog.dismiss()
            deleteForm()
        }
        pDialog.setCancelClickListener {
            pDialog.dismiss()
        }
        pDialog.show()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    fun getCurrentDateAndTime(): String? {
        val c: Date = Calendar.getInstance().time
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
        return simpleDateFormat.format(c)
    }

    private fun formRegistration() {
        progressDialog!!.show()

        val oAuth = if (binding.radioPledgedYes.isChecked) 1 else 0
        val ghosh = if (binding.radioGhoshiYes.isChecked) 1 else 0

        var request = JSONObject()
        request.put("name", binding.edtName.text.toString().trim())
        request.put("lastname", binding.edtLastName.text.toString().trim())
        request.put("occupation", binding.edtBusiness.text.toString().trim())
        request.put("address", binding.edtAddress.text.toString().trim())
        request.put("mobile", binding.edtMobileNumber.text.toString().trim())
        request.put("whatsapp", binding.edtWhatsAppMobileNumber.text.toString().trim())
        request.put("dob", binding.txtDateOfBirth.text.toString().trim())
        request.put("blood_group", binding.spinnerBloodGroup.selectedItem.toString().trim())
        request.put("shikshan", binding.spinnerEducation.selectedItem.toString().trim())
        request.put("shikshan_year", binding.edtEducationYear.text.toString().trim())
        request.put("pre_respon", binding.edtCurrentLiabilities.text.toString().trim())
        request.put("oauth", oAuth)
        request.put("ghosh", ghosh)
        request.put("vidhik_present", binding.edtPresent.text.toString().trim())
        request.put("vidhik_past", binding.edtPresent.text.toString().trim())
        request.put("createdy", MyApplication.ReadIntPreferences(ApiContants.PREF_USER_ID))
        request.put("updateddy", MyApplication.ReadIntPreferences(ApiContants.PREF_USER_ID))
        request.put("shakha", MyApplication.ReadIntPreferences(ApiContants.PREF_USER_SHAKA))
        request.put("other", binding.edtOther.text.toString())
        request.put("flag", "i")

        val apiService: ApiInterface =
            RestClient().getClient(context)!!.create(ApiInterface::class.java)
        val call: Call<JsonObject> = apiService.formRegistration(request.toString())
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
                            showDialog("सफलतापूर्वक फॉर्म जमा किया गया")
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


    private fun formUpdate() {
        progressDialog!!.show()

        val oAuth = if (binding.radioPledgedYes.isChecked) 1 else 0
        val ghosh = if (binding.radioGhoshiYes.isChecked) 1 else 0

        var request = JSONObject()
        request.put("name", binding.edtName.text.toString().trim())
        request.put("lastname", binding.edtLastName.text.toString().trim())
        request.put("occupation", binding.edtBusiness.text.toString().trim())
        request.put("address", binding.edtAddress.text.toString().trim())
        request.put("mobile", binding.edtMobileNumber.text.toString().trim())
        request.put("whatsapp", binding.edtWhatsAppMobileNumber.text.toString().trim())
        request.put("dob", binding.txtDateOfBirth.text.toString().trim())
        request.put("blood_group", binding.spinnerBloodGroup.selectedItem.toString().trim())
        request.put("shikshan", binding.spinnerEducation.selectedItem.toString().trim())
        request.put("shikshan_year", binding.edtEducationYear.text.toString().trim())
        request.put("pre_respon", binding.edtCurrentLiabilities.text.toString().trim())
        request.put("oauth", oAuth)
        request.put("ghosh", ghosh)
        request.put("vidhik_present", binding.edtPresent.text.toString().trim())
        request.put("vidhik_past", binding.edtPresent.text.toString().trim())
        request.put("createdy", MyApplication.ReadIntPreferences(ApiContants.PREF_USER_ID))
        request.put("updateddy", MyApplication.ReadIntPreferences(ApiContants.PREF_USER_ID))
        request.put("shakha", MyApplication.ReadIntPreferences(ApiContants.PREF_USER_SHAKA))
        request.put("other", binding.edtOther.text.toString())
        request.put("flag", "u")
        request.put("ss_id", ss_id)

        val apiService: ApiInterface =
            RestClient().getClient(context)!!.create(ApiInterface::class.java)
        val call: Call<JsonObject> = apiService.formRegistration(request.toString())
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
                            showDialog("सफलतापूर्वक फॉर्म अपडेट किया गया")
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