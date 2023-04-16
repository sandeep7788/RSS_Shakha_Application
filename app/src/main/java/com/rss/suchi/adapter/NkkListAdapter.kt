package com.rss.suchi.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import com.rss.suchi.activity.NKKForm
import com.rss.suchi.databinding.AdapterNkkBinding
import com.rss.suchi.model.DashBoardShakaList
import com.rss.suchi.model.NkkListModel


class NkkListAdapter(var requestManager: RequestManager, var context: Activity) :
    RecyclerView.Adapter<NkkListAdapter.ViewHolder>() {


    private var mOptionList: ArrayList<NkkListModel> =
        java.util.ArrayList<NkkListModel>()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NkkListAdapter.ViewHolder {

        val viewProductCategoryBinding: AdapterNkkBinding =
            AdapterNkkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        context = parent.getA
        return ViewHolder(viewProductCategoryBinding)


    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        try {
            holder.binding.txtName.text =
                mOptionList.get(position).nkkName + " " + mOptionList.get(position).nkkLastName
            holder.binding.txtMobile.text = mOptionList.get(position).nkkMobile + " "
            holder.binding.txtOccupation.text = mOptionList.get(position).nkkOccupation + " "
//            holder.binding.txtPreRespon.text = mOptionList.get(position).nkkOccupation + " "

            if (mOptionList.get(position).nkkName != null && mOptionList.get(position).nkkOccupation.isNotEmpty())
                holder.binding.txtFirstChar.text = mOptionList.get(position).nkkName.substring(0, 1)

            holder.binding.linearLayout.setOnClickListener {
                val mainIntent = Intent(context, NKKForm::class.java)
                mainIntent.putExtra("isFormUpdate", true)
                mainIntent.putExtra("nkk_id", mOptionList.get(position).nkkId.toInt())
                mainIntent.putExtra("ss_id", mOptionList.get(position).nkkShakaId.toInt())
                mainIntent.putExtra("sknyear", mOptionList.get(position).nkkShikshanYear.toString())

                val jsonString = Gson().toJson(mOptionList.get(position))
                mainIntent.putExtra("obj", jsonString)

                val REQUEST_CODE = 1;
                context!!.startActivityForResult(mainIntent, REQUEST_CODE)
            }
        } catch (e: Exception) {

        }

    }


    fun setData(mOptionList: ArrayList<NkkListModel>) {
        this.mOptionList = mOptionList
        notifyDataSetChanged()
    }

    fun updateList(list: ArrayList<NkkListModel>) {
        mOptionList = list
        notifyDataSetChanged()
    }


    private fun getItem(index: Int): String {
        return mOptionList[index].nkkName.toString()
    }

    override fun getItemCount(): Int {
        return mOptionList.size
    }


    inner class ViewHolder(val binding: AdapterNkkBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        override fun onClick(v: View?) {
            ////
        }
    }

    fun bind(photo: DashBoardShakaList) {

    }
}