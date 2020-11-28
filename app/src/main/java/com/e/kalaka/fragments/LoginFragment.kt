package com.e.kalaka.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.e.kalaka.R
import com.e.kalaka.activities.MainActivity
import com.e.kalaka.databinding.FragmentLoginBinding
import com.e.kalaka.utils.Validation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        binding.saveButton.setOnClickListener {
            mAuth = FirebaseAuth.getInstance();
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (!isValidLoginFields(email, password))
                return@setOnClickListener


            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            context, "Sikeres bejelentkezés",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Sign in success, update UI with the signed-in user's information
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.action_loginFragment_to_homeFragment)
                    } else {
                        Toast.makeText(
                            context, "Sikertelen bejelentkezés.",
                            Toast.LENGTH_SHORT
                        ).show()
                        // If sign in fails, display a message to the user.
                        Log.w("Helo", "signInWithEmail:failure", task.exception)
                    }
                }
        }

        binding.gotoRegisterButton.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_loginFragment_to_registerFragment)
        }


        return binding.root
    }

    private fun isValidLoginFields(email: String, password: String): Boolean {
        if (TextUtils.isEmpty(email)) {
            binding.emailEditText.error = "Email Hiányzik"
            return false
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordEditText.error = "Jelszó hiányzik"
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