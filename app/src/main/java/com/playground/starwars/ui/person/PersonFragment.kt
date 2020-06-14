package com.playground.starwars.ui.person

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.playground.starwars.R
import kotlinx.android.synthetic.main.fragment_person.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


@FlowPreview
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class PersonFragment : Fragment(R.layout.fragment_person) {
    companion object {
        private const val ARG_PERSON_ID = "ARG_ID"
        fun newInstance(personId: Int): PersonFragment {
            return PersonFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PERSON_ID, personId)
                }
            }
        }
    }

    private val personIdArg by lazy {
        arguments!!.getInt(ARG_PERSON_ID, 0)
    }

    private val viewModel by viewModel<PersonViewModel> { parametersOf(personIdArg) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.bindViewModel()
        retryButton.setOnClickListener {
            viewModel.refresh()
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

fun Fragment.launchCoroutine(block: suspend CoroutineScope.() -> Unit): Job {
    val handler =
        CoroutineExceptionHandler { _, exception ->
            Log.e("Fragment", "Unhandled Exception: $exception")
            throw exception
        }

    return lifecycleScope.launch(context = handler, block = block)
}

@InternalCoroutinesApi
fun <T> Fragment.bind(property: Flow<T>, block: (T) -> Unit) {
    launchCoroutine {
        property.collect(object : FlowCollector<T> {
            override suspend fun emit(value: T) {
                block(value)
            }
        })
    }
}

@InternalCoroutinesApi
fun Fragment.bindToTextView(property: Flow<String>, textView: TextView) =
    bind(property) { textView.text = it }
