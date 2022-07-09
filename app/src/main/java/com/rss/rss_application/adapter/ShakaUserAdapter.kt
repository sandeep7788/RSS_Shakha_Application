package com.rss.rss_application.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.rss.rss_application.activity.ServicesForm
import com.rss.rss_application.activity.SignInActivity
import com.rss.rss_application.databinding.AdapterShakaLayoutBinding
import com.rss.rss_application.model.ShakaUser

class ShakaUserAdapter(var requestManager: RequestManager,var activity: Activity) :
    RecyclerView.Adapter<ShakaUserAdapter.ViewHolder>() {

    lateinit var context: Context
    private var mOptionList: ArrayList<ShakaUser> = java.util.ArrayList<ShakaUser>()
    val REQUEST_CODE = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShakaUserAdapter.ViewHolder {

        val viewProductCategoryBinding: AdapterShakaLayoutBinding =
            AdapterShakaLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return ViewHolder(viewProductCategoryBinding)


    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        try {
            holder.binding.txtName.text = mOptionList.get(position).name + " " + mOptionList.get(position).lastname
            holder.binding.txtMobile.text = mOptionList.get(position).mobile + " "
            holder.binding.txtOccupation.text = mOptionList.get(position).occupation + " "
            holder.binding.txtPreRespon.text = mOptionList.get(position).preRespon + " "

            if (mOptionList.get(position).name != null && mOptionList.get(position).preRespon.isNotEmpty())
                holder.binding.txtFirstChar.text = mOptionList.get(position).name.substring(0, 1)

            holder.binding.linearLayout.setOnClickListener {
                val mainIntent = Intent(context, ServicesForm::class.java)
                mainIntent.putExtra("isFormUpdate",true)
                mainIntent.putExtra("ss_id",mOptionList.get(position).ssId.toInt())
                activity!!.startActivityForResult(mainIntent,REQUEST_CODE)
            }
        } catch (e: Exception) {

        }

    }

    fun setData(mOptionList: ArrayList<ShakaUser>) {
        this.mOptionList = mOptionList
        notifyDataSetChanged()
    }

    fun updateList(list: ArrayList<ShakaUser>) {
        mOptionList = list
        notifyDataSetChanged()
    }


    private fun getItem(index: Int): String {
        return mOptionList[index].name.toString()
    }

    override fun getItemCount(): Int {
        return mOptionList.size
    }


    inner class ViewHolder(val binding: AdapterShakaLayoutBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        override fun onClick(v: View?) {
            ////
        }
    }

    fun bind(photo: ShakaUser) {

    }
}