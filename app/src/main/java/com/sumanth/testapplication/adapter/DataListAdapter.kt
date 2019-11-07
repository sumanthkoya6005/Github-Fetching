package com.sumanth.testapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sumanth.testapplication.R
import com.sumanth.testapplication.interfaces.DataListener
import com.sumanth.testapplication.model.RepoList
import com.sumanth.testapplication.model.User


class DataListAdapter(
    private var data: List<User>,
    private var repoList: ArrayList<ArrayList<RepoList>>?
) :
    RecyclerView.Adapter<DataListAdapter.MyViewHolder>(), Filterable {
    private var listFiltered: List<User>? = null
    private lateinit var dataSelectedListener: DataListener
    fun setDataListener(dataListener: DataListener) {
        this.dataSelectedListener = dataListener
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: AppCompatTextView =
            view.findViewById(R.id.label_name)
        var repoCount: AppCompatTextView =
            view.findViewById(R.id.label_repo_count)
        var image: ImageView = view.findViewById(R.id.image_people)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val topics = listFiltered?.get(position)
        holder.title.text = topics?.login
        if (topics?.repoList != null)
            holder.repoCount.text = topics.repoList!!.size.toString()
        else
            holder.repoCount.text = "0"
        if (topics?.avatar_url.isNullOrEmpty())
            holder.image.setImageResource(R.drawable.ic_launcher_background)
        else
            topics?.avatar_url?.let { loadImage(holder.image, it) }
        holder.itemView.setOnClickListener {
            dataSelectedListener.onSelected(topics)
        }
    }

    private fun loadImage(view: ImageView, imageUrl: String) {
        val mDefaultBackground =
            view.context.resources.getDrawable(R.drawable.ic_menu_gallery)

        Glide.with(view.context)
            ?.load(imageUrl)
            ?.centerCrop()
            ?.error(mDefaultBackground)?.into(view)
    }

    fun update(data: List<User>, repoCompleteList: ArrayList<ArrayList<RepoList>>?) {
        this.data = data
        this.repoList = repoCompleteList
        this.listFiltered = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listFiltered!!.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                listFiltered = if (charString.isEmpty()) {
                    data
                } else {
                    val filteredList = ArrayList<User>()
                    for (row in data) {
                        if (row.login.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }

                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = listFiltered
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                listFiltered = filterResults.values as List<User>
                notifyDataSetChanged()
            }
        }
    }
}
