package com.example.sampleapp.viewModal

import androidx.lifecycle.ViewModel
import com.example.sampleapp.service.GymServiceRepository

abstract class BaseViewModel internal constructor() :
    ViewModel() {
    internal var gymServiceRepository = GymServiceRepository.getInstance()
}