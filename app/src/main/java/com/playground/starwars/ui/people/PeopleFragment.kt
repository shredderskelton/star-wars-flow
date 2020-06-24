package com.playground.starwars.ui.people

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.playground.starwars.R
import com.playground.starwars.pushFragment
import com.playground.starwars.ui.person.PersonFragment
import com.playground.starwars.ui.person.bind
import kotlinx.android.synthetic.main.fragment_people.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PeopleFragment : Fragment(R.layout.fragment_people) {

    private val adapter = PageLoadingAdapter {
        parentFragmentManager.pushFragment(PersonFragment.newInstance(it))
    }

    private val viewModel by viewModel<PeopleViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
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