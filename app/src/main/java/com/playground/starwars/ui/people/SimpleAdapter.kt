package com.playground.starwars.ui.people

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.playground.starwars.R
import kotlinx.android.synthetic.main.layout_simple_item.view.*

class SimpleAdapter(private val onItemClicked: (Int) -> Unit) :
    RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder>() {

    var items: List<SimpleListItem> = emptyList()
        set(value) {
            val diffResult = DiffCallback(
                field,
                value
            ).let(DiffUtil::calculateDiff)
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SimpleViewHolder(inflater.inflate(R.layout.layout_simple_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class SimpleViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        fun bind(item: SimpleListItem) {
            itemView.title.text = item.title
            itemView.subTitle.text = item.subtitle
            itemView.setOnClickListener { onItemClicked(item.id) }
        }
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

