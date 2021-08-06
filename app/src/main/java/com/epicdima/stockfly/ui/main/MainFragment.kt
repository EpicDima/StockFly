package com.epicdima.stockfly.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.epicdima.stockfly.R
import com.epicdima.stockfly.base.BaseFragment
import com.epicdima.stockfly.databinding.FragmentMainBinding
import com.epicdima.stockfly.other.customize
import com.epicdima.stockfly.other.getDimensionInSp
import com.epicdima.stockfly.other.set
import com.epicdima.stockfly.ui.MainRouter
import com.epicdima.stockfly.ui.main.all.AllMainTabFragment
import com.epicdima.stockfly.ui.main.favourite.FavouriteMainTabFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {

    companion object {

        @JvmStatic
        fun newInstance(): MainFragment {
            Timber.i("newInstance")
            return MainFragment()
        }
    }

    private lateinit var titles: Array<String>

    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.v("onCreateView")
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.v("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        titles = MainTab.values().map { resources.getString(it.titleId) }.toTypedArray()
        binding.apply {
            viewPager.also {
                it.adapter = MainFragmentAdapter(this@MainFragment)
                it.isUserInputEnabled = false
                it.offscreenPageLimit = 1  // для загрузки списка избранных без ожидания
            }
            searchLayout.setOnClickListener {
                (requireActivity() as MainRouter.SearchFragmentOpener).openSearchFragment()
            }
        }
        setupTabs()
    }

    private fun setupTabs() {
        binding.apply {
            tabLayoutMediator = tabLayout.customize(viewPager, R.layout.main_tab_item_layout, titles, onSelect = {
                it.set(
                    resources.getDimensionInSp(R.dimen.main_tab_selected_textsize),
                    R.color.black
                )
            }, onUnselect = {
                it.set(resources.getDimensionInSp(R.dimen.main_tab_textsize), R.color.dark)
            })
            tabLayout.getTabAt(0)?.select()
        }
    }

    override fun onDestroyView() {
        binding.viewPager.adapter = null
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
        super.onDestroyView()
    }


    enum class MainTab(
        @StringRes
        val titleId: Int,
    ) {
        ALL(R.string.stocks),
        FAVOURITE(R.string.favourite)
    }


    private class MainFragmentAdapter(
        fragment: Fragment
    ) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int {
            return MainTab.values().size
        }

        override fun createFragment(position: Int): Fragment {
            Timber.i("createFragment with position %d", position)
            return when (position) {
                MainTab.ALL.ordinal -> AllMainTabFragment.newInstance()
                MainTab.FAVOURITE.ordinal -> FavouriteMainTabFragment.newInstance()
                else -> throw RuntimeException("Unknown main tab fragment on position '$position'")
            }
        }
    }
}