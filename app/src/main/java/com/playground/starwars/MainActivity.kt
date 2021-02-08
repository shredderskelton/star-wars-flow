package com.playground.starwars

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.playground.starwars.ui.people.PeopleFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi


@InternalCoroutinesApi

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null)
            supportFragmentManager.pushFragment(PeopleFragment())

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                finish()
            }
        }
    }
}

fun FragmentManager.pushFragment(fragment: Fragment) =
    beginTransaction()
        .setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
        .add(R.id.container, fragment)
        .addToBackStack(fragment::class.java.simpleName)
        .commit()
