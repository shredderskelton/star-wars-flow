package com.playground.starwars.ui.person

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.playground.starwars.R
import com.playground.starwars.ui.FragmentArgumentDelegate
import kotlinx.android.synthetic.main.fragment_person.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PersonFragment : Fragment(R.layout.fragment_person) {
    companion object {
        fun newInstance(personId: Int) = PersonFragment().apply { personIdArg = personId }
    }

    private var personIdArg by FragmentArgumentDelegate<Int>()
    private val viewModel by viewModel<PersonViewModel> { parametersOf(personIdArg) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.bindViewModel()
        retryButton.setOnClickListener {
            viewModel.refresh()
        }
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun PersonViewModel.bindViewModel() {
        bindToTextView(name, nameTextView)
        bindToTextView(height, heightTextView)
        bindToTextView(birthday, birthdayTextView)
        bindToTextView(bmi, bmiTextView)
        bindToTextView(planetName, planetTextView)
        bindToTextView(planetClimate, planetClimateTextView)
        bindToTextView(planetTerrain, planetTerrainTextView)
        bindToTextView(planetGravity, planetGravityTextView)
        bindToTextView(planetPopulation, planetPopulationTextView)
        bindToTextView(films, filmsTextView)
        bind(isDataContainerVisible) {
            dataContainer.isVisible = it
        }
        bind(isErrorVisible) {
            errorText.isVisible = it
            retryButton.isVisible = it
        }
        bind(isLoadingVisible) {
            loadingView.isVisible = it
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
}


val handler =
    CoroutineExceptionHandler { _, exception ->
        Log.e("Fragment", "Unhandled Exception: $exception")
        throw exception
    }

fun Fragment.launchCoroutine(block: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launch(context = handler, block = block)
}

fun <T> Fragment.bind(property: Flow<T>, block: (T) -> Unit) {
    launchCoroutine {
        property.collectLatest { block(it) }
    }
}

fun Fragment.bindToTextView(property: Flow<String>, textView: TextView) =
    bind(property) { textView.text = it }
