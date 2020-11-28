package com.e.kalaka.fragments

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.e.kalaka.R
import com.e.kalaka.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    companion object {
        const val IMAGE_PICK_CODE = 1000;
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)


        binding.chooseImageButton.setOnClickListener {
            Log.d("Helo", "buttonClick")
            checkForPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE, "Galéria", IMAGE_PICK_CODE)

        }

        return binding.root

    }

    private fun checkForPermissions(permission: String, name: String, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("Helo", "SDK_INT itt")
            when {
                ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(activity, "$name permission granted", Toast.LENGTH_SHORT).show()
                    Log.d("Helo", "engedelyezve")
                }
                shouldShowRequestPermissionRationale(permission) -> {
                    Log.d("Helo", "nincs engedelyezve")
                    showDialog(permission, name, requestCode)
                }

                else -> {
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), requestCode)
                    Log.d("Helo", "else ag")
                }
            }
        }
    }

    //ez csak a legvegen hivodik meg
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "$name permission denied", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "$name permission granted", Toast.LENGTH_SHORT).show()
            }
        }

        when (requestCode) {
            IMAGE_PICK_CODE -> innerCheck("Galéria")
        }
    }

    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(activity)
        Log.d("Helo", "showDialog")
        builder.apply {

            setMessage("Permission to access your $name is required")
            setTitle("Permission required")
            setPositiveButton("OK") { dialog, which ->
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(permission),
                    requestCode
                )
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }

}