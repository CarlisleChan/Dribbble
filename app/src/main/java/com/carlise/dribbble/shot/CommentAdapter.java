package com.carlise.dribbble.shot;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.carlise.dribbble.R;
import com.carlise.dribbble.users.UserInfoActivity;
import com.carlisle.model.DribleComment;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by zhanglei on 15/8/2.
 */
public class CommentAdapter extends BaseAdapter {

    private ArrayList<DribleComment> mComments;
    private Context mContext;
    private LayoutInflater mInflater;

    public CommentAdapter(Context context, ArrayList<DribleComment> mComments) {
        this.mComments = mComments;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mComments.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final HolderView holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_comment, parent, false);
            holder = new HolderView();
            holder.commentBody = (TextView) convertView.findViewById(R.id.comment_item_body);
            holder.avatar = (SimpleDraweeView) convertView.findViewById(R.id.comment_item_avatar);
            holder.authorName = (TextView) convertView.findViewById(R.id.comment_author_name);
            holder.created_at = (TextView) convertView.findViewById(R.id.comment_created_at);
            convertView.setTag(holder);
        } else {
            holder = (HolderView) convertView.getTag();
        }
        final DribleComment comment = mComments.get(position);
        holder.commentBody.setText(Html.fromHtml(comment.body));
        holder.commentBody.setMovementMethod(LinkMovementMethod.getInstance());

        Uri uri = Uri.parse(comment.user.avatar_url);
        holder.avatar.setImageURI(uri);
        holder.authorName.setText(comment.user.name);
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserInfoActivity.class);
                intent.putExtra(UserInfoActivity.USER_ID_EXTRA, comment.user.id);
                mContext.startActivity(intent);
            }
        });

        holder.authorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserInfoActivity.class);
                intent.putExtra(UserInfoActivity.USER_ID_EXTRA, comment.user.id);
                mContext.startActivity(intent);
            }
        });

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        holder.created_at.setText(formatter.format(comment.created_at.getTime()));
        return convertView;
    }

    class HolderView {
        TextView commentBody;
        SimpleDraweeView avatar;
        TextView authorName;
        TextView created_at;
    }
}
