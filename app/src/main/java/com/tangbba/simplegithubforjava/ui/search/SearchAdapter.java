package com.tangbba.simplegithubforjava.ui.search;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tangbba.simplegithubforjava.R;
import com.tangbba.simplegithubforjava.api.model.GithubRepo;
import com.tangbba.simplegithubforjava.ui.GlideApp;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter
        extends RecyclerView.Adapter<SearchAdapter.RepositoryHolder> {

    private List<GithubRepo> mItems = new ArrayList<>();
    private ColorDrawable mPlaceHolders = new ColorDrawable(Color.GRAY);

    @Nullable
    private ItemClickListener mListener;

    @NonNull
    @Override
    public RepositoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RepositoryHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RepositoryHolder holder, int position) {
        final GithubRepo repo = mItems.get(position);

        GlideApp.with(holder.itemView.getContext())
                .load(repo.getOwner().getAvatarUrl())
                .placeholder(mPlaceHolders)
                .into(holder.profileImageView);

        holder.nameTextView.setText(repo.getFullName());
        holder.languageTextView.setText(TextUtils.isEmpty(repo.getLanguage())
                ? holder.itemView.getContext().getText(R.string.no_language_specified)
                : repo.getLanguage());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    mListener.onItemClick(repo);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setItems(List<GithubRepo> items) {
        mItems = items;
    }

    public void clearItems() {
        mItems.clear();
    }

    public void setItemClickListener(@Nullable ItemClickListener listener) {
        mListener = listener;
    }

    public static class RepositoryHolder extends RecyclerView.ViewHolder {

        ImageView profileImageView;
        TextView nameTextView;
        TextView languageTextView;

        public RepositoryHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repository, parent, false));

            profileImageView = itemView.findViewById(R.id.profile_image_view);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            languageTextView = itemView.findViewById(R.id.language_text_view);
        }
    }

    public interface ItemClickListener {
        void onItemClick(GithubRepo repository);
    }
}
