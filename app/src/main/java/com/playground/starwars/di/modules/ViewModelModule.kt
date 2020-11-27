package com.playground.starwars.di.modules

import com.playground.starwars.ui.people.PeopleViewModel
import com.playground.starwars.ui.person.PersonViewModel
import com.playground.starwars.ui.person.PersonViewModelOne
import com.playground.starwars.ui.person.PersonViewModelThree
import com.playground.starwars.ui.person.PersonViewModelTwo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@ExperimentalStdlibApi
val viewModelModule = module {
    viewModel { PeopleViewModel(get()) }
//    viewModel<PersonViewModel> { (personId: Int) -> PersonViewModelOne(get(), personId) }
//    viewModel<PersonViewModel> { (personId: Int) -> PersonViewModelTwo(get(), personId) }
    viewModel<PersonViewModel>{ (personId: Int) -> PersonViewModelThree(get(), personId) }
}
