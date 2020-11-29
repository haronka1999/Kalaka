package com.e.kalaka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.e.kalaka.R
import com.e.kalaka.databinding.FragmentAddTaskPopUpBinding
import com.e.kalaka.viewModels.TopicViewModel


class AddTaskPopUpFragment : DialogFragment() {


    private lateinit var binding: FragmentAddTaskPopUpBinding
    private val topicViewModel: TopicViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_add_task_pop_up,
                container,
                false
            )

        binding.vallalomButton.setOnClickListener {
            topicViewModel.choosedTask.value = 1
            dismiss()
        }

        binding.keszButton.setOnClickListener {
            topicViewModel.choosedTask.value = 2
            dismiss()
        }

        binding.postazvaButton.setOnClickListener {
            topicViewModel.choosedTask.value = 3
            dismiss()
        }

        binding.torlesButton.setOnClickListener {
            topicViewModel.choosedTask.value = 4
            dismiss()
        }

        return binding.root
    }

}


