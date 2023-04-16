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
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.rss.suchi.R
import com.rss.suchi.databinding.ActivityNkkFormBinding
import com.rss.suchi.helper.ApiInterface
import com.rss.suchi.helper.RestClient
import com.rss.suchi.model.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class NKKForm : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    lateinit var binding: ActivityNkkFormBinding
    var TAG = "@@TAG"
    var progressDialog: SweetAlertDialog? = null
    var date: Calendar = Calendar.getInstance()
    var thisAYear = date.get(Calendar.YEAR).toInt()
    var thisAMonth = date.get(Calendar.MONTH).toInt()
    var thisADay = date.get(Calendar.DAY_OF_MONTH).toInt()
    private lateinit var context: Activity
    private var nkk_id = 0
    private var sknyear = ""
    private var isFormUpdate = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nkk_form)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nkk_form)
        context = this@NKKForm

        binding.toolbar.txtTitle.text = getString(R.string.nkk)
        progressDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progressDialog!!.progressHelper.barColor = R.color.theme_color
        progressDialog!!.titleText = "लोड हो रहा है...."
        progressDialog!!.setCancelable(false)

        binding.spinnerBloodGroup.onItemSelectedListener = this

        getDaitvyListList()


        setSpinnerBloodGroup(binding.spinnerBloodGroup)
        setSpinnerEducation(binding.spinnerEducation)

        binding.imgMobile.visibility = View.VISIBLE
        clickListener()

//        setSpinnerEducation(binding.spinnerEducation)

        if (intent != null && intent.getBooleanExtra("isFormUpdate", false)) {
            nkk_id = intent.getIntExtra("nkk_id", 0)
//            ss_id = intent.getIntExtra("ss_id", 0)
            sknyear = intent.getStringExtra("sknyear").toString()
            isFormUpdate = true
            binding.btnSubmit.text = getString(R.string.update)
            binding.toolbar.imgDelete.visibility = View.GONE

            var obj: String? = intent.getStringExtra("obj")
            convertedObject = Gson().fromJson(obj, NkkListModel::class.java)

//            getFormDetails(convertedObject!!)

            if (intent.getBooleanExtra(ApiContants.isDisable, false)) {
                binding.btnSubmit.visibility = View.GONE
                binding.toolbar.imgDelete.visibility = View.GONE
                disableAllView()
            }
            /*else if (!MyApplication.readBoolPreferences(ApiContants.isMskUser)) {
                    binding.btnSubmit.visibility = View.GONE
                    binding.toolbar.imgDelete.visibility = View.GONE
                    disableAllView()
                }*/
        }






    }

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



    var convertedObject: NkkListModel? = null

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

    private fun disableRadioButton(view: RadioButton) {
        view.isFocusable = false
        view.isEnabled = false
        view.setBackgroundColor(Color.TRANSPARENT)
        view.isClickable = false
    }

    private fun disableAllView() {

        disableEditText(binding.edtName)
        disableEditText(binding.edtLastName)
        disableEditText(binding.edtBusiness)
        disableEditText(binding.edtAddress)
        disableEditText(binding.edtMobileNumber)
        disableEditText(binding.edtEmail)

        disableText(binding.txtDateOfBirth)
        disableSpinner(binding.spinnerBloodGroup)

        disableEditText(binding.edtOther)
        disableSpinner(binding.spinnerEducation)
        disableEditText(binding.edtEducationYear)

    }

    private fun getIndex(spinner: Spinner, myString: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().trim()
                    .equals(myString.trim(), ignoreCase = true)
                || spinner.getItemAtPosition(i).toString().trim()
                    .equals(myString.trim(), ignoreCase = true)
            ) {
                return i
            }
        }
        return 0
    }

    private fun getShakhaId(selected: String): Int {
        val account = allShakhaList.first { it.shakha.trim().equals(selected.trim()) }
        return account.shakhaid
    }

    //शारीरिक शिक्षण प्रमुख
