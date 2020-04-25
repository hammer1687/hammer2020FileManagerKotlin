package com.example.filemanagerkotlin2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filemanager_kotlin.FileModel
import com.example.filemanagerkotlin2.Utils.getFileModelsFromFiles
import com.example.filemanagerkotlin2.Utils.getFilesFromPath
import kotlinx.android.synthetic.main.fragment_files_list.*
import java.io.File
import java.lang.Exception

class FilesListFragment: Fragment() {

    private lateinit var mFilesAdapter: FilesRecyclerAdapter
    private lateinit var PATH: String
    private lateinit var mCallback: OnItemClickListener
    private lateinit var mFileChangeBroadcastReceiver: BroadcastReceiver
    private lateinit var mCallback2: OnDeleteItemListener


    interface OnItemClickListener {
        fun onClick(fileModel: FileModel)
        fun onLongClick(fileModel: FileModel)
    }

    interface OnDeleteItemListener {
        fun deleteSelectedItems(selectList: List<FileModel>)
    }


    companion object {
        private const val ARG_PATH: String = "com.example.filemanagerkotlin2.fileslist.path"
        fun build(block: Builder.() -> Unit) = Builder().apply(block).build()

    }

    class Builder {
        var path: String = ""

        fun build(): FilesListFragment {
            val fragment = FilesListFragment()
            val args = Bundle()
            args.putString(ARG_PATH, path)
            fragment.arguments = args;
            return fragment
        }
    }

    //On Attach of Activity to Fragment, make sure that Activity containing context implements the Fragment Class with interface of OnItemClickListener
    override fun onAttach(context: Context) {
        super.onAttach(context)

        Log.i("LOG_TAG", " FilesListFragment onAttach")
        try {
            mCallback = context as OnItemClickListener
            mCallback2 = context as OnDeleteItemListener
        } catch (e: Exception) {
            Log.i("LOG_TAG", "Should implement FilesListFragment")
            throw Exception("${context} should implement FilesListFragment")

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("LOG_TAG", " FilesListFragment onCreateView")
        return inflater.inflate(R.layout.fragment_files_list, container, false)
    }

    override fun onResume() {
        super.onResume()
        context?.registerReceiver(mFileChangeBroadcastReceiver, IntentFilter(getString(R.string.file_change_broadcast)))
        context?.registerReceiver(mFileChangeBroadcastReceiver, IntentFilter(getString(R.string.file_delete_broadcast)))
        Log.i("LOG_TAG", " FilesListFragment onResume")
    }

    override fun onPause() {
        super.onPause()
        context?.unregisterReceiver(mFileChangeBroadcastReceiver)
        Log.i("LOG_TAG", " FilesListFragment onPause")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("LOG_TAG", " FilesListFragment onCreate")

        val filePath = arguments?.getString(ARG_PATH)
        if (filePath == null) {
            Toast.makeText(context, "Path should not be null!", Toast.LENGTH_SHORT).show()
            return
        }
        PATH = filePath

        Log.i("LOG_TAG", " FilesListFragment check")

        mFileChangeBroadcastReceiver = FileChangeBroadcastReceiver(PATH) {
            deleteData()
            updateDate()
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("LOG_TAG", " FilesListFragment onViewCreated")
        initViews()
    }

    //Setup recyvlerView Layout, and adapter
    //The onClickListener is inside Adapter Class but after
    //initialize Adapter onItemClickListener, when Click is detected it can invoke it in Fragment Class
    //updateData is used to load files to the recyclerView by Adapter updateData function
    private fun initViews() {
        filesRecyclerView.layoutManager = LinearLayoutManager(context)
        mFilesAdapter = FilesRecyclerAdapter()
        filesRecyclerView.adapter = mFilesAdapter


        mFilesAdapter.onItemClickListener = {
            Log.i("LOG_TAG", "mFilesAdapter OnItemClickListener")
            mCallback.onClick(it)
        }

        mFilesAdapter.onItemLongClickListener = {
            mCallback.onLongClick(it)
        }

        mFilesAdapter.onDeleteItemListener = {
            mCallback2.deleteSelectedItems(it)
        }
        updateDate() //I think this should be updateData LOL
    }

    private fun updateDate() {
        //val files = getFileModelsFromFiles(getFilesFromPath(PATH))
        val files = getFileModelsFromFiles(getFilesFromPath(PATH))

        if (files.isEmpty()) {
            emptyFolderLayout.visibility = View.VISIBLE
        } else {
            emptyFolderLayout.visibility = View.INVISIBLE
        }
        Log.i("LOG_TAG", "FilesListFragment: updateData")
        mFilesAdapter.updateData(files)
    }

    fun deleteData() {
        if(AppConstants.isDeleting) {
            Log.i("LOG_TAG", "Deleting Items")
            mFilesAdapter.deleteData()
            AppConstants.isDeleting=false
        }

    }





















}