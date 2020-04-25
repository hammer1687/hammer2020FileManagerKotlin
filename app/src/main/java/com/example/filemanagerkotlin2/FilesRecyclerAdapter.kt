package com.example.filemanagerkotlin2

import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanager_kotlin.FileModel
import com.example.filemanagerkotlin2.Utils.FileType
import kotlinx.android.synthetic.main.item_recycler_file.view.*

class FilesRecyclerAdapter: RecyclerView.Adapter<FilesRecyclerAdapter.ViewHolder>() {

    var onItemClickListener: ((FileModel) -> Unit)? = null
    var onItemLongClickListener: ((FileModel) -> Unit)? = null
    var onDeleteItemListener: ((List<FileModel>) -> Unit)? = null

    var filesList = listOf<FileModel>()
    var selectList: MutableList<FileModel> = ArrayList()
    var itemStateArray: HashMap<Int, Boolean> = HashMap()

    var longClickPosition: Int = 0

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener  {

        var mCheckBox: CheckBox

        init {
            mCheckBox = itemView.findViewById<CheckBox>(R.id.checkBox)
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }
        override fun onClick(v: View?) {

            Log.i("LOG_TAG", "FilesRecyclerAdapter: onClick")
            if(AppConstants.isContextualModeEnabled) {
                makeSelection(adapterPosition)
            } else {
                onItemClickListener?.invoke(filesList[adapterPosition])
            }
        }

        override fun onLongClick(v: View?): Boolean {

            Log.i("LOG_TAG", "FilesRecyclerAdapter: onLongClick")
            itemStateArray.set(adapterPosition, true)
            longClickPosition = adapterPosition
            onItemLongClickListener?.invoke(filesList[adapterPosition])

            return true
        }

        fun makeSelection(position: Int) {
            if (AppConstants.isContextualModeEnabled) {

                if (mCheckBox.isChecked) {
                    itemStateArray.set(position,false)
                    mCheckBox.isChecked=false
                } else {
                    itemStateArray.set(position,true)
                    mCheckBox.isChecked=true
                }

            }
        }


        fun selectAll(position: Int) {
            if (AppConstants.isSelectAllEnabled) {
                itemStateArray.set(position, true)
                Log.i(
                    "LOG_TAG",
                    "Position " + position + " is " + itemStateArray.getOrElse(position) { false })
                mCheckBox.isChecked = true
            } else {
                itemStateArray.set(longClickPosition, true)
            }
        }

        fun showCheckbox() {
            if (AppConstants.isContextualModeEnabled) {
                mCheckBox.visibility = View.VISIBLE
            } else {
                mCheckBox.visibility = View.GONE
            }

        }



        fun bindView(position: Int) {
            val fileModel = filesList[position]
            itemView.nameTextView.text = fileModel.name

            if (fileModel.fileType == FileType.FOLDER) {
                itemView.folderTextView.visibility = View.VISIBLE
                itemView.totalSizeTextView.visibility = View.GONE
                itemView.folderTextView.text = "(${fileModel.subFiles} files)"
            } else {
                itemView.folderTextView.visibility = View.GONE
                itemView.totalSizeTextView.visibility = View.VISIBLE
                itemView.totalSizeTextView.text ="${String.format("%.2f", fileModel.sizeInMB)} mb"
            }

            if(AppConstants.isContextualModeEnabled && !AppConstants.isSelectAllEnabled) {
                mCheckBox.isChecked = itemStateArray.getOrElse(position) {false} == true
            }



        }
    }


    //Inflate the layout for each item of the recyclerView to view
    //Then call inner ViewHolder Class and pass in the view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_file, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filesList.size
    }

    //call inner ViewHolder Class bindView function to handle how to present the data for each elements of each item of recyclerView
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.selectAll(position)
        holder.showCheckbox()
        holder.bindView(position)


    }

    //For notifying if data is changed and automatically calls the onCreate, onBindViewHolder, getItemCount members
    fun updateData(filesList: List<FileModel>) {
        this.filesList = filesList
        dataClear()
        notifyDataSetChanged()
        }

    fun dataClear() {
        Log.i("LOG_TAG", "FilesRecyclerAdapter: dataClear")
        itemStateArray.clear()
    }


    fun deleteData() {
        getSelectedItems()
        onDeleteItemListener?.invoke(selectList)

    }

    fun getSelectedItems() {
        Log.i("LOG_TAG", "FilesRecyclerAdapter: getSelectedItems")
        if(selectList ==null) {

        } else {
            selectList!!.clear()
        }
        for(i in 0 until filesList.size) {
            if(itemStateArray.get(i) == true) {
                selectList.add(filesList[i])
                Log.i("LOG_TAG", "selectList 0: " + filesList[i].name)
            }
        }
    }

}



