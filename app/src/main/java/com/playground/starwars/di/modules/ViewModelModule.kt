package com.playground.starwars.di.modules

import com.playground.starwars.ui.people.PeopleViewModel
import com.playground.starwars.ui.person.PersonViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


@FlowPreview
@ExperimentalCoroutinesApi
val viewModelModule = module {
    viewModel { PeopleViewModel(get(), get()) }
    viewModel { (personId: Int) -> PersonViewModel(get(), get(), personId) }
}
