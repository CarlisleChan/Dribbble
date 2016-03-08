package com.carlise.dribbble.users;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carlise.dribbble.R;
import com.carlisle.model.DribleUser;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 * Created by chengxin on 16/3/8.
 */
public class UserListAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<DribleUser> users;

    public UserListAdapter(Context context, ArrayList<DribleUser> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UserListViewHolder userListVH = (UserListViewHolder) holder;
        if (!TextUtils.isEmpty(users.get(position).avatarUrl)) {
            Uri avatarUri = Uri.parse(users.get(position).avatarUrl);
            userListVH.avatar.setImageURI(avatarUri);
            final int userId = users.get(position).id;
            userListVH.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserInfoActivity.class);
                    intent.putExtra(UserInfoActivity.USER_ID_EXTRA, userId);
                    context.startActivity(intent);
                }
            });
        } else {
            userListVH.avatar.setImageURI(null);
        }

        if (!TextUtils.isEmpty(users.get(position).name)) {
            userListVH.userName.setText(users.get(position).name);
        } else {
            userListVH.userName.setText("");
        }

        if (!TextUtils.isEmpty(users.get(position).bio)) {
            userListVH.userDescrip.setText(Html.fromHtml(users.get(position).bio));
            userListVH.userDescrip.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            userListVH.userDescrip.setText("");
        }
        final int userId = users.get(position).id;

        if (userId != 0) {
            userListVH.detailText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserInfoActivity.class);
                    intent.putExtra(UserInfoActivity.USER_ID_EXTRA, userId);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UserListViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView avatar;
        TextView userName;
        TextView userDescrip;
        RelativeLayout detailZone;
        TextView detailText;

        public UserListViewHolder(View itemView) {
            super(itemView);
            avatar = (SimpleDraweeView) itemView.findViewById(R.id.user_item_avater);
            userName = (TextView) itemView.findViewById(R.id.user_item_name);
            userDescrip = (TextView) itemView.findViewById(R.id.user_item_description);
            detailText = (TextView) itemView.findViewById(R.id.user_item_detail_btn);
            detailZone = (RelativeLayout) itemView.findViewById(R.id.user_item_detail_zone);
        }
    }
}
