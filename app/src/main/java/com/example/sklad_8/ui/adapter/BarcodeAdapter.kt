package com.example.sklad_8.ui.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sklad_8.R
import com.google.zxing.BarcodeFormat
import com.example.sklad_8.data.repositores.data.BarcodeEntity
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.lang.Exception

class BarcodeAdapter: RecyclerView.Adapter<BarcodeAdapter.BarcodeViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<BarcodeEntity>() {
        override fun areItemsTheSame(oldItem: BarcodeEntity, newItem: BarcodeEntity): Boolean =
            oldItem.barcode == newItem.barcode

        override fun areContentsTheSame(oldItem: BarcodeEntity, newItem: BarcodeEntity): Boolean =
            oldItem.hashCode() == newItem.hashCode()
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(newList: List<BarcodeEntity>) {
        differ.submitList(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeViewHolder {
        return BarcodeViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_barcode_horizontal,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BarcodeViewHolder, position: Int) {
        val barcode: BarcodeEntity = differ.currentList[position]
        holder.bind(barcode)
    }

    override fun getItemCount() = differ.currentList.size

    class BarcodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivBarcode: ImageView = itemView.findViewById(R.id.iv_barcode)
        private val titleBarcode: TextView = itemView.findViewById(R.id.tv_title_barcode)

        fun bind(barcodeEntity: BarcodeEntity) {
            titleBarcode.text = barcodeEntity.barcode
            try {
                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.encodeBitmap(barcodeEntity.barcode, BarcodeFormat.CODE_128, 120, 40)
                ivBarcode.setImageBitmap(bitmap)
            } catch (e: Exception) {
            }
        }
    }
}