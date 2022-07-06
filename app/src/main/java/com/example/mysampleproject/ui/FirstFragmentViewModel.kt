package com.example.mysampleproject.ui

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mysampleproject.R


class FirstFragmentViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val MINIMUM_METRES = 10.0f
        private const val ANY_POSITIVE_NUMBER = 1
    }

    private val userDistanceLiveData = MutableLiveData<String>()
    val userDistance: LiveData<String>
        get() = userDistanceLiveData

    @VisibleForTesting
    var startingPoint: Location? = null

    fun getDistanceFromStartingPoint(location: Location?) {
        if (startingPoint == null) {
            startingPoint = location
        }
        val distanceInMeters = startingPoint?.distanceTo(location) ?: 0.0f

        if (distanceInMeters.compareTo(MINIMUM_METRES) >= ANY_POSITIVE_NUMBER) {
            val message = getApplication<Application>().getString(R.string.user_notification_message, distanceInMeters.toInt())
            userDistanceLiveData.value = message
        }
    }

}

