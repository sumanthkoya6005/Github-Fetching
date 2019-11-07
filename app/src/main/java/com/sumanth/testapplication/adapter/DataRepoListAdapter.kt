package com.sumanth.testapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.sumanth.testapplication.interfaces.DataListener
import com.sumanth.testapplication.model.RepoList


class DataRepoListAdapter(
    private var data: List<RepoList>
) :
    RecyclerView.Adapter<DataRepoListAdapter.MyViewHolder>(), Filterable {
    private var listFiltered: List<RepoList>? = null
    private lateinit var dataSelectedListener: DataListener
    private lateinit var context:Context
    fun setDataListener(dataListener: DataListener) {
        this.dataSelectedListener = dataListener
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: AppCompatTextView =
            view.findViewById(com.sumanth.testapplication.R.id.label_name)
        var repoCount: AppCompatTextView =
            view.findViewById(com.sumanth.testapplication.R.id.label_stars_count)
        var forkCount: AppCompatTextView =
            view.findViewById(com.sumanth.testapplication.R.id.label_forks_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        this.context=parent.context
        val itemView = LayoutInflater.from(parent.context)
            .inflate(com.sumanth.testapplication.R.layout.item_user_repo, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val topics = listFiltered?.get(position)
        holder.title.text = topics?.name
        holder.repoCount.text = String.format(context.getString(com.sumanth.testapplication.R.string.user_stars), topics?.stargazers_count.toString())
        holder.forkCount.text = String.format(context.getString(com.sumanth.testapplication.R.string.user_fork), topics?.forks_count.toString())
        holder.itemView.setOnClickListener {
        }
    }

    fun update(data: List<RepoList>) {
        this.data = data
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
                    var filteredList = ArrayList<RepoList>()
                    for (row in data) {
                        if (row.name.toLowerCase().contains(charString.toLowerCase())) {
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
                listFiltered = filterResults.values as List<RepoList>
                notifyDataSetChanged()
            }
        }
    }
}
