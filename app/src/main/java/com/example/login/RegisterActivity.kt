package com.example.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.login.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable

@SuppressLint("CheckResult")
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Auth
        auth = FirebaseAuth.getInstance()

        // Fullname Validation
        val nameStream = RxTextView.textChanges(binding.etFullname)
            .skipInitialValue()
            .map { name ->
                name.isEmpty()
            }
        nameStream.subscribe{
            showNameExistAlert(it)
        }


        // Email Validation
        val emailStream = RxTextView.textChanges(binding.etEmail)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        emailStream.subscribe{
            showEmailValidAlert(it)
        }

        // Username Validation
        val usernameStream = RxTextView.textChanges(binding.etUsername)
            .skipInitialValue()
            .map { username ->
                username.length < 6
            }
        usernameStream.subscribe{
            showTextMinimalAlert(it,"Username")
        }

        // Password Validation
        val passwordStream = RxTextView.textChanges(binding.etPassword)
            .skipInitialValue()
            .map { password ->
                password.length < 6
            }
        passwordStream.subscribe{
            showTextMinimalAlert(it,"Password")
        }

        // Confirm Password Validation
        val passwordConfirmStream = Observable.merge(
            RxTextView.textChanges(binding.etPassword)
                .skipInitialValue()
                .map { password ->
                    password.toString() != binding.etConfirmPassword.text.toString()
                },
            RxTextView.textChanges(binding.etConfirmPassword)
                .skipInitialValue()
                .map { confirmPassword ->
                    confirmPassword.toString() != binding.etPassword.text.toString()
                })
        passwordConfirmStream.subscribe {
            showConfirmPasswordAlert(it)
        }

        // Button Enable True or False
        val invalidFieldsStream = Observable.combineLatest(
            nameStream,
            emailStream,
            usernameStream,
            passwordStream,
            passwordConfirmStream
        ) { nameInvalid: Boolean, usernameInvalid: Boolean, emailInvalid: Boolean, passwordInvalid: Boolean, passwordConfirmInvalid: Boolean ->
            !nameInvalid && !usernameInvalid && !emailInvalid && !passwordInvalid && !passwordConfirmInvalid
        }
        invalidFieldsStream.subscribe { isValid ->
            if (isValid) {
                binding.btnRegister.isEnabled = true
                binding.btnRegister.backgroundTintList = ContextCompat.getColorStateList(this,R.color.primary_color)
            }else{
                binding.btnRegister.isEnabled = false
                binding.btnRegister.backgroundTintList = ContextCompat.getColorStateList(this,android.R.color.darker_gray)
            }
        }


        // Click
        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            registerUser(email,password)
        }

        binding.tvHaveAccount.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }

    private fun showNameExistAlert(isNotValid:Boolean){
        binding.etFullname.error = if (isNotValid) "Nama tidak boleh kosong!" else null
    }

    private fun showTextMinimalAlert(isNotValid: Boolean, text : String){
        if (text == "Username")
            binding.etUsername.error = if (isNotValid) "$text harus lebih dari 6 huruf" else null
        else if (text == "Password")
            binding.etPassword.error = if (isNotValid) "$text harus lebih dari 8 huruf!" else null
    }

    private fun showEmailValidAlert(isNotValid: Boolean){
        binding.etEmail.error = if (isNotValid) "Email tidak valid!" else null
    }

    private fun showConfirmPasswordAlert(isNotValid: Boolean){
        binding.etConfirmPassword.error = if (isNotValid) "Password tidak sama!" else null
    }

    private fun registerUser(email : String,password : String){
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){
                if (it.isSuccessful) {
                    startActivity(Intent(this,LoginActivity::class.java))
                    Toast.makeText(this,"Register Berhasil",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this,it.exception?.message,Toast.LENGTH_SHORT).show()
                }

            }
    }

}