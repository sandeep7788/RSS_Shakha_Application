package com.rss.suchi.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rss.suchi.R
import com.rss.suchi.activity.NkkListActivity
import com.rss.suchi.databinding.AdapterDashboardLayoutBinding
import com.rss.suchi.model.DashboardNewListModel
import com.rss.suchi.model.ShakaUser

class DashboardAdapter(var activity: Activity, var listener: MyAdapterListener) :
    RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

    lateinit var context: Context
    private var mOptionList: ArrayList<DashboardNewListModel> =
        java.util.ArrayList<DashboardNewListModel>()
    val REQUEST_CODE = 1
    private var flagId="";

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardAdapter.ViewHolder {

        val viewProductCategoryBinding: AdapterDashboardLayoutBinding =
            AdapterDashboardLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        context = parent.context
        return ViewHolder(viewProductCategoryBinding)


    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        try {
            holder.binding.txtName.text =
                activity.resources.getString(R.string.name) + ": " + mOptionList.get(position).name
            holder.binding.txTotalSwayamSevak.text =
                activity.resources.getString(R.string.totalSwayamsevak) + ": " + mOptionList.get(
                    position
                ).toatlSwayamSevak.toString()
            holder.binding.txtTotalShaka.text =
                activity.resources.getString(R.string.totalShakha) + ": " + mOptionList.get(position).totalShaka.toString()

            if (mOptionList.get(position).present != null) {
                holder.binding.txtPresent.text =
                    activity.resources.getString(R.string.presence) + ": " + mOptionList.get(position).present.toString()
            } else {
                holder.binding.txtPresent.text =
                    activity.resources.getString(R.string.presence) + ": " + "00".toString()
            }


            if (position.toString().endsWith("0"))
                holder.binding.imageView.setImageResource(R.drawable.team1)
            if (position.toString().endsWith("1"))
                holder.binding.imageView.setImageResource(R.drawable.team2)
            if (position.toString().endsWith("2"))
                holder.binding.imageView.setImageResource(R.drawable.team3)
            if (position.toString().endsWith("3"))
                holder.binding.imageView.setImageResource(R.drawable.team4)
            if (position.toString().endsWith("4"))
                holder.binding.imageView.setImageResource(R.drawable.team5)
            if (position.toString().endsWith("5"))
                holder.binding.imageView.setImageResource(R.drawable.unity)
            if (position.toString().endsWith("6"))
                holder.binding.imageView.setImageResource(R.drawable.unity_icon)
            if (position.toString().endsWith("7"))
                holder.binding.imageView.setImageResource(R.drawable.team1)
            if (position.toString().endsWith("8"))
                holder.binding.imageView.setImageResource(R.drawable.team4)
            if (position.toString().endsWith("9"))
                holder.binding.imageView.setImageResource(R.drawable.team5)

            var finalFlag = ""

            if (mOptionList.get(position).flag != null)
                finalFlag = mOptionList.get(position).flag
            else if (mOptionList.get(position).flage != null)
                finalFlag = mOptionList.get(position).flage

            holder.binding.linearLayout.setOnClickListener {
                if (finalFlag == "S") {

                    listener.onContainerClick(
                        mOptionList.get(position).id,
                        finalFlag, true
                    )

//                    val mainIntent = Intent(context, ShakaLisActivity::class.java)
//                    activity!!.startActivityForResult(mainIntent,REQUEST_CODE)

                } else  {
                    listener.onContainerClick(
                        mOptionList.get(position).id,
                        finalFlag,
                        false
                    )

                }
            }

            Log.e("@@@@@@@@@", "onBindViewHolder: "+flagId )
            if (flagId.equals("1")) {
                holder.binding.nkk.visibility = View.VISIBLE
            } else {
                holder.binding.nkk.visibility = View.GONE
            }

            holder.binding.nkk.setOnClickListener {
                context.startActivity(Intent(context, NkkListActivity::class.java))
            }

        } catch (e: Exception) {

        }



    }

    fun setData(mOptionList: ArrayList<DashboardNewListModel>) {
        this.mOptionList = mOptionList
        notifyDataSetChanged()
    }

    fun setFlagId(flag_id: String) {
        this.flagId =flag_id.toString()
    }

    fun updateList(list: ArrayList<DashboardNewListModel>) {
        mOptionList = list
        notifyDataSetChanged()
    }


    private fun getItem(index: Int): String {
        return mOptionList[index].name.toString()
    }

    override fun getItemCount(): Int {
        return mOptionList.size
    }


    inner class ViewHolder(val binding: AdapterDashboardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        override fun onClick(v: View?) {
            ////
        }
    }

    fun bind(photo: ShakaUser) {

    }

    interface MyAdapterListener {
        fun onContainerClick(flag_id: Int, flagType: String, isChangeApi: Boolean)
    }
}