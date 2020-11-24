package com.playground.starwars.ui.person

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.playground.starwars.R
import com.playground.starwars.databinding.FragmentPersonBinding
import com.playground.starwars.ui.FragmentArgumentDelegate
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PersonFragment : Fragment(R.layout.fragment_person) {
    companion object {
        fun newInstance(personId: Int) = PersonFragment().apply { personIdArg = personId }
    }

    private var personIdArg by FragmentArgumentDelegate<Int>()
    private val viewModel by viewModel<PersonViewModel> { parametersOf(personIdArg) }

    private var _binding: FragmentPersonBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.bindViewModel()
        binding.retryButton.setOnClickListener {
            viewModel.refresh()
        }
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun PersonViewModel.bindViewModel() {
        bindToTextView(name, binding.nameTextView)
        bindToTextView(height, binding.heightTextView)
        bindToTextView(birthday, binding.birthdayTextView)
        bindToTextView(bmi, binding.bmiTextView)
        bindToTextView(planetName, binding.planetTextView)
        bindToTextView(planetClimate, binding.planetClimateTextView)
        bindToTextView(planetTerrain, binding.planetTerrainTextView)
        bindToTextView(planetGravity, binding.planetGravityTextView)
        bindToTextView(planetPopulation, binding.planetPopulationTextView)
        bindToTextView(films,binding. filmsTextView)
        bind(isDataContainerVisible) {
            binding.dataContainer.isVisible = it
        }
        bind(isErrorVisible) {
            binding. errorText.isVisible = it
            binding.retryButton.isVisible = it
        }
        bind(isLoadingVisible) {
            binding.loadingView.isVisible = it
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
