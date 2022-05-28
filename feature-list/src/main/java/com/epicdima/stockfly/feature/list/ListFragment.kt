package com.epicdima.stockfly.feature.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.epicdima.stockfly.core.common.ViewModelFragment
import com.epicdima.stockfly.core.ui.customize
import com.epicdima.stockfly.core.ui.getDimensionInSp
import com.epicdima.stockfly.core.ui.set
import com.epicdima.stockfly.feature.list.databinding.FragmentListBinding
import com.epicdima.stockfly.feature.list.favourite.FavouriteListTabFragment
import com.epicdima.stockfly.feature.list.total.TotalListTabFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ListFragment : ViewModelFragment<ListViewModel, FragmentListBinding>() {

    companion object {

        @JvmStatic
        fun newInstance(): ListFragment {
            Timber.i("newInstance")
            return ListFragment()
        }
    }

    override val viewModel: ListViewModel by viewModels()

    private val titles: Array<String> by lazy(LazyThreadSafetyMode.NONE) {
        MainTab.values().map { resources.getString(it.titleId) }.toTypedArray()
    }

    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun getLayoutId(): Int = R.layout.fragment_list

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?, attachToParent: Boolean) =
        FragmentListBinding.inflate(inflater, container, attachToParent)

    override fun bind(view: View) = FragmentListBinding.bind(view)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewPager.also {
                it.adapter = MainFragmentAdapter(this@ListFragment)
                it.isUserInputEnabled = false
                it.offscreenPageLimit = MainTab.values().size
            }
            searchLayout.setOnClickListener {
                viewModel.openSearch()
            }
        }
        setupTabs()
    }

    private fun setupTabs() {
        binding.apply {
            tabLayoutMediator =
                tabLayout.customize(viewPager, R.layout.main_tab_item_layout, titles, onSelect = {
                    it.set(
                        resources.getDimensionInSp(com.epicdima.stockfly.core.ui.R.dimen.main_tab_selected_textsize),
                        com.epicdima.stockfly.core.ui.R.color.black
                    )
                }, onUnselect = {
                    it.set(
                        resources.getDimensionInSp(com.epicdima.stockfly.core.ui.R.dimen.main_tab_textsize),
                        com.epicdima.stockfly.core.ui.R.color.dark
                    )
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
                MainTab.ALL.ordinal -> TotalListTabFragment.newInstance()
                MainTab.FAVOURITE.ordinal -> FavouriteListTabFragment.newInstance()
                else -> throw RuntimeException("Unknown main tab fragment on position '$position'")
            }
        }
    }
}