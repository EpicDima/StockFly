package ru.yandex.stockfly.ui.search

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.HORIZONTAL
import dagger.hilt.android.AndroidEntryPoint
import ru.yandex.stockfly.R
import ru.yandex.stockfly.base.BaseFragment
import ru.yandex.stockfly.databinding.FragmentSearchBinding
import ru.yandex.stockfly.ui.CompanyFragmentOpener

@AndroidEntryPoint
class SearchFragment : BaseFragment<SearchViewModel, FragmentSearchBinding>() {
    companion object {
        @JvmStatic
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    override val viewModel: SearchViewModel by viewModels()

    private val onChipClick: (String) -> Unit = {
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
        _binding = FragmentSearchBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            loading = viewModel.loading
            error = viewModel.error
            showPopular = viewModel.showPopular
            showSearched = viewModel.showSearched
            showResult = viewModel.showResult
            emptyResult = viewModel.emptyResult
        }
        setupSearchField()
        setupButtons()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.popularRecyclerView.setupSearchChipList(viewModel.popular)
        binding.searchedRecyclerView.setupSearchChipList(viewModel.searched)
        setupResultList()
    }

    override fun onStart() {
        super.onStart()
        showKeyboard()
    }

    private fun setupResultList() {
        val resultAdapter = SearchItemAdapter { ticker ->
            (requireActivity() as CompanyFragmentOpener).openCompanyFragment(ticker)
        }
        binding.resultRecyclerView.apply {
            adapter = resultAdapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
        }
        viewModel.result.observe(viewLifecycleOwner) { resultAdapter.submitList(it) }
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
            hideKeyboard()
            requireActivity().onBackPressed()
        }
    }

    private fun showKeyboard() {
        if (binding.searchEditText.requestFocus()) {
            (requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                .toggleSoftInput(SHOW_FORCED, HIDE_NOT_ALWAYS)
        }
    }

    private fun hideKeyboard() {
        binding.searchEditText.apply {
            clearFocus()
            (requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(windowToken, 0)
        }
    }

    private fun RecyclerView.setupSearchChipList(listLiveData: LiveData<List<String>>) {
        val adapter = SearchChipAdapter(onChipClick)
        this.adapter = adapter
        layoutManager = StaggeredGridLayoutManager(2, HORIZONTAL)
        itemAnimator = null
        listLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            scrollToPosition(0)
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