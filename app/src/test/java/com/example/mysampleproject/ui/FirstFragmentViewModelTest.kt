package com.example.mysampleproject.ui

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class FirstFragmentViewModelTest {

    private lateinit var viewModel: FirstFragmentViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = FirstFragmentViewModel(mockk(relaxed = true))
    }

    @Test
    fun `Given location is not null, When background service returns location, Then comparison of distance must be performed`() {
        val testStartingLocation: Location = mockk(relaxed = true)
        testStartingLocation.longitude = 0.0
        testStartingLocation.latitude = 0.0

        viewModel.startingPoint = testStartingLocation

        val sampleLocation: Location = mockk(relaxed = true)
        sampleLocation.longitude = 33.123456
        sampleLocation.latitude = 16.12333

        viewModel.getDistanceFromStartingPoint(sampleLocation)

        verify {
            viewModel.startingPoint?.distanceTo(sampleLocation)
        }

        Assert.assertTrue(viewModel.startingPoint != null)
        Assert.assertTrue(viewModel.userDistance.value != null)
    }

}