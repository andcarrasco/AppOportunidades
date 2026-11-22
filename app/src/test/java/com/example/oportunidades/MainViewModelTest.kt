package com.example.oportunidades

import com.example.oportunidades.navigation.NavigationEvent
import com.example.oportunidades.navigation.Screen
import com.example.oportunidades.viewmodel.MainViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(dispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    "los eventos de navegación llegan en el orden en que se emiten" {
        runTest(dispatcher) {
            val viewModel = MainViewModel()
            val eventos = mutableListOf<NavigationEvent>()
            val job = launch {
                viewModel.navigationEvents.collect { eventos.add(it) }
            }
            advanceUntilIdle()

            viewModel.navigateTo(Screen.Home, popUpToRoute = Screen.Login, inclusive = true)
            advanceUntilIdle()
            viewModel.navigateBack()
            advanceUntilIdle()
            viewModel.navigateUp()
            advanceUntilIdle()
            viewModel.navigateTo(Screen.Feriados, singleTop = true)
            advanceUntilIdle()

            eventos shouldContainExactly listOf(
                NavigationEvent.NavigateTo(Screen.Home, Screen.Login, inclusive = true),
                NavigationEvent.PopBackStack,
                NavigationEvent.NavigateUp,
                NavigationEvent.NavigateTo(Screen.Feriados, singleTop = true)
            )
            job.cancel()
        }
    }
})
