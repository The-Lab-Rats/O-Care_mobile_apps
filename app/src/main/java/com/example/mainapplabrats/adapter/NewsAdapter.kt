package com.example.mainapplabrats.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mainapplabrats.R
import com.example.mainapplabrats.activities.DetailNews
import com.example.mainapplabrats.databinding.ListItemNewsBinding
import com.example.mainapplabrats.model.ModelArticle
import com.example.mainapplabrats.util.Utils

class NewsAdapter(private val modelArticles: MutableList<ModelArticle>, private val context: Context) :
    RecyclerView.Adapter<NewsAdapter.ListViewHolder>() {

    class ListViewHolder(var binding: ListItemNewsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val binding = ListItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val model = modelArticles[position]

        if (model.urlToImage != null) {
            Glide.with(context)
                .load(model.urlToImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.imageThumbnail)
        } else {
            holder.binding.imageThumbnail.setImageResource(R.drawable.ic_broken_image)

        }

        if (model.author == null) {
            holder.binding.tvNameSource.text = model.modelSource?.name
        } else {
            holder.binding.tvNameSource.text = model.author + " \u2023 " + model.modelSource?.name
        }

        holder.binding.apply {
            holder.binding.tvTimeAgo.text = Utils.DateTimeHourAgo(model.publishedAt)
            holder.binding.tvTitleNews.text = limitWords(model.title,8)
            holder.binding.tvDateTime.text = Utils.DateFormat(model.publishedAt)
            holder.binding.frameListNews.setOnClickListener {
                val intent = Intent(context, DetailNews::class.java)
                intent.putExtra(DetailNews.DETAIL_NEWS, modelArticles[position])
                context.startActivity(intent)
            }
        }

    }
    fun limitWords(input: String, wordLimit: Int): String {
        val words = input.split(" ")
        return words.take(wordLimit).joinToString(" ")
    }
    override fun getItemCount(): Int {
        return modelArticles.size
    }

}