package ru.yandex.stockfly.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.yandex.stockfly.base.BaseDiffUtilCallback
import ru.yandex.stockfly.databinding.ItemCompanyBinding
import ru.yandex.stockfly.model.Company

class CompanyAdapter(
    private val clickListener: OnCompanyClickListener
) : ListAdapter<Company, CompanyAdapter.CompanyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCompanyBinding.inflate(inflater, parent, false)
        return CompanyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        holder.bind(getItem(position), position, clickListener)
    }


    class CompanyViewHolder(private val binding: ItemCompanyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(company: Company, position: Int, clickListener: OnCompanyClickListener) {
            binding.apply {
                root.setOnClickListener {
                    clickListener.onClick(company.ticker)
                }
                this.company = company
                this.position = position
                executePendingBindings()
            }
        }
    }


    fun interface OnCompanyClickListener {
        fun onClick(ticker: String)
    }
}

private val DIFF_CALLBACK = object : BaseDiffUtilCallback<Company>() {
    override fun areItemsTheSame(oldItem: Company, newItem: Company): Boolean {
        return oldItem.ticker == newItem.ticker
    }
}