package com.playground.starwars.ui.people

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.playground.starwars.R
import com.playground.starwars.databinding.LayoutLoadingItemBinding
import com.playground.starwars.databinding.LayoutSimpleItemBinding

private const val NORMAL = 1
private const val LOADING = 2

class PageLoadingAdapter(private val onItemClicked: (Int) -> Unit) :
    RecyclerView.Adapter<PageLoadingAdapter.SimpleViewHolder>() {

    var isLoading: Boolean = true
        set(value) {
            field = value
            if (value) notifyItemInserted(items.size)
            else notifyItemRemoved(items.size)
        }

    var items: List<SimpleListItem> = emptyList()
        set(value) {
            val diffResult = DiffCallback(
                old = field,
                new = value
            ).let(DiffUtil::calculateDiff)
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        return when (viewType) {
            NORMAL -> SimpleViewHolder.NormalViewHolder(
                LayoutSimpleItemBinding.inflate(LayoutInflater.from(parent.context)),
                onItemClicked
            )
            LOADING -> SimpleViewHolder.LoadingViewHolder(
                LayoutLoadingItemBinding.inflate(LayoutInflater.from(parent.context)).root
            )
            else -> error("Unknown view type")
        }
    }

    override fun getItemCount(): Int {
        return items.count() + if (isLoading) 1 else 0
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        if (holder is SimpleViewHolder.NormalViewHolder) holder.bind(items[position])
    }

    override fun getItemViewType(position: Int) = if (position == items.size) LOADING else NORMAL

    sealed class SimpleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        class NormalViewHolder(
            private val binding: LayoutSimpleItemBinding,
            private val onItemClicked: (Int) -> Unit
        ) :
            SimpleViewHolder(binding.root) {

            fun bind(item: SimpleListItem) {
                with(binding) {
                    title.text = item.title
                    subTitle.text = item.subtitle
                    root.setOnClickListener { onItemClicked(item.id) }
                }
            }
        }

        class LoadingViewHolder(view: View) : SimpleViewHolder(view)
    }
}

private class DiffCallback<T>(
    private val old: List<T>,
    private val new: List<T>
) : DiffUtil.Callback() {
    override fun getOldListSize() = old.size

    override fun getNewListSize() = new.size

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int) =
        old[oldPosition] == new[newPosition]

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int) =
        old[oldPosition] == new[newPosition]
}

