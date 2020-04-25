package com.example.filemanagerkotlin2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanager_kotlin.FileModel
import kotlinx.android.synthetic.main.item_recycler_file.view.*


class BreadcrumbRecyclerAdapter: RecyclerView.Adapter<BreadcrumbRecyclerAdapter.ViewHolder>() {

    var onItemClickListener: ((FileModel) -> Unit)? = null
    var files = listOf<FileModel>()


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onItemClickListener?.invoke(files[adapterPosition])
        }

        fun bindView(position: Int) {
            val file = files[position]
            itemView.nameTextView.text = file.name
        }
    }

    fun updateData(files: List<FileModel>) {
        this.files = files

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_breadcrumb, parent ,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = files.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(position)
    }


}