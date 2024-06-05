package com.example.payallinone.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.payallinone.R
import com.example.payallinone.base.BaseActivity
import com.example.payallinone.constants.Constants
import com.example.payallinone.databinding.ActivityDashboardBinding
import com.example.payallinone.utils.isNetworkAvailable
import com.example.payallinone.viewmodel.MainViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DashboardActivity : BaseActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding:ActivityDashboardBinding
    private var apiJob: Job?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainViewModel=ViewModelProvider(this)[MainViewModel::class.java]
        initviews()
    }
    private fun initviews() {
        with(binding){
            ivOptoins.setOnClickListener {
                if(dLayout.isOpen){
                    dLayout.close()
                }else{
                    dLayout.open()
                }
            }
            val navigationView: NavigationView = findViewById(R.id.navigation_view)
            navigationView.setNavigationItemSelectedListener{menuItem->
                when(menuItem.itemId){
                    R.id.lgout->{
                        showCustomdialog(this@DashboardActivity,"logoutCustom")
                        dLayout.close()
                        true
                    }
                    else->false
                }
            }
            logoutDialogClickOk.observe(this@DashboardActivity, Observer {
                if(it){
                    showCustomdialog(this@DashboardActivity,"loading")
                    val editor: SharedPreferences.Editor = sharedPref.edit()
                    editor.clear()
                    editor.apply()
                    dismissdialog()
                    finish()
                    startActivity(Intent(this@DashboardActivity,LoginActivity::class.java))
                }
            })
            logoutDialogClickCancel.observe(this@DashboardActivity, Observer {

            })
            ivSync.setOnClickListener {
                if(isNetworkAvailable(this@DashboardActivity)){
                    hitHomeApi()
                }else{
                    snackbar(binding.root,getString(R.string.inedinternet),0)
                }
            }
        }
        //handle Api Success
        mainViewModel.apiObj.observe(this, Observer {
            if(!it.isEmpty){
                snackbar(binding.root,"${it["MESSAGE"]}",0)
            }else{
                snackbar(binding.root,getString(R.string.msg_pls_try_ltr),0)
            }
        })
        //handle Api Failure
        mainViewModel.isApiFailure.observe(this, Observer {
            if(it){
                snackbar(binding.root,getString(R.string.msg_pls_try_ltr),0)
            }
        })
        onBackPressedDispatcher.addCallback(this,object:OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }
    private fun hitHomeApi() {
        apiJob=CoroutineScope(Dispatchers.IO).launch {
            mainViewModel.hitHomePageApi(Constants.API_HOME)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        with(binding){
            ivSync.setOnClickListener(null)
        }
        apiJob?.cancel()
    }
}