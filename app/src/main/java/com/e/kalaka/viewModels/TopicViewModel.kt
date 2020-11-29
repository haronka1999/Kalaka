package com.e.kalaka.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.e.kalaka.models.Business
import com.e.kalaka.utils.Tag

class TopicViewModel : ViewModel() {
    var list : MutableLiveData<MutableList<Business>> = MutableLiveData()
    var filteredList : MutableLiveData<List<Pair<String, String>>> = MutableLiveData()
}