package com.rss.suchi.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.cbi_solar.helper.ApiContants
import com.cbi_solar.helper.MyApplication
import com.rss.suchi.R
import com.rss.suchi.databinding.ActivityContactUsBinding
import com.rss.suchi.databinding.ActivityNotifactionListBinding

class ContactUsActivity : AppCompatActivity() {

    lateinit var binding: ActivityContactUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_us)
        binding.toolbar.txtTitle.text = "संपर्क करें"

        binding.txtCall.setOnClickListener { makeCall(this@ContactUsActivity,"+919929225263") }
        binding.toolbar.imgBack.setOnClickListener { finish() }

        if (MyApplication.ReadStringPreferences(ApiContants.PREF_STATUS)!=null
            && MyApplication.ReadStringPreferences(ApiContants.PREF_STATUS).toString().equals("-1")) {
            binding.layoutNumber.visibility = View.GONE
        }
    }

    fun makeCall(context: Context, mob: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL)

            intent.data = Uri.parse("tel:$mob")
            context.startActivity(intent)
        } catch (e: java.lang.Exception) {
            Toast.makeText(context,
                "Unable to call at this time", Toast.LENGTH_SHORT).show()
        }
    }
}