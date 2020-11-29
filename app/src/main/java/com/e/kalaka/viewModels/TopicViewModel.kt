package com.e.kalaka.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.e.kalaka.models.Business

class TopicViewModel : ViewModel() {
    var list : MutableLiveData<MutableList<Business>> = MutableLiveData()
}