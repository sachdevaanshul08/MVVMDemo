package com.demo.ui.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.demo.connectioncallback.ConnectionObserver
import javax.inject.Inject

class MapViewModel @Inject constructor(val app: Application) :
    AndroidViewModel(app) {

    var connectivityObserver: LiveData<Boolean> = ConnectionObserver(app)

}