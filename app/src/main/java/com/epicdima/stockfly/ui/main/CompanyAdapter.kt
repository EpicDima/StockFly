package com.epicdima.stockfly.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.Disposable
import com.epicdima.stockfly.R
import com.epicdima.stockfly.databinding.ItemCompanyBinding
import com.epicdima.stockfly.model.Company
import com.epicdima.stockfly.other.createUri
import com.epicdima.stockfly.other.getColor
import com.epicdima.stockfly.other.getDrawable
import timber.log.Timber

class CompanyAdapter(
    private val clickListener: OnCompanyClickListener
) : ListAdapter<CompanyViewHolderItem, CompanyAdapter.CompanyViewHolder>(DIFF_CALLBACK) {

    fun submitCompanyList(list: List<Company>) {
        super.submitList(list.mapIndexed { index, company ->
            CompanyViewHolderItem(
                index,
                company
            )
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCompanyBinding.inflate(inflater, parent, false)
        return CompanyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    override fun onBindViewHolder(
        holder: CompanyViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            holder.bind(getItem(position), payloads.first() as CompanyItemPayload)
        }
    }

    override fun onViewDetachedFromWindow(holder: CompanyViewHolder) {
        holder.unbind()
    }


    class CompanyViewHolder(
        private val binding: ItemCompanyBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var logoDisposable: Disposable? = null

        fun bind(companyItem: CompanyViewHolderItem, clickListener: OnCompanyClickListener) {
            binding.apply {
                root.setCardBackgroundColor(getColor(companyItem.rootCardBackgroundColor))
                ticker.text = companyItem.ticker
                favouriteIcon.setImageDrawable(getDrawable(companyItem.favouriteIcon))
                name.text = companyItem.name
                current.text = companyItem.currentString
                change.text = companyItem.changeString
                change.setTextColor(getColor(companyItem.changeTextColor))
                setLogo(logo, companyItem)
                root.setOnClickListener {
                    clickListener.onClick(companyItem.ticker)
                }
            }
        }

        private fun setLogo(logo: ImageView, companyItem: CompanyViewHolderItem) {
            logo.apply {
                setImageDrawable(null)
                setBackgroundColor(binding.getColor(companyItem.logoBackgroundColor))
                if (companyItem.logoUrl.isNotBlank()) {
                    logoDisposable = load(createUri(companyItem.logoUrl)) {
                        target {
                            background = null
                            setImageDrawable(it)
                        }
                    }
                }
            }
        }

        fun bind(companyItem: CompanyViewHolderItem, payload: CompanyItemPayload) {
            Timber.v("bind %s", payload)
            binding.apply {
                if (payload.name) {
                    name.text = companyItem.name
                }
                if (payload.favouriteIcon) {
                    favouriteIcon.setImageDrawable(getDrawable(companyItem.favouriteIcon))
                }
                if (payload.currentString) {
                    current.text = companyItem.currentString
                }
                if (payload.changeString) {
                    change.text = companyItem.changeString
                }
                if (payload.changeTextColor) {
                    change.setTextColor(getColor(companyItem.changeTextColor))
                }
            }
        }

        fun unbind() {
            logoDisposable?.dispose()
        }
    }


    fun interface OnCompanyClickListener {
        fun onClick(ticker: String)
    }
}


class CompanyViewHolderItem(
    val position: Int,
    company: Company
) {
    val logoUrl = company.logoUrl
    val ticker = company.ticker
    val favourite = company.favourite
    val name = company.name
    val currentString = company.currentString

    val rootCardBackgroundColor = if (position % 2 == 0) {
        R.color.light
    } else {
        R.color.white
    }

    val logoBackgroundColor = if (position % 2 == 0) {
        R.color.white
    } else {
        R.color.light
    }

    val favouriteIcon = if (favourite) {
        R.drawable.ic_star_solid_selected
    } else {
        R.drawable.ic_star_solid
    }

    val changeString = company.changeString +
            (if (company.changePercentString.isEmpty()) "" else " (") +
            company.changePercentString +
            if (company.changePercentString.isEmpty()) "" else ")"

    val changeTextColor = when {
        company.changeString.startsWith("+") -> R.color.green
        company.changeString.startsWith("-") -> R.color.red
        else -> R.color.black
    }
}


private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CompanyViewHolderItem>() {

    override fun areItemsTheSame(
        oldItem: CompanyViewHolderItem,
        newItem: CompanyViewHolderItem
    ): Boolean {
        return oldItem.position == newItem.position
    }

    override fun areContentsTheSame(
        oldItem: CompanyViewHolderItem,
        newItem: CompanyViewHolderItem
    ): Boolean {
        return oldItem.logoUrl == newItem.logoUrl
                && oldItem.ticker == newItem.ticker
                && oldItem.favourite == newItem.favourite
                && oldItem.name == newItem.name
                && oldItem.currentString == newItem.currentString
                && oldItem.changeString == newItem.changeString
                && oldItem.changeTextColor == newItem.changeTextColor
    }

    override fun getChangePayload(
        oldItem: CompanyViewHolderItem,
        newItem: CompanyViewHolderItem
    ): Any? {
        if (oldItem.ticker != newItem.ticker) {
            return super.getChangePayload(oldItem, newItem)
        }

        val payload = CompanyItemPayload(
            oldItem.name != newItem.name,
            oldItem.favouriteIcon != newItem.favouriteIcon,
            oldItem.currentString != newItem.currentString,
            oldItem.changeString != newItem.changeString,
            oldItem.changeTextColor != newItem.changeTextColor,
        )

        if (payload != DEFAULT_COMPANY_ITEM_PAYLOAD) {
            return payload
        }

        return super.getChangePayload(oldItem, newItem)
    }
}


data class CompanyItemPayload(
    var name: Boolean = false,
    var favouriteIcon: Boolean = false,
    var currentString: Boolean = false,
    var changeString: Boolean = false,
    var changeTextColor: Boolean = false,
)

private val DEFAULT_COMPANY_ITEM_PAYLOAD = CompanyItemPayload()