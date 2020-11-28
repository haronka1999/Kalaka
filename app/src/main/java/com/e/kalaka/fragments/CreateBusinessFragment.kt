package com.e.kalaka.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.e.kalaka.R
import com.e.kalaka.databinding.FragmentCreateBusinessBinding
import com.e.kalaka.utils.Tag
import com.google.android.material.chip.Chip
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CreateBusinessFragment : Fragment() {

    private lateinit var binding: FragmentCreateBusinessBinding
    private var tags = mutableListOf<String>()
    private val emails= mutableListOf<String>()

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getUserEmails()
        Log.d("-----", "$emails")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_create_business,
            container,
            false
        )

        //setupAutoCompleteView()
        binding.chooseLogoButton.setOnClickListener{
            pickImageFromGallery()
        }

        binding.createBusinessButton.setOnClickListener{

        }

        setUpSpinner()

        binding.createBusinessTagContainer.removeAllViews()

        return binding.root
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, CreateBusinessFragment.IMAGE_PICK_CODE)
    }

    private fun setupAutoCompleteView() {
        Log.d("-----", "$emails")
        val autoComplete = binding.userAutoComplete
        val adapter = ArrayAdapter(binding.root.context, R.layout.spinner_item_layout, emails)
        autoComplete.setAdapter(adapter)
        autoComplete.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val email = emails[position]
                addUserChip(email)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun addUserChip(email: Any) {

    }

    private fun getUserEmails() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("users")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    val email = data.child("email").value.toString()
                    emails.add(email)
                    Log.d("-----", "$emails")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("--------", "Failed to read value.", error.toException())
            }
        })
    }

    private fun setUpSpinner() {
        val spinner = binding.tagSpinner
        val arrayAdapter = ArrayAdapter(
            binding.root.context,
            R.layout.spinner_item_layout,
            Tag.getTagList()
        )
        spinner.adapter = arrayAdapter

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item  = Tag.getTags()[position]
                if(!tags.contains(item.second)) {
                    tags.add(item.second)
                    addTagChip(item)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


    }

    private fun addTagChip(item: Pair<String, String>) {
        val chipGroup = binding.createBusinessTagContainer
        val chip = Chip(binding.root.context)
        val paddingDp: Int = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            10F,
            resources.displayMetrics
        ).toInt()
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        chip.text = item.second
        val iconId = binding.root.context.resources.getIdentifier(
            item.first,
            "drawable",
            binding.root.context.packageName
        )
        chip.chipIcon = ContextCompat.getDrawable(binding.root.context, iconId)
        chip.isCloseIconVisible = true
        chip.setCloseIconResource(R.drawable.ic_action_close)
        chip.setOnCloseIconClickListener {
            it as TextView
            tags.remove(it.text)
            chipGroup.removeView(chip)
        }
        chipGroup.addView(chip)
    }

}