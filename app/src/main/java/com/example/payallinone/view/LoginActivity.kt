package com.example.payallinone.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.example.payallinone.R
import com.example.payallinone.base.BaseActivity
import com.example.payallinone.constants.spConstants
import com.example.payallinone.databinding.ActivityLoginBinding
import com.example.payallinone.utils.isNetworkAvailable

class LoginActivity : BaseActivity() {
    private lateinit var binding:ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }
    private fun initViews() {   try {
        onBackPressedDispatcher.addCallback(this,object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
        with(binding){
            textView.setOnClickListener {
                startActivity(Intent(
                    this@LoginActivity,
                    DashboardActivity::class.java))
            }
            btnLogin.setOnClickListener {
                val getEmail=etname.text.toString()
                val getPass=etpass.text.toString()
                if(getEmail.isNotEmpty() && getPass.isNotEmpty()){
                    if(isNetworkAvailable(this@LoginActivity)){
                        showCustomdialog(this@LoginActivity,"loading")
                        firebaseAuth.signInWithEmailAndPassword(getEmail,getPass)
                            .addOnCompleteListener{
                                if(it.isSuccessful){
                                    dismissdialog()
                                    val getUId=it.result.user?.uid
                                    addDataIntoSP(spConstants.LOGIN_USER_UID,getUId.toString())
                                    toast(this@LoginActivity,getString(R.string.lginsuccess))
                                    startActivity(Intent(this@LoginActivity,DashboardActivity::class.java))
                                }else{
                                    dismissdialog()
                                    snackbar(binding.root,getString(R.string.athfldor),0)
                                }
                            }
                    }else{
                        snackbar(binding.root,getString(R.string.inedinternet),0)
                    }
                }else{
                    toast(this@LoginActivity,getString(R.string.fldcnntempt))
                }
            }
            btnSignup.setOnClickListener{
                startActivity(Intent(this@LoginActivity,RegisterActivity::class.java))
            }
        }
    }catch(e:Exception){
        e.printStackTrace()
    }
    }
    override fun onDestroy() {
        super.onDestroy()
        with(binding){
            btnLogin.setOnClickListener(null)
            btnSignup.setOnClickListener(null)
        }
    }
}