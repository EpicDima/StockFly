package com.epicdima.stockfly.ui.main

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.clear
import coil.load
import coil.request.Disposable
import com.epicdima.stockfly.R
import com.epicdima.stockfly.databinding.ItemCompanyBinding
import com.epicdima.stockfly.model.Company
import com.epicdima.stockfly.other.createUri
import timber.log.Timber

class CompanyAdapter(
    private val clickListener: OnCompanyClickListener
) : ListAdapter<CompanyViewHolderItem, CompanyAdapter.CompanyViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val commonRecycledViewPool by lazy {
            RecyclerView.RecycledViewPool().apply {
                setMaxRecycledViews(0, 10)
            }
        }
    }

    val recycledViewPool: RecyclerView.RecycledViewPool = commonRecycledViewPool

    fun submitCompanyList(list: List<Company>, context: Context) {
        super.submitList(list.mapIndexed { index, company ->
            CompanyViewHolderItem(
                index,
                company
            ).apply {
                rootCardBackgroundColor =
                    getColor(context.applicationContext, rootCardBackgroundColorId)
                logoBackgroundColor = getColor(context.applicationContext, logoBackgroundColorId)
                favouriteIcon = getDrawable(context.applicationContext, favouriteIconId)
                changeTextColor = getColor(context.applicationContext, changeTextColorId)
            }
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
            Timber.v("bind %s", companyItem)
            binding.apply {
                root.setCardBackgroundColor(companyItem.rootCardBackgroundColor)
                ticker.text = companyItem.ticker
                ticker.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    companyItem.favouriteIcon,
                    null
                )
                name.text = companyItem.name
                current.text = companyItem.currentString
                change.text = companyItem.changeString
                change.setTextColor(companyItem.changeTextColor)
                setLogo(logo, companyItem)
                root.setOnClickListener {
                    clickListener.onClick(companyItem.ticker)
                }
            }
        }

        private fun setLogo(logo: ImageView, companyItem: CompanyViewHolderItem) {
            logo.apply {
                setImageDrawable(null)
                setBackgroundColor(companyItem.logoBackgroundColor)
                if (companyItem.logoUrl.isNotBlank()) {
                    logoDisposable = load(createUri(companyItem.logoUrl)) {
                        target {
                            Timber.d("bind logo image for %s", companyItem.ticker)
                            background = null
                            setImageDrawable(it)
                        }
                    }
                }
            }
        }

        fun bind(companyItem: CompanyViewHolderItem, payload: CompanyItemPayload) {
            Timber.v("bind %s with payload %s", companyItem, payload)
            binding.apply {
                if (payload.name) {
                    name.text = companyItem.name
                }
                if (payload.favouriteIcon) {
                    ticker.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        companyItem.favouriteIcon,
                        null
                    )
                }
                if (payload.currentString) {
                    current.text = companyItem.currentString
                }
                if (payload.changeString) {
                    change.text = companyItem.changeString
                }
                if (payload.changeTextColor) {
                    change.setTextColor(companyItem.changeTextColor)
                }
            }
        }

        fun unbind() {
            logoDisposable?.dispose()
            binding.logo.clear()
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

    val rootCardBackgroundColorId = if (position % 2 == 0) {
        R.color.light
    } else {
        R.color.white
    }
    var rootCardBackgroundColor = 0

    val logoBackgroundColorId = if (position % 2 == 0) {
        R.color.white
    } else {
        R.color.light
    }
    var logoBackgroundColor: Int = 0

    val favouriteIconId = if (favourite) {
        R.drawable.ic_star_solid_selected
    } else {
        R.drawable.ic_star_solid
    }
    var favouriteIcon: Drawable? = null

    val changeString = company.changeString +
            (if (company.changePercentString.isEmpty()) "" else " (") +
            company.changePercentString +
            if (company.changePercentString.isEmpty()) "" else ")"

    val changeTextColorId = when {
        company.changeString.startsWith("+") -> R.color.green
        company.changeString.startsWith("-") -> R.color.red
        else -> R.color.black
    }
    var changeTextColor: Int = 0
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
                && oldItem.changeTextColorId == newItem.changeTextColorId
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
            oldItem.favouriteIconId != newItem.favouriteIconId,
            oldItem.currentString != newItem.currentString,
            oldItem.changeString != newItem.changeString,
            oldItem.changeTextColorId != newItem.changeTextColorId,
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