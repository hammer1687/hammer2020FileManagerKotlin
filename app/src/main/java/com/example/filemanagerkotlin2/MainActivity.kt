package com.example.filemanagerkotlin2

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filemanager_kotlin.FileModel
import com.example.filemanagerkotlin2.Utils.BackStackManager
import com.example.filemanagerkotlin2.Utils.FileType
import com.example.filemanagerkotlin2.Utils.createNewFolder
import com.example.filemanagerkotlin2.Utils.launchFileIntent
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_enter_name.view.*

//Declared explicitly because default libraries has deleteFile method
import com.example.filemanagerkotlin2.Utils.deleteFile as FileUtilsDeleteFile

class MainActivity : AppCompatActivity(), FilesListFragment.OnItemClickListener, FilesListFragment.OnDeleteItemListener {


    private val backStackManager = BackStackManager()
    private lateinit var mBreadcrumbRecyclerAdapter: BreadcrumbRecyclerAdapter
    var mActionMode: ActionMode? = null


    //Parameters for Permissions
    val REQUEST_PERMISSIONS: Int = 1234
    var PERMISSIONS_COUNT = 2
    val PERMISSIONS = Array<String> (2) {
        Manifest.permission.READ_EXTERNAL_STORAGE
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    }

    //Method for checking permissions for access of external storage
    @SuppressLint("NewApi")
    fun arePermissionsDenied(): Boolean {
        var p: Int =0
        while (p<PERMISSIONS_COUNT) {
            if (checkSelfPermission(PERMISSIONS[p])!= PackageManager.PERMISSION_GRANTED) {
                return true
            }
            p++
        }
        //False - Permission Granted
        return false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppConstants.initialization(this.applicationContext)

        if (savedInstanceState ==null) {



            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionsDenied()) {
                //Newer apis do not give permissions by default
                //if permissions are denied, we will request permissions
                requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS)
                return
            }



            val filesListFragment = FilesListFragment.build {
                path = Environment.getExternalStorageDirectory().absolutePath
                //path = Environment.getExternalStorageDirectory().absolutePath.toString()
                //path = Environment.getExternalStorageState().toString()
                //path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                Log.i("LOG_TAG", "Initialize at:" + path)
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, filesListFragment)
                .addToBackStack(Environment.getExternalStorageDirectory().absolutePath)
                .commit()
        }

        initBreadcrumbView()
        initBackStack()
    }

    private fun initBackStack() {
        backStackManager.onStackChangeListener = {

            updateAdapterData(it)


        }
        Log.i("LOG_TAG" , "Initial Directory: " + Environment.getExternalStorageDirectory().absoluteFile)
        backStackManager.addToStack(fileModel = FileModel(Environment.getExternalStorageDirectory().absolutePath.toString(), FileType.FOLDER, "STORAGE" , 0.0))

    }

    private fun updateAdapterData(files: List<FileModel>) {
        mBreadcrumbRecyclerAdapter.updateData(files)

        if (files.isNotEmpty()) {
            breadcrumbRecyclerView.smoothScrollToPosition(files.size-1)
        }

    }



    private fun initBreadcrumbView() {
        //setSupportActionBar(toolbar)

        breadcrumbRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBreadcrumbRecyclerAdapter = BreadcrumbRecyclerAdapter()
        breadcrumbRecyclerView.adapter = mBreadcrumbRecyclerAdapter
        mBreadcrumbRecyclerAdapter.onItemClickListener = {

            if (!AppConstants.isContextualModeEnabled) {
                supportFragmentManager.popBackStack(it.path, 2);

                Log.i("LOG_TAG", "check2 " + it.path)
                backStackManager.popFromStackTill(it)
            }
        }
    }




    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_termsOfService -> TermsOfServiceActivity.start(this)
            R.id.action_settings -> SettingsActivity.start(this)
            //R.id.menuNewFile -> createNewFileInCurrentDirectory()
            R.id.action_newFolder -> createNewFolderInCurrentDirectory()



        }
        return super.onOptionsItemSelected(item)

    }

    private fun createNewFolderInCurrentDirectory() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_enter_name, null)
        builder.setView(view)
        val dialog = builder.create()
        view.createButton.setOnClickListener(View.OnClickListener {

            val fileName = view.nameEditText.text.toString()
            Log.i("LOG_TAG", "Create New Folder")
            if(fileName.isNotEmpty()) {
                createNewFolder(fileName, backStackManager.top.path) {_, message ->
                    updateContentOfCurrentFragment()
                    dialog.cancel()
                }
            }

        })
        dialog.show()

    }

    private fun updateContentOfCurrentFragment() {
        val broadcastIntent = Intent()
        broadcastIntent.action = applicationContext.getString(R.string.file_change_broadcast)
        broadcastIntent.putExtra(FileChangeBroadcastReceiver.EXTRA_PATH, backStackManager.top.path)
        sendBroadcast(broadcastIntent)
    }


    private fun addFileFragment(fileModel: FileModel) {

        Log.i ("LOG_TAG", "Add File Fragment")
        val filesListFragment = FilesListFragment.build {
            path = fileModel.path
        }
        backStackManager.addToStack(fileModel)


        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, filesListFragment)
        fragmentTransaction.addToBackStack(fileModel.path)
        fragmentTransaction.commit()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        backStackManager.popFromStack()
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }

    override fun onClick(fileModel: FileModel) {

        Log.i("LOG_TAG", "onClick")
        if(fileModel.fileType == FileType.FOLDER) {
            //backStackManager.addToStack(fileModel)
            addFileFragment(fileModel)
        } else {

            launchFileIntent(fileModel)
        }
    }

    override fun onLongClick(fileModel: FileModel) {

        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show()
        if (mActionMode ==null) mActionMode =startSupportActionMode(mActionModeCallback())
        
    }

    inner class mActionModeCallback: ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            Log.i("LOG_TAG", "MainActivity: onActionItemClicked")
            when(item!!.itemId){
                R.id.action_delete -> {
                    AppConstants.isDeleting =true
                    deleteItemsInCurrentFragment()

                    mode!!.finish()
                    return true
                }
                R.id.action_select_all -> {
                    if(!AppConstants.isSelectAllEnabled) {
                        AppConstants.isSelectAllEnabled = true

                    } else {
                        mode!!.finish()
                    }
                    updateContentOfCurrentFragment()
                    return true
                }

                R.id.action_copy -> {
                    
                }
            }

            return true
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {

            Log.i("LOG_TAG", "MainActivity: onCreateActionMode")
            mode!!.menuInflater.inflate(R.menu .context_menu, menu)
            mode!!.title = "Edit Menu"
            AppConstants.isContextualModeEnabled=true
            updateContentOfCurrentFragment()
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            Log.i("LOG_TAG", "MainActivity: onPrepareActionMode")
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            Log.i("LOG_TAG", "MainActivity: onDestroyActionMode")
            AppConstants.isContextualModeEnabled=false
            AppConstants.isSelectAllEnabled = false
            updateContentOfCurrentFragment()

            mActionMode = null
        }
    }


    fun deleteItemsInCurrentFragment() {
        val broadcastIntent = Intent()
        broadcastIntent.action = applicationContext.getString(R.string.file_delete_broadcast)
        broadcastIntent.putExtra(FileChangeBroadcastReceiver.EXTRA_PATH, backStackManager.top.path)
        sendBroadcast(broadcastIntent)

    }


    override fun deleteSelectedItems(selectList: List<FileModel>) {
        Log.i("LOG_TAG", "MainActivity: deleteSelectedItems")

        for(i in 0 until selectList.size) {
            FileUtilsDeleteFile(selectList[i].path)
        }



    }
}
