package com.carlise.dribbble.shot;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carlise.dribbble.R;
import com.carlise.dribbble.users.UserInfoActivity;
import com.carlisle.model.DribleComment;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by zhanglei on 15/8/2.
 */
public class CommentAdapter extends RecyclerView.Adapter {

    private List<DribleComment> comments;
    private Context context;

    public CommentAdapter(Context context, List<DribleComment> comments) {
        this.comments = comments;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CommentViewHolder commentVH = (CommentViewHolder) holder;
        final DribleComment comment = comments.get(position);
        commentVH.commentBody.setText(Html.fromHtml(comment.body));
        commentVH.commentBody.setMovementMethod(LinkMovementMethod.getInstance());

        Uri uri = Uri.parse(comment.user.avatarUrl);
        commentVH.avatar.setImageURI(uri);
        commentVH.authorName.setText(comment.user.name);
        commentVH.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.putExtra(UserInfoActivity.USER_ID_EXTRA, comment.user.id);
                context.startActivity(intent);
            }
        });

        commentVH.authorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.putExtra(UserInfoActivity.USER_ID_EXTRA, comment.user.id);
                context.startActivity(intent);
            }
        });

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        commentVH.createdAt.setText(formatter.format(comment.createdAt.getTime()));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView commentBody;
        SimpleDraweeView avatar;
        TextView authorName;
        TextView createdAt;

        public CommentViewHolder(View itemView) {
            super(itemView);
            commentBody = (TextView) itemView.findViewById(R.id.comment_item_body);
            avatar = (SimpleDraweeView) itemView.findViewById(R.id.comment_item_avatar);
            authorName = (TextView) itemView.findViewById(R.id.comment_author_name);
            createdAt = (TextView) itemView.findViewById(R.id.comment_created_at);
        }
    }
}
