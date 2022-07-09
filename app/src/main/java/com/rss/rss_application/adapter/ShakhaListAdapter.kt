package com.rss.rss_application.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.rss.rss_application.activity.ServicesForm
import com.rss.rss_application.activity.ShakaUserListActivity
import com.rss.rss_application.databinding.AdapterAllShakaLayoutBinding
import com.rss.rss_application.databinding.AdapterShakaLayoutBinding
import com.rss.rss_application.model.DashBoardShakaList

class ShakhaListAdapter(var requestManager: RequestManager) :
    RecyclerView.Adapter<ShakhaListAdapter.ViewHolder>() {

    lateinit var context: Context
    private var mOptionList: ArrayList<DashBoardShakaList> =
        java.util.ArrayList<DashBoardShakaList>()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShakhaListAdapter.ViewHolder {

        val viewProductCategoryBinding: AdapterAllShakaLayoutBinding =
            AdapterAllShakaLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return ViewHolder(viewProductCategoryBinding)


    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        try {
            holder.binding.txtShakaName.text = mOptionList.get(position).shakha_name + " "
            holder.binding.txtTotalShaka.text = mOptionList.get(position).total.toString() + " "
            holder.binding.layoutShakha.setOnClickListener {
                val mainIntent = Intent(context, ShakaUserListActivity::class.java)
                mainIntent.putExtra("isFormUpdate", true)
                mainIntent.putExtra("ss_id", mOptionList.get(position).shakhaid.toInt())
                context.startActivity(mainIntent)
            }
        } catch (e: Exception) {

        }

    }

    fun setData(mOptionList: ArrayList<DashBoardShakaList>) {
        this.mOptionList = mOptionList
        notifyDataSetChanged()
    }

    fun updateList(list: ArrayList<DashBoardShakaList>) {
        mOptionList = list
        notifyDataSetChanged()
    }


    private fun getItem(index: Int): String {
        return mOptionList[index].shakha_name.toString()
    }

    override fun getItemCount(): Int {
        return mOptionList.size
    }


    inner class ViewHolder(val binding: AdapterAllShakaLayoutBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        override fun onClick(v: View?) {
            ////
        }
    }

    fun bind(photo: DashBoardShakaList) {

    }
}