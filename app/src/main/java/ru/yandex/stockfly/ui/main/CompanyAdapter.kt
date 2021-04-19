package ru.yandex.stockfly.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.yandex.stockfly.base.AsyncListAdapter
import ru.yandex.stockfly.base.BaseDiffUtilCallback
import ru.yandex.stockfly.databinding.ItemCompanyBinding
import ru.yandex.stockfly.model.Company

open class CompanyAdapter(
    private val clickListener: OnCompanyClickListener
) : AsyncListAdapter<CompanyItem, CompanyAdapter.CompanyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCompanyBinding.inflate(inflater, parent, false)
        return CompanyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    fun submitCompanyList(list: List<Company>) {
        super.submitList(list.mapIndexed { index, company -> CompanyItem(company, index) })
    }


    class CompanyViewHolder(
        private val binding: ItemCompanyBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(companyItem: CompanyItem, clickListener: OnCompanyClickListener) {
            binding.apply {
                root.setOnClickListener {
                    clickListener.onClick(companyItem.company.ticker)
                }
                this.logo.setImageDrawable(null)
                this.company = companyItem.company
                this.position = companyItem.position
                executePendingBindings()
            }
        }
    }


    fun interface OnCompanyClickListener {
        fun onClick(ticker: String)
    }
}


data class CompanyItem(
    val company: Company,
    var position: Int
)


private val DIFF_CALLBACK = object : BaseDiffUtilCallback<CompanyItem>() {
    override fun areItemsTheSame(oldItem: CompanyItem, newItem: CompanyItem): Boolean {
        return oldItem.position == newItem.position
    }

    override fun areContentsTheSame(oldItem: CompanyItem, newItem: CompanyItem): Boolean {
        return oldItem.company == newItem.company
    }
}