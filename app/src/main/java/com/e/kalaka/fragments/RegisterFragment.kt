package com.e.kalaka.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import androidx.navigation.Navigation
import com.e.kalaka.R
import com.e.kalaka.databinding.FragmentRegisterBinding
import com.e.kalaka.models.User
import com.e.kalaka.utils.Validation
import com.e.kalaka.viewModels.PreloadViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.concurrent.timerTask


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var imageUri: Uri
    private lateinit var userId: String
    private lateinit var lastName: String
    private lateinit var firstName: String
    private lateinit var email: String
    private lateinit var password: String
    var database = FirebaseDatabase.getInstance()
    var myRef = database.reference
    private val preloadedData: PreloadViewModel by activityViewModels()

    companion object {
        //image pick code
        val IMAGE_PICK_CODE = 1;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        binding.chooseImageButton.setOnClickListener {
            pickImageFromGallery()
        }

        binding.saveButton.setOnClickListener {
            lastName = binding.lastNameEditText.text.toString()
            firstName = binding.firstNameEditText.text.toString()
            email = binding.emailEditText.text.toString()
            password = binding.passwordEditText.text.toString()

            Log.d("helo", "Email : $email")
            Log.d("helo", "password : $password")
            if (!registrationValidation(lastName, firstName, email, password))
                return@setOnClickListener


            //authentication
            registerUserInDataBase(email, password)
        }

        binding.gotoLoginButton.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_registerFragment_to_loginFragment)
        }
        return binding.root
    }



    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
        }
    }


    private fun uploadPicture(id: String) {

        val riversRef: StorageReference = storageReference.child("profile_image/$id")

        riversRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot -> // Get a URL to the uploaded content
                Log.d("Helo", "kep sikeresen feltoltve")
                Toast.makeText(activity, "Kép sikeresen feltöltve", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.d("Helo", "valami hiba a kepfeltoltesnel")
            }

    }

    private fun putUserDataIntoRealTimeDatabase(
        user: User
    ) {
        // Log.d("Helo", "LastName: $lastName")
        //Log.d("Helo", "firstName: $firstName")
        Log.d("Helo", "imageUri: $imageUri")

        userId = mAuth.currentUser?.uid.toString()
        // upload user image to firebase storage
        uploadPicture(userId)

        user.userId = userId
        user.photoURL = "profile_image/${userId}"
        Log.d("Helo", "userId: $userId")
        myRef.child("users").child(userId).setValue(user)

        Toast.makeText(
            activity,
            "User created into realtime",
            Toast.LENGTH_SHORT
        ).show()


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
                    Log.d("Helo", "imageUri.toString() $imageUri")

                    userId = ""
                    val user = User(
                        "0",
                        email,
                        arrayListOf(),
                        firstName,
                        userId,
                        lastName,
                        arrayListOf(),
                        imageUri.toString()
                    )
                    //realtime
                    Toast.makeText(
                        activity,
                        "User created into authentication",
                        Toast.LENGTH_SHORT
                    ).show()

                    putUserDataIntoRealTimeDatabase(user)
                    preloadedData.user.value = user
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var callbackCounter = 0
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (callbackCounter == 0) {
                Toast.makeText(requireContext(), "Nyomja meg újra a kilépéshez!", Toast.LENGTH_SHORT).show()
                Timer().schedule(timerTask {
                    callbackCounter = 0
                }, 2000)

                callbackCounter++
            } else requireActivity().finish()
        }
    }
}