//     Account account = accountList.stream().filter(a -> a.getId() == YOUR_ID).collect(Collectors.toList()).get(0);
    private fun getDaitvaId(selected: String): Int {


        val account = allDaitvList.first { it!!.daitav_name.trim()!!.contentEquals(selected) }

//        for (i in 0 until allDaitvList.size) {
//            if (allDaitvList.get(i).daitav_name.equals(selected, true)) {
//                return allDaitvList.get(i).daitav_id
//            }
//        }
        return account.daitav_id
    }

    private fun getFormDetails(convertedObject: NkkListModel) {

        var data = convertedObject!!
        binding.edtName.setText(data.nkkName)
        binding.edtLastName.setText(data.nkkLastName)
        binding.edtBusiness.setText(data.nkkOccupation)
        binding.edtAddress.setText(data.nkkAddress)
        binding.edtMobileNumber.setText(data.nkkMobile)
        binding.txtDateOfBirth.text = data.nkkDob

        binding.spinnerBloodGroup.setSelection(
            getIndex(
                binding.spinnerBloodGroup,
                data.nkkBloodGroup
            )
        )

        for (i in 0 until allDaitvList.size) {
            if (allDaitvList.get(i).daitav_id.toString().trim().equals(data.nkkDaitva.toString().trim())
            ) {
                Log.e(TAG, "getFormDetails: ddddddd "+i )
                binding.spinnerDaitvy.setSelection(i)
                break
            }
        }

        for (i in 0 until allShakhaList.size) {
            if (allShakhaList.get(i).shakhaid.toString().trim().equals(data.nkkShakaId.toString().trim())
            ) {
                Log.e(TAG, "getFormDetails: sssssssssssssss "+i )
                binding.spinnerShakha.setSelection(i)
                break
            }
        }


//        binding.spinnerShakha.setSelection(
//            getIndex(
//                binding.spinnerShakha,
//                data.nkkShikshan
//            )
//        )
        binding.spinnerEducation.setSelection(
            getIndex(
                binding.spinnerEducation,
                data.nkkShikshan
            )
        )


        binding.edtEmail.setText(data.nkkEmail)
        binding.edtEducationYear.setText(data.nkkShikshanYear.toString())

        binding.edtOther.setText("extra")
    }

    private fun deleteForm() {
        progressDialog!!.show()

        val apiService: ApiInterface =
            RestClient().getClient(context)!!.create(ApiInterface::class.java)
        val call: Call<JsonObject> = apiService.deletess(nkk_id)
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
                                SweetAlertDialog(this@NKKForm, SweetAlertDialog.SUCCESS_TYPE)
                            pDialog.titleText = "सफलतापूर्वक हटा दिया गया!" + ""
                            pDialog.confirmText = "OK"
                            pDialog.progressHelper.barColor = Color.parseColor("#FFE8560D")
                            pDialog.setCancelable(false)
                            pDialog.setConfirmClickListener {
                                pDialog.dismiss()
                                val intent = intent
                                intent.putExtra(isDeleted, true)
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

//                val dateFormat = SimpleDateFormat("dd-MM-yyyy")
                val dateFormat = SimpleDateFormat("MM-dd-yyyy")
                val dateString = dateFormat.format(calendar.time)

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
            binding.layoutDob.setBackgroundResource(R.drawable.edit_txtbg)

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

                binding.txtDateOfBirth.text.isEmpty() -> {
                    Utility.showSnackBar(this, "कृपया जन्म तिथि चुनें")
                    binding.layoutDob.setBackgroundResource(R.drawable.edit_txt_error)
                }

                binding.edtEmail.text.isEmpty() -> {
                    Utility.showSnackBar(this, "कृपया email-Id दर्ज करें")
                    binding.edtEmail.setBackgroundResource(R.drawable.edit_txt_error)
                }

                binding.edtEducationYear.text.isEmpty() && binding.spinnerEducation.selectedItemPosition != 0 -> {
                    Utility.showSnackBar(this, "कृपया शिक्षण वर्ष दर्ज करें")
                    binding.layoutEducationYear.setBackgroundResource(R.drawable.edit_txt_error)
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

        binding.imgMobile.setOnClickListener {
            if (!isValidPhoneNumber(binding.edtMobileNumber.text.toString())) {
                Utility.hideKeyboard(this@NKKForm)
                Utility.showSnackBar(this@NKKForm, "अमान्य मोबाइल नंबर")
            } else if (binding.edtMobileNumber.text.isNotEmpty()) {
                Utility.makeCall(
                    this@NKKForm,
                    binding.edtMobileNumber.text.trim().toString()
                )
            }
        }
    }

    val REQUEST_CODE = 1
    private fun showDialog(msg: String) {
        val pDialog =
            SweetAlertDialog(this@NKKForm, SweetAlertDialog.SUCCESS_TYPE)
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
            SweetAlertDialog(this@NKKForm, SweetAlertDialog.WARNING_TYPE)
        pDialog.titleText = "work on progress. please try after some time." + ""
        pDialog.confirmText = getString(R.string.yes)
        pDialog.cancelText = context.getString(R.string.no)
        pDialog.progressHelper.barColor = Color.parseColor("#FFE8560D")
        pDialog.setCancelable(false)
        pDialog.setConfirmClickListener {
            pDialog.dismiss()
//            deleteForm()
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


    private val allShakhaList: ArrayList<ShakhaDropDownListModel> = ArrayList()

    private val allDaitvList: ArrayList<DaitvyDropDownListModel> = ArrayList()

    private fun getShakhaListList() {
        progressDialog!!.show()

        val apiService: ApiInterface =
            RestClient().getClient(context)!!.create(ApiInterface::class.java)
        val call: Call<ShakhaDropDownMainListModel> =
            apiService.getShakhaList(MyApplication.ReadIntPreferences(ApiContants.PREF_nager))

        call.enqueue(object : Callback<ShakhaDropDownMainListModel?> {
            override fun onResponse(
                call: Call<ShakhaDropDownMainListModel?>,
                response: Response<ShakhaDropDownMainListModel?>
            ) {
                Log.e(TAG, "onResponse:1 " + response.body().toString())
                try {
                    allShakhaList.clear()
                    if (response.isSuccessful && response.body() != null) {

                        allShakhaList.addAll(response!!.body()!!.data)
                        if (response.body() == null || allShakhaList.isEmpty()) {
                            Utility.toast(this@NKKForm, "shakha list is empty")
                        } else {
                            setSpinnerShakha()
                        }
                    } else {
                        Utility.toast(this@NKKForm, "error to fetch shakha list")
                    }

                } catch (e: Exception) {
                    Log.d(TAG, "onResponse: " + e.message.toString())
                    Toast.makeText(
                        context,
                        " " + resources.getString(R.string.error),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                progressDialog!!.dismiss()

                if (isFormUpdate)
                    getFormDetails(convertedObject!!)

            }

            override fun onFailure(call: Call<ShakhaDropDownMainListModel?>, t: Throwable) {
                Utility.toast(this@NKKForm, "shakha list is empty")
                if (Utility.isNetworkAvailable(context)) {
                    Toast.makeText(
                        context,
                        " " + resources.getString(R.string.error),
                        Toast.LENGTH_LONG
                    )
                        .show()

                }
                progressDialog!!.dismiss()
            }
        })
    }

    fun setSpinnerShakha() {
        var spinner = binding.spinnerShakha
        val item: ArrayList<String> = ArrayList()

        allShakhaList.forEach {
            if (it != null && it.shakha != null)
                item.add(it.shakha)
        }

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
                binding.spinnerHintEducation.visibility = View.GONE
            }
        }
    }

    private fun getDaitvyListList() {
        progressDialog!!.show()


        val apiService: ApiInterface =
            RestClient().getClient(context)!!.create(ApiInterface::class.java)
        val call: Call<DaitvyDropDownMainListModel> =
            apiService.getDaitvyList()

        call.enqueue(object : Callback<DaitvyDropDownMainListModel?> {
            override fun onResponse(
                call: Call<DaitvyDropDownMainListModel?>,
                response: Response<DaitvyDropDownMainListModel?>
            ) {
                allDaitvList.clear()
                try {
                    if (response.isSuccessful && response.body() != null) {

                        allDaitvList.addAll(response!!.body()!!.data!!)
                        setSpinnerDaitvy()
                        if (response.body() == null || allDaitvList.isEmpty()) {
                            Utility.toast(
                                this@NKKForm,
                                "error occurred while fetch daitva list data"
                            )
                        } else {
//                            setSpinnerDaitvy()
                        }
                    } else {
                        Utility.toast(this@NKKForm, "error occurred while fetch daitva list data")
                    }

                } catch (e: Exception) {
                    Log.d(TAG, "onResponse: " + e.message.toString())
                    Toast.makeText(
                        context,
                        " " + resources.getString(R.string.error),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }

                progressDialog!!.dismiss()
                getShakhaListList()
            }

            override fun onFailure(call: Call<DaitvyDropDownMainListModel?>, t: Throwable) {
                Utility.toast(this@NKKForm, "problem occurred with daitva list data")
                if (Utility.isNetworkAvailable(context)) {
                    Toast.makeText(
                        context,
                        " " + resources.getString(R.string.error),
                        Toast.LENGTH_LONG
                    )
                        .show()

                }
                progressDialog!!.dismiss()
            }
        })
    }

    fun setSpinnerDaitvy() {
        var spinner = binding.spinnerDaitvy
        val item: ArrayList<String> = ArrayList()

        allDaitvList.forEach {
            if (it.daitav_name != null)
                item.add(it.daitav_name)
        }

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
                binding.spinnerHintEducation.visibility = View.GONE
            }
        }
    }

    private fun formRegistration() {
        progressDialog!!.show()

        var request = JSONObject()

        var educationYear = if (binding.spinnerEducation.selectedItemPosition == 0) {
            "0"
        } else {
            binding.edtEducationYear.text.toString().trim()
        }

//        request.put("nagerid", MyApplication.ReadIntPreferences(ApiContants.PREF_nager))
        request.put("nagerid", MyApplication.ReadIntPreferences(ApiContants.PREF_nager).toString())
        request.put("name", binding.edtName.text.toString().trim())
        request.put("lastname", binding.edtLastName.text.toString().trim())
        request.put("occupation", binding.edtBusiness.text.toString().trim())
        request.put("address", binding.edtAddress.text.toString().trim())
        request.put("mobile", binding.edtMobileNumber.text.toString().trim())
        request.put("dob", binding.txtDateOfBirth.text.toString())
        request.put("bg", binding.spinnerBloodGroup.selectedItem.toString().trim())

        request.put("email", binding.edtEmail.getText().toString().trim())

        request.put(
            "daitva",
            getDaitvaId(binding.spinnerDaitvy.selectedItem.toString().trim()).toString()
        )
        request.put(
            "shikshan",
            binding.spinnerEducation.selectedItem.toString().trim()
        )

//        request.put("sknyear", binding.spinnerDaitvy.selectedItem.toString().trim())

        request.put("sknyear", educationYear)
//        request.put("nkk_shaka_id", getShakhaId(binding.spinnerShakha.selectedItem.toString().trim()))
        request.put(
            "sid",
            getShakhaId(binding.spinnerShakha.selectedItem.toString().trim())
        )

//        request.put("createdy", MyApplication.ReadIntPreferences(ApiContants.PREF_nager))
//        request.put("updateddy", MyApplication.ReadIntPreferences(ApiContants.PREF_USER_ID))
//        request.put("shakha", MyApplication.ReadIntPreferences(ApiContants.PREF_USER_SHAKA))
//        request.put("other", binding.edtOther.text.toString())
//        request.put("flag", "i")

        Log.d(TAG, "formRegistration: @@@@@@@@@@@" + request)

        val apiService: ApiInterface =
            RestClient().getClient(context)!!.create(ApiInterface::class.java)
        val call: Call<JsonObject> = apiService.addNkk(request.toString())
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

                if (progressDialog!!.isShowing) {
                    progressDialog!!.dismiss()
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

        var educationYear = if (binding.spinnerEducation.selectedItemPosition == 0) {
            "0"
        } else {
            binding.edtEducationYear.text.toString().trim()
        }

        var request = JSONObject()

//        request.put("nagerid", MyApplication.ReadIntPreferences(ApiContants.PREF_nager))
        request.put("nagerid", MyApplication.ReadIntPreferences(ApiContants.PREF_nager).toString())
        request.put("name", binding.edtName.text.toString().trim())
        request.put("lastname", binding.edtLastName.text.toString().trim())
        request.put("occupation", binding.edtBusiness.text.toString().trim())
        request.put("address", binding.edtAddress.text.toString().trim())
        request.put("mobile", binding.edtMobileNumber.text.toString().trim())
        request.put("dob", binding.txtDateOfBirth.text.toString())
        request.put("bg", binding.spinnerBloodGroup.selectedItem.toString().trim())

        request.put("email", binding.edtEmail.getText().toString().trim())

        request.put(
            "daitva",
            getDaitvaId(binding.spinnerDaitvy.selectedItem.toString().trim()).toString()
        )
        request.put(
            "shikshan",
            binding.spinnerEducation.selectedItem.toString().trim()
        )


        request.put("sknyear", educationYear)

        request.put(
            "sid",
            getShakhaId(binding.spinnerShakha.selectedItem.toString().trim())
        )
//        request.put("nkk_shaka_id", getShakhaId(binding.spinnerShakha.selectedItem.toString().trim()))
        request.put("nkk_id", nkk_id.toString())

        Log.e(TAG, "formUpdate: "+request )

        val apiService: ApiInterface =
            RestClient().getClient(context)!!.create(ApiInterface::class.java)
        val call: Call<JsonObject> = apiService.updateNkk(request.toString())
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                Log.e(TAG, "onResponse:1 " + response.body().toString())
                try {
                    progressDialog?.dismiss()
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
                    binding.spinnerHintEducation.visibility = View.GONE
                }
            }
        }
    }

}


//
//
//
//
//
//
//
//
//
//{
//{
//    "nagerid": "1",
//    "name": "ufuf",
//    "lastname": "ufuffu",
//    "occupation": "yficvjchc",
//    "address": "jcjcjcig",
//    "mobile": "6888600668",
//    "dob": "15-04-1923",
//    "bg": "A+",
//    "email": "hhc@hxcu.ivov",
//    "daitva": "1",
//    "shikshan": "1",
//    "sknyear": "2003",
//    "sid": "1"
//}
//}
//{
//    "nagerid": "1",
//    "name": "Sandrrp",
//    "lastname": "pareek",
//    "occupation": "Engeenier",
//    "address": "6 kalyan nager road no 3 vki area ",
//    "mobile": "9292998763",
//    "dob": "01-15-1987",
//    "bg": "o+",
//    "email": "preekharsh@hotmail.com",
//    "daitva": "1",
//    "shikshan": "Pratham",
//    "sknyear": "2003",
//    "sid": "1"
//}