package com.tangbba.simplegithubforjava.ui.search

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.tangbba.simplegithubforjava.R
import com.tangbba.simplegithubforjava.api.model.GithubRepo
import com.tangbba.simplegithubforjava.ui.GlideApp

import java.util.ArrayList

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.RepositoryHolder>() {

    private var mItems: MutableList<GithubRepo> = ArrayList()
    private val mPlaceHolders = ColorDrawable(Color.GRAY)

    private var mListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryHolder {
        return RepositoryHolder(parent)
    }

    override fun onBindViewHolder(holder: RepositoryHolder, position: Int) {
        val repo = mItems[position]

        GlideApp.with(holder.itemView.context)
                .load(repo.owner.avatarUrl)
                .placeholder(mPlaceHolders)
                .into(holder.profileImageView)

        holder.nameTextView.text = repo.fullName
        holder.languageTextView.text = if (TextUtils.isEmpty(repo.language))
            holder.itemView.context.getText(R.string.no_language_specified)
        else
            repo.language
        holder.itemView.setOnClickListener {
            if (null != mListener) {
                mListener!!.onItemClick(repo)
            }
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun setItems(items: MutableList<GithubRepo>) {
        mItems = items
    }

    fun clearItems() {
        mItems.clear()
    }

    fun setItemClickListener(listener: ItemClickListener?) {
        mListener = listener
    }

    class RepositoryHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_repository, parent, false)) {

        var profileImageView: ImageView
        var nameTextView: TextView
        var languageTextView: TextView

        init {
            profileImageView = itemView.findViewById(R.id.profile_image_view)
            nameTextView = itemView.findViewById(R.id.name_text_view)
            languageTextView = itemView.findViewById(R.id.language_text_view)
        }
    }

    interface ItemClickListener {
        fun onItemClick(repository: GithubRepo)
    }
}
