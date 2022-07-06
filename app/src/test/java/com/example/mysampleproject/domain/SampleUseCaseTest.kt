package com.example.mysampleproject.domain

import org.junit.Before
import org.junit.Test

class SampleUseCaseTest {
    private lateinit var useCase: SampleUseCase

    @Before
    fun setup() {
        useCase = SampleUseCase()
    }

    @Test
    fun `sampleTest`(){
        useCase.invoke()
    }
}