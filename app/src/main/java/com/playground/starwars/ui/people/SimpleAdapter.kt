package com.playground.starwars.ui.people

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.playground.starwars.R
import kotlinx.android.synthetic.main.layout_simple_item.view.*

private const val NORMAL = 1
private const val LOADING = 2

class SimpleAdapter(private val onItemClicked: (Int) -> Unit) :
    RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder>() {

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
                parent.inflate(R.layout.layout_simple_item),
                onItemClicked
            )
            LOADING -> SimpleViewHolder.LoadingViewHolder(
                parent.inflate(R.layout.layout_loading_item)
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
        class NormalViewHolder(view: View, private val onItemClicked: (Int) -> Unit) :
            SimpleViewHolder(view) {

            fun bind(item: SimpleListItem) {
                with(itemView) {
                    title.text = item.title
                    subTitle.text = item.subtitle
                    setOnClickListener { onItemClicked(item.id) }
                }
            }
        }

        class LoadingViewHolder(view: View) : SimpleViewHolder(view)
    }
}

fun ViewGroup.inflate(@LayoutRes layout: Int): View =
    LayoutInflater.from(context).inflate(layout, this, false)

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

