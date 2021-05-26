package com.example.sklad_8.ui.goods

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sklad_8.R
import com.google.android.material.textview.MaterialTextView

class GoodsAdapter(private val clickListener: (GoodViewData, Boolean) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<GoodViewData>() {
        override fun areItemsTheSame(oldViewData: GoodViewData, newViewData: GoodViewData): Boolean {
            return oldViewData.id == newViewData.id
        }
        override fun areContentsTheSame(oldViewData: GoodViewData, newViewData: GoodViewData): Boolean {
            return oldViewData.hashCode() == newViewData.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<GoodViewData>) = differ.submitList(list)

    override fun getItemViewType(position: Int): Int {
        val good = differ.currentList[position]
        return if (good.isGroup) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            0 -> {
                GoodItem(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_good,
                        parent,
                        false
                    )
                ){itemPosition, isFirst ->
                    val currGood = differ.currentList.getOrNull(itemPosition)
                    currGood?.let { good ->
                        clickListener(good, isFirst)
                    }
                }
            }
            else -> {
                GoodGroup(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_good_group,
                        parent,
                        false
                    )
                ){itemPosition, isFirst ->
                    val currGood = differ.currentList.getOrNull(itemPosition)
                    currGood?.let { good ->
                        clickListener(good, isFirst)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val good = differ.currentList[position]
        if (holder is GoodItem) holder.bind(good)
        if (holder is GoodGroup) holder.bind(good)
    }

    override fun getItemCount() = differ.currentList.size
}

class GoodItem(itemView: View, private val clickAtPosition: (Int, Boolean) -> Unit) : RecyclerView.ViewHolder(itemView) {

    private val title = itemView.findViewById<MaterialTextView>(R.id.tvTitleGood)
    private val article = itemView.findViewById<MaterialTextView>(R.id.tv_articul)

    init {
        itemView.setOnClickListener {
            clickAtPosition(adapterPosition, adapterPosition==0)
        }
    }

    fun bind(good: GoodViewData) {
        title.text = good.title
        article.text = good.article
    }
}
class GoodGroup(itemView: View, private val clickAtPosition: (Int, Boolean) -> Unit) : RecyclerView.ViewHolder(itemView) {
    val title = itemView.findViewById<MaterialTextView>(R.id.tvTitleGood)
    init {
        itemView.setOnClickListener {
            clickAtPosition(adapterPosition, adapterPosition==0)
        }
    }
    fun bind(good: GoodViewData) {
        title.text = good.title
    }
}