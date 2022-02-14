 package com.example.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.login.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

 class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding : ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        mainBinding= ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)

        // Auth
        auth = FirebaseAuth.getInstance()

        // Click
        mainBinding.btnLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
        mainBinding.btnRegister.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }

    }

     override fun onStart() {
         super.onStart()
         if (auth.currentUser != null){
             Intent(this,HomeActivity::class.java).also {
                 it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                 startActivity(it)
             }
         }
     }
}