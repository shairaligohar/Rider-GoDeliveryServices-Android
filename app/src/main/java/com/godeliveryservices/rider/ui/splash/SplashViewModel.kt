package com.godeliveryservices.rider.ui.splash

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplashViewModel : ViewModel() {

    object Events {
        const val NAVIGATE_TO_HOME: String = "NAVIGATE_TO_HOME"
        const val NAVIGATE_TO_LOGIN: String = "NAVIGATE_TO_LOGIN"
    }

//    private val _navigate = MutableLiveData<Lifecycle.Event<String>>()
//
//    val navigate: LiveData<Lifecycle.Event<String>> get() = _navigate

    fun start() {
//        viewModelScope.launch {
//            launch(Dispatchers.IO) {
//                Thread.sleep(1000)
//            }
//        }
    }

    fun navigate() {
//        viewModelScope.launch {
//            if (preferencesRepository.isUserAuthenticated()) {
//                userRepository.loadAuthUser()
//                _navigate.postValue(Event.create(content = Events.NAVIGATE_TO_HOME))
//            } else {
//                _navigate.postValue(Event.create(content = Events.NAVIGATE_TO_LOGIN))
//            }
//        }
    }

}
