package com.example.mainapplabrats.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mainapplabrats.databinding.ListItemDiagnosaBinding
import com.example.mainapplabrats.model.Cell

class JsonAdapter(private val cell: ArrayList<Cell>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class CellViewHolder(var viewBinding: ListItemDiagnosaBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ListItemDiagnosaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CellViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewHolder = holder as CellViewHolder
        itemViewHolder.viewBinding.tvDescDiagnose.text = cell[position].nama
        itemViewHolder.viewBinding.tvDescPenjelasan.text = cell[position].penjelasan
        itemViewHolder.viewBinding.tvDescTanda.text = cell[position].tanda
        itemViewHolder.viewBinding.tvDescPenyebab.text = cell[position].penyebab
        itemViewHolder.viewBinding.tvDescPencegahan.text = cell[position].pencegahan
        itemViewHolder.viewBinding.tvDescRekomendasi.text = cell[position].rekomendasi
    }

    override fun getItemCount(): Int {
        return 1
    }
}