package com.e.kalaka.fragments

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.e.kalaka.R
import com.e.kalaka.databinding.FragmentLoginBinding
import com.e.kalaka.models.User
import com.e.kalaka.utils.Validation
import com.e.kalaka.viewModels.PreloadViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.concurrent.timerTask


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userID: String
    private val preloadedData: PreloadViewModel by activityViewModels()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.circularProgress.visibility = View.GONE

        //the user cant go back to the profile fragment after the logout
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        //Set onclick listener for login button
        binding.saveButton.setOnClickListener {
            mAuth = FirebaseAuth.getInstance();

            //get sharedPref
            sharedPreferences = requireContext().getSharedPreferences("credentials", Context.MODE_PRIVATE)


            //get input data from fields
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            //validate data
            if (!isValidLoginFields(email, password))
                return@setOnClickListener

            binding.circularProgress.visibility = View.VISIBLE
            //trying to sing-in
            login(email,password)
        }

        //navigate to the register fragment
        binding.gotoRegisterButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
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

    private fun login(email: String, password: String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                //if successful, load the user data into preloadedData
                if (task.isSuccessful) {
                    Toast.makeText(context, "Sikeres bejelentkezés", Toast.LENGTH_SHORT).show()

                    //save to shared pref
                    val edit = sharedPreferences.edit()
                    edit.clear()
                    edit.putString("email", email)
                    edit.putString("password", password)
                    edit.apply()

                    //observe the preloaded user
                    preloadedData.user.observe(viewLifecycleOwner, Observer { user ->
                        if (user != null)
                        {
                            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                        }
                        else
                        {
                            mAuth.signOut()
                            Toast.makeText(requireContext(),"Sikertelen bejelentkezés",Toast.LENGTH_LONG).show()
                        }

                    })
                    loadUserData()

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


    private fun loadUserData(){
        database = FirebaseDatabase.getInstance()
        databaseRef = database.getReference("users")
        firebaseAuth = FirebaseAuth.getInstance()
        userID = firebaseAuth.currentUser?.uid.toString()

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val user = dataSnapshot.child(userID)
                Log.d("user", "val: $user")

                val newUser = User(
                    "0",
                    user.child("email").value.toString(),
                    mutableListOf(),
                    user.child("firstName").value.toString(),
                    user.child("userId").value.toString(),
                    user.child("lastName").value.toString(),
                    mutableListOf(),
                    user.child("photoURL").value.toString()
                )

                //load user data into preloadedData
                preloadedData.user.value = newUser
                Log.d("preloadedData","login: $newUser")

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(
                    ContentValues.TAG,
                    "Failed to read value.",
                    error.toException()
                )
            }
        })
    }


}