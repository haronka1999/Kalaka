package com.e.kalaka.fragments

import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.e.kalaka.R
import com.e.kalaka.databinding.FragmentRegisterBinding
import com.e.kalaka.models.User
import com.e.kalaka.models.UserOrder
import com.e.kalaka.utils.Validation
import com.e.kalaka.viewModels.PreloadViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*


class RegisterFragment : Fragment() {

    private lateinit var datab: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userID: FirebaseUser
    private val preloadedData: PreloadViewModel by activityViewModels()
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var imageUri: Uri
    private lateinit var userId: String
    var database = FirebaseDatabase.getInstance()
    var myRef = database.reference

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1;
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
            userId = mAuth.currentUser?.uid.toString()
            val user = User(
                0,
                email,
                arrayListOf(),
                firstName,
                userId,
                lastName,
                arrayListOf(),
                imageUri.toString()
            )
            putUserDataIntoRealTimeDatabase(user)
        }
        binding.gotoLoginButton.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_registerFragment_to_loginFragment)
        }
        return binding.root
    }

    private fun putUserDataIntoRealTimeDatabase(
        user: User
    ) {
        // Log.d("Helo", "LastName: $lastName")
        //Log.d("Helo", "firstName: $firstName")
        Log.d("Helo", "imageUri: $imageUri")
        Log.d("Helo", "userId: $userId")


        myRef.child("users").child(user.userId).setValue(user)
//        myRef.child("users").child(user.userId).child("email").setValue(user.email)
//        myRef.child("users").child(user.userId).child("username")
//            .setValue(user.userName)


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
                    datab = FirebaseDatabase.getInstance()
                    databaseRef = database.getReference("users")
                    firebaseAuth = FirebaseAuth.getInstance()
                    userID = firebaseAuth.currentUser!!
                    databaseRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val user = dataSnapshot.child("userID")
                            val u = User(0,user.child("email").value.toString(),
                                mutableListOf(),user.child("firsName").value.toString(),user.child("userId").value.toString(),user.child("lastName").value.toString(),
                                mutableListOf(),user.child("photoUrl").value.toString()
                                )
                            Log.d("user","val: $u")
                            preloadedData.user.value=u

                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                            Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                        }
                    })
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
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            uploadPicture()
        }
    }

    private fun uploadPicture() {
        val randomKey = UUID.randomUUID().toString()
        val riversRef: StorageReference = storageReference.child("profile_image/" + randomKey)

        riversRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot -> // Get a URL to the uploaded content
                Log.d("Helo", "kep sikeresen feltoltve")
                Toast.makeText(activity, "Kép sikeresen feltöltve", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.d("Helo", "valami hiba a kepfeltoltesnel")
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
}