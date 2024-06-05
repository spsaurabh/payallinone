package com.example.payallinone.base

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.payallinone.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

abstract class BaseActivity:AppCompatActivity() {
    lateinit var dbFirestore: FirebaseFirestore
    lateinit var firebaseAuth: FirebaseAuth
    private var dialog: AlertDialog? = null
    lateinit var sharedPref: SharedPreferences
    val logoutDialogClickOk = MutableLiveData<Boolean>()
    val logoutDialogClickCancel = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth=FirebaseAuth.getInstance()
        dbFirestore= FirebaseFirestore.getInstance()
        sharedPref=getSharedPreferences("LoginPreference",Context.MODE_PRIVATE)
    }
    fun addDataIntoSP(key:String,value:String){ try {
        val editor=sharedPref.edit()
        editor.putString(key,value)
        editor.apply()
    }catch(e:Exception){
        e.printStackTrace()
    }
    }
    fun getDataFromSP(key: String,value: String):String?{
        val getUID=sharedPref.getString(key,value)
        return getUID
    }
    fun toast(context:Context, msg:String){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show()
    }
    fun snackbar(view:View,msg:String,lnth:Int){
        Snackbar.make(view,msg,lnth).show()
    }
    fun showCustomdialog(context: Context,type:String) {
        // adding ALERT Dialog builder object and passing activity as parameter
        runOnUiThread {
            if (dialog==null){
                val builder = AlertDialog.Builder(context)
                // layoutinflater object and use activity to get layout inflater
                val inflater: LayoutInflater = layoutInflater
                val view : View
                if(type.equals("loading")){
                    view = inflater.inflate(R.layout.progresscustom,null)
                    builder.setView(view)
                }else if(type.equals("logoutCustom")){
                    view = inflater.inflate(R.layout.logoutcustomdialog,null)
                    builder.setView(view)
                    val positiveButton: Button = view.findViewById(R.id.btnOk)
                    val negativeButton: Button = view.findViewById(R.id.btnCancel)
                    positiveButton.setOnClickListener {
                        logoutDialogClickOk.value=true
                        dismissdialog()
                    }
                    negativeButton.setOnClickListener {
                        logoutDialogClickCancel.value=true
                        dismissdialog()
                    }
                }
                builder.setCancelable(false)
                dialog = builder.create()
            }
            dialog?.show()
        }
    }
    fun dismissdialog() {
        runOnUiThread {
            dialog?.dismiss()
            dialog=null
        }
    }
}