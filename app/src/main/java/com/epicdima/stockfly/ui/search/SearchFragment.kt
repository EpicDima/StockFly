package com.epicdima.stockfly.ui.search

import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_FORCED
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.HORIZONTAL
import com.epicdima.stockfly.R
import com.epicdima.stockfly.base.ViewModelFragment
import com.epicdima.stockfly.databinding.FragmentSearchBinding
import com.epicdima.stockfly.other.Formatter
import com.epicdima.stockfly.ui.MainRouter
import com.epicdima.stockfly.ui.main.CompanyAdapter
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : ViewModelFragment<SearchViewModel, FragmentSearchBinding>(),
    ViewTreeObserver.OnGlobalLayoutListener, AppBarLayout.OnOffsetChangedListener {

    companion object {
        @JvmStatic
        fun newInstance(): SearchFragment {
            Timber.i("newInstance")
            return SearchFragment()
        }
    }

    override val viewModel: SearchViewModel by viewModels()

    private var searchQueryText: String = ""
    private var isKeyboardShowing = false
    private var isAppBarExpanded = true

    private val onChipClick: (String) -> Unit = {
        Timber.i("onChipClick %s", it)

        binding.searchEditText.apply {
            setText(it)
            setSelection(it.length)
            showKeyboard()
        }
    }

    @Inject
    lateinit var formatter: Formatter

    override fun getLayoutId(): Int = R.layout.fragment_search

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?, attachToParent: Boolean) =
        FragmentSearchBinding.inflate(inflater, container, attachToParent)

    override fun bind(view: View) = FragmentSearchBinding.bind(view)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loading.collect {
                        binding.progressBarWidget.root.isVisible = it
                    }
                }

                launch {
                    viewModel.error.collect {
                        binding.errorWidget.root.isVisible = it
                    }
                }

                launch {
                    viewModel.emptyResult.collect {
                        binding.emptyTextview.isVisible = it
                    }
                }

                launch {
                    viewModel.showPopular.collect {
                        binding.popularTitle.isVisible = it
                        binding.popularRecyclerView.isVisible = it
                    }
                }

                launch {
                    viewModel.showSearched.collect {
                        binding.searchedTitle.isVisible = it
                        binding.searchedRecyclerView.isVisible = it
                    }
                }

                launch {
                    viewModel.showResult.collect {
                        binding.resultTitle.isVisible = it
                        binding.resultRecyclerView.isVisible = it
                    }
                }
            }
        }

        setupSearchField()
        setupButtons()
        binding.popularRecyclerView.setupSearchChipList(viewModel.popular)
        binding.searchedRecyclerView.setupSearchChipList(viewModel.searched)
        setupResultList()
    }

    override fun onGlobalLayout() {
        if (isVisible) {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.rootView.height
            isKeyboardShowing = (screenHeight - rect.bottom) > (screenHeight * 0.15)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(this)
        binding.appbar.addOnOffsetChangedListener(this)
        showKeyboard()
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
        binding.appbar.removeOnOffsetChangedListener(this)
        binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        isAppBarExpanded = verticalOffset == 0
    }

    private fun setupResultList() {
        val resultAdapter = CompanyAdapter(formatter) { ticker ->
            (requireActivity() as MainRouter.CompanyFragmentOpener).openCompanyFragment(ticker)
        }.apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        binding.resultRecyclerView.apply {
            adapter = resultAdapter
            setRecycledViewPool(resultAdapter.recycledViewPool)
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
            setHasFixedSize(true)
        }
        viewModel.result
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { resultAdapter.submitCompanyList(it, requireContext()) }
            .flowOn(Dispatchers.Default)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setupSearchField() {
        binding.searchEditText.afterTextChanged {
            binding.close.visibility = if (it.isNotEmpty()) View.VISIBLE else View.INVISIBLE
        }
        binding.searchEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (viewModel.search(v.text.toString())) {
                    hideKeyboard()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.not_empty_request),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                true
            } else {
                false
            }
        }
    }

    private fun setupButtons() {
        binding.close.setOnClickListener {
            viewModel.reset()
            binding.searchEditText.text.clear()
            showKeyboard()
        }
        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun showKeyboard() {
        Timber.v("showKeyboard %s %s", isAppBarExpanded, isKeyboardShowing)
        if (isAppBarExpanded && !isKeyboardShowing && binding.searchEditText.requestFocus()) {
            Timber.v("showKeyboard with request focus")
            (requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                .toggleSoftInput(SHOW_FORCED, 0)
        }
    }

    private fun hideKeyboard() {
        Timber.v("hideKeyboard")
        if (isKeyboardShowing) {
            binding.searchEditText.apply {
                clearFocus()
                (requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(windowToken, 0)
            }
            isKeyboardShowing = false
        }
    }

    private fun RecyclerView.setupSearchChipList(listFlow: StateFlow<List<String>>) {
        val adapter = SearchChipAdapter(onChipClick)
        this.adapter = adapter
        layoutManager = StaggeredGridLayoutManager(2, HORIZONTAL)
        itemAnimator = null
        setHasFixedSize(true)

        viewLifecycleOwner.lifecycleScope.launch {
            listFlow.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                adapter.submitList(it)
                scrollToPosition(0)
            }
        }
    }

    private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                val newText = text.toString()
                if (searchQueryText != newText) {
                    searchQueryText = newText
                    afterTextChanged.invoke(editable.toString())
                    if (!isAppBarExpanded) {
                        binding.appbar.setExpanded(true, true)
                    }
                }
            }
        })
    }
}