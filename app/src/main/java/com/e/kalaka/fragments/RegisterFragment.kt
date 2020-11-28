package com.e.kalaka.fragments

import android.content.Intent
import android.opengl.ETC1.isValid
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.e.kalaka.R
import com.e.kalaka.databinding.FragmentRegisterBinding
import com.e.kalaka.utils.Validation
import com.google.firebase.auth.FirebaseAuth


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var mAuth: FirebaseAuth


    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)




        binding.chooseImageButton.setOnClickListener {
            pickImageFromGallery()

        }

        binding.saveButton.setOnClickListener {
            val lastName = binding.lastNameEditText.text.toString()
            val firstName = binding.firstNameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            // val image = binding.imageView.

            Log.d("helo", "Email : $email")
            Log.d("helo", "password : $password")
            if (!registrationValidation(lastName, firstName, email, password))
                return@setOnClickListener
            registerUserInDataBase(email, password)
        }

        binding.gotoLoginButton.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_registerFragment_to_loginFragment)
        }

        return binding.root

    }

    private fun registerUserInDataBase(email: String, password: String) {
        val navController = Navigation.findNavController(binding.root);
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                // let the user know that the registration was successful
                Log.d("Helo", "itt vagy ? ")

                if (task.isSuccessful) {
                    Log.d("Helo", "successfull")

                    Toast.makeText(
                        activity,
                        "User created",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.navigate(R.id.homeFragment)
                } else {
                    Log.d("Helo", task.exception.toString())
                    Toast.makeText(
                        activity,
                        "Error !" + task.exception.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun registrationValidation(
        lastName: String,
        firstName: String,
        email: String,
        password: String
    ): Boolean {
        if (TextUtils.isEmpty(lastName)) {
            binding.lastNameEditText.error = "Vezetéknév hiányzik"
            return false
        }

        if (TextUtils.isEmpty(firstName)) {
            binding.firstNameEditText.error = "Keresztnév hiányzik"
            return false
        }

        if (TextUtils.isEmpty(email)) {
            binding.emailEditText.error = "Email Hiányzik"
            return false
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordEditText.error = "Jelszó hiányzik"
            return false
        }

        if (lastName.length >= 12) {
            binding.lastNameEditText.error = "Vezetéknév túl hosszú"
            return false
        }
        val validation = Validation()
        if (!validation.isValidEmail(email)) {
            binding.emailEditText.error = "Email nem helyes"
            return false
        }

        if (password.length < 6) {
            binding.passwordEditText.error = "Jelszó legalább 6 karakter kell legyen"
            return false
        }


        return true
    }
}