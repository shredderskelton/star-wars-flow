package com.playground.starwars.ui.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.playground.starwars.R
import com.playground.starwars.databinding.FragmentPeopleBinding
import com.playground.starwars.pushFragment
import com.playground.starwars.ui.person.PersonFragment
import com.playground.starwars.ui.person.bind
import org.koin.androidx.viewmodel.ext.android.viewModel

class PeopleFragment : Fragment(R.layout.fragment_people) {
    private var _binding: FragmentPeopleBinding? = null
    private val binding get() = requireNotNull(_binding) { "FragmentPeopleBinding cannot be null" }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPeopleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private val adapter = PageLoadingAdapter {
        parentFragmentManager.pushFragment(PersonFragment.newInstance(it))
    }

    private val viewModel by viewModel<PeopleViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )
        viewModel.bindViewModel()
    }

    private fun PeopleViewModel.bindViewModel() {
        bind(people) {
            adapter.items = it
        }
        bind(isLoading) {
            adapter.isLoading = it
        }
    }
}