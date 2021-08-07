package com.epicdima.stockfly.ui.search

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
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
import com.epicdima.stockfly.base.BaseViewModelFragment
import com.epicdima.stockfly.databinding.FragmentSearchBinding
import com.epicdima.stockfly.ui.MainRouter
import com.epicdima.stockfly.ui.main.CompanyAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SearchFragment : BaseViewModelFragment<SearchViewModel, FragmentSearchBinding>() {

    companion object {
        @JvmStatic
        fun newInstance(): SearchFragment {
            Timber.i("newInstance")
            return SearchFragment()
        }
    }

    override val _viewModel: SearchViewModel by viewModels()

    private val onChipClick: (String) -> Unit = {
        Timber.i("onChipClick %s", it)

        binding.searchEditText.apply {
            setText(it)
            setSelection(it.length)
            showKeyboard()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.v("onCreateView")
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.v("onViewCreated")
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

    override fun onResume() {
        super.onResume()
        showKeyboard()
    }

    override fun onPause() {
        hideKeyboard()
        super.onPause()
    }

    private fun setupResultList() {
        val resultAdapter = CompanyAdapter { ticker ->
            (requireActivity() as MainRouter.CompanyFragmentOpener).openCompanyFragment(ticker)
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
        }
        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun showKeyboard() {
        if (binding.searchEditText.requestFocus()) {
            Timber.v("showKeyboard with request focus")
            (requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                .toggleSoftInput(SHOW_FORCED, HIDE_NOT_ALWAYS)
        }
    }

    private fun hideKeyboard() {
        Timber.v("hideKeyboard")
        binding.searchEditText.apply {
            clearFocus()
            (requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(windowToken, 0)
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
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }
}