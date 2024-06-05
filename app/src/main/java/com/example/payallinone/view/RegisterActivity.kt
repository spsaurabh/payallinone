package com.example.payallinone.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.payallinone.R
import com.example.payallinone.base.BaseActivity
import com.example.payallinone.constants.spConstants
import com.example.payallinone.databinding.ActivityRegisterBinding
import com.example.payallinone.utils.isNetworkAvailable
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class RegisterActivity : BaseActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityRegisterBinding
    private val locationPermissionCode = 1000
    var isValidEmail = false
    var currentUserUID = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient=LocationServices.getFusedLocationProviderClient(this)
        initviews()
    }
    private fun initviews() {
        binding.etemail.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(validateEmail(p0.toString())){
                    binding.tventrvldeml.visibility= View.GONE
                }else if(p0.toString().isEmpty()){
                    binding.tventrvldeml.visibility= View.GONE
                }else{
                    binding.tventrvldeml.visibility= View.VISIBLE
                }
            }
            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.btnSignup.setOnClickListener {
            if(checkValidation()){
                if(isNetworkAvailable(this@RegisterActivity)){
                    showCustomdialog(this,"loading")
                    addDataToDatabase()
                }else{
                    snackbar(binding.root,getString(R.string.inedinternet),0)
                }
            }
        }
        binding.tvusecurrlocation.setOnClickListener {try {
            binding.etAddress.text.clear()
            getlocation()
        }catch(e:Exception){
            e.printStackTrace()
        }
        }
    }
    private fun validateEmail(email:String):Boolean {
        val emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        if(email.matches(emailPattern.toRegex())){
            return true
        }
        return false
    }
    private fun addDataToDatabase() {
        val email=binding.etemail.text.toString()
        val pass=binding.etpasswrod.text.toString()
        val name=binding.etname.text.toString()
        val mobile=binding.etmobile.text.toString()
        val address=binding.etAddress.text.toString()
        val username=binding.etUsername.text.toString()

        firebaseAuth.createUserWithEmailAndPassword(email,pass)
            .addOnCompleteListener(this){
                if(it.isSuccessful){
                    dismissdialog()
                    val user = firebaseAuth.currentUser
                    user.let {
                        currentUserUID=it?.uid.toString()
                        val userMap = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "mobile" to mobile,
                        "address" to address,
                        "username" to username
                        )
                        dbFirestore.collection("Users").document(currentUserUID)
                            .set(userMap)
                            .addOnSuccessListener {
                                dismissdialog()
                                addDataIntoSP(spConstants.LOGIN_USER_UID,currentUserUID)
                                toast(this,"Signed Up")
                                startActivity(Intent(this,DashboardActivity::class.java))
                            }
                            .addOnFailureListener {
                                dismissdialog()
                                toast(this,"Signup Failed")
                            }
                    }
                }else{
                    dismissdialog()
                    toast(this,"Signup Failed")
                }
            }
    }
    private fun checkValidation():Boolean {
        if(binding.etname.text.toString().isEmpty()){
            toast(this,getString(R.string.entrname))
            return false
        }else if(binding.etemail.text.toString().isEmpty()){
            toast(this,getString(R.string.entremail))
            return false
        }else if(binding.etmobile.text.toString().isEmpty()){
            toast(this,getString(R.string.entrmobile))
            return false
        }else if(binding.etAddress.text.toString().isEmpty()){
            toast(this,getString(R.string.entraddress))
            return false
        }else if(binding.etUsername.text.toString().isEmpty()){
            toast(this,getString(R.string.entrusrnm))
            return false
        }else if(binding.etpasswrod.text.toString().isEmpty()){
            toast(this,getString(R.string.entrpass))
            return false
        }
        return true
    }
    private fun getlocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(
                this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
                ),locationPermissionCode)
        }else{
            try {
                fusedLocationClient.lastLocation.addOnCompleteListener(OnCompleteListener {task->
                    if(task.isSuccessful && task.result != null){
                        val location: Location = task.result
                        val lat = location.latitude
                        val longi = location.longitude
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(lat,longi, 1)
                        if(addresses!!.isNotEmpty()){
                            val address = addresses[0].getAddressLine(0)
                            binding.etAddress.setText(address)
                        }else{
                            binding.etAddress.setText(R.string.addrntfound)
                        }
                    }else{
                        toast(this,getString(R.string.addrntfound))
                    }
                })
            }catch(e:Exception){
                e.printStackTrace()
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode){
            if((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                getlocation()
            }else{
                toast(this,"Location Denied")
            }
        }
    }
}