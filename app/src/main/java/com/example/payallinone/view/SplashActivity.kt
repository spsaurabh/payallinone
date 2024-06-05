package com.example.payallinone.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import com.example.payallinone.R
import com.example.payallinone.base.BaseActivity
import com.example.payallinone.constants.spConstants
import com.example.payallinone.databinding.ActivitySplashBinding
import com.google.firebase.FirebaseApp

class SplashActivity : BaseActivity() {
    private lateinit var binding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        initAnimation()
    }
    private fun initAnimation() {   try {
        val imageAnimation = AnimationUtils.loadAnimation(this, R.anim.toptocenter)
        binding.ivLogo.animation = imageAnimation
        val textAnimation = AnimationUtils.loadAnimation(this, R.anim.bottomtocenter)
        binding.tvText.animation = textAnimation
        Handler(Looper.getMainLooper()).postDelayed({
              if(getDataFromSP(spConstants.LOGIN_USER_UID,"")!!.isEmpty()){
                    startActivity(Intent(this,LoginActivity::class.java))
                    finish()
              }else{
                    startActivity(Intent(this,DashboardActivity::class.java))
                    finish()
              }
        }, 3000)
    }catch(e:Exception){
        e.printStackTrace()
    }
    }

}