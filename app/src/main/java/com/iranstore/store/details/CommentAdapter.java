package com.iranstore.store.details;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iranstore.store.model.Comment;
import com.ss.sevenstore.R;

import java.util.ArrayList;
import java.util.List;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> comments = new ArrayList<>();

    public CommentAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_comment, parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bindComment(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTv;
        private TextView contentTv;
        private TextView dateTv;
        private TextView authorTv;

        public CommentViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.tv_comment_title);
            contentTv = itemView.findViewById(R.id.tv_comment_content);
            dateTv = itemView.findViewById(R.id.tv_comment_date);
            authorTv = itemView.findViewById(R.id.tv_comment_author);
        }

        public void bindComment(Comment comment) {
            titleTv.setText(comment.getTitle());
            contentTv.setText(comment.getContent());
            dateTv.setText(comment.getDate());
            authorTv.setText(comment.getAuthor().getEmail());
        }
    }
}
