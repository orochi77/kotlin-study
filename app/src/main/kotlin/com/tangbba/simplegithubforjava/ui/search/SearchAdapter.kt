package com.tangbba.simplegithubforjava.ui.search

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import com.tangbba.simplegithubforjava.R
import com.tangbba.simplegithubforjava.api.model.GithubRepo
import com.tangbba.simplegithubforjava.ui.GlideApp
import kotlinx.android.synthetic.main.item_repository.view.*
import java.util.*

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.RepositoryHolder>() {

    private var mItems: MutableList<GithubRepo> = mutableListOf()
    private val mPlaceHolders = ColorDrawable(Color.GRAY)

    private var mListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RepositoryHolder(parent)

    override fun onBindViewHolder(holder: RepositoryHolder, position: Int) {

        mItems[position].let { repo ->
            with(holder.itemView) {
                GlideApp.with(context)
                        .load(repo.owner.avatarUrl)
                        .placeholder(mPlaceHolders)
                        .into(profile_image_view)

                name_text_view.text = repo.fullName
                language_text_view.text = if (TextUtils.isEmpty(repo.language))
                    context.getText(R.string.no_language_specified)
                else
                    repo.language

                setOnClickListener { mListener?.onItemClick(repo) }
            }
        }
    }

    override fun getItemCount() = mItems.size

    fun setItems(items: List<GithubRepo>) {
        mItems = items.toMutableList()
    }

    fun clearItems() {
        mItems.clear()
    }

    fun setItemClickListener(listener: ItemClickListener?) {
        mListener = listener
    }

    class RepositoryHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_repository, parent, false))

    interface ItemClickListener {
        fun onItemClick(repository: GithubRepo)
    }
}
