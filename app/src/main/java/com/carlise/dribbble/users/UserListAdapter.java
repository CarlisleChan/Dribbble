package com.carlise.dribbble.users;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carlise.dribbble.R;
import com.carlisle.model.DribleUser;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 * Created by zhanglei on 15/8/2.
 */
public class UserListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<DribleUser> mUsers;
    private LayoutInflater mInflater;

    public UserListAdapter(Context context, ArrayList<DribleUser> mUsers) {
        this.mContext = context;
        this.mUsers = mUsers;

        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mUsers.size();
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
        final ViewHolder holder;
        if (convertView == null) {
            convertView = (RelativeLayout) mInflater.inflate(R.layout.item_user, parent, false);
            holder = new ViewHolder();
            holder.avatar = (SimpleDraweeView) convertView.findViewById(R.id.user_item_avater);
            holder.userName = (TextView) convertView.findViewById(R.id.user_item_name);
            holder.userDescrip = (TextView) convertView.findViewById(R.id.user_item_description);
            holder.detailText = (TextView) convertView.findViewById(R.id.user_item_detail_btn);
            holder.detailZone = (RelativeLayout) convertView.findViewById(R.id.user_item_detail_zone);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (!TextUtils.isEmpty(mUsers.get(position).avatarUrl)) {
            Uri avatarUri = Uri.parse(mUsers.get(position).avatarUrl);
            holder.avatar.setImageURI(avatarUri);
            final int userId = mUsers.get(position).id;
            holder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserInfoActivity.class);
                    intent.putExtra(UserInfoActivity.USER_ID_EXTRA, userId);
                    mContext.startActivity(intent);
                }
            });
        } else {
            holder.avatar.setImageURI(null);
        }

        if (!TextUtils.isEmpty(mUsers.get(position).name)) {
            holder.userName.setText(mUsers.get(position).name);
        } else {
            holder.userName.setText("");
        }

        if (!TextUtils.isEmpty(mUsers.get(position).bio)) {
            holder.userDescrip.setText(Html.fromHtml(mUsers.get(position).bio));
            holder.userDescrip.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            holder.userDescrip.setText("");
        }
        final int userId = mUsers.get(position).id;

        if (userId != 0) {
            holder.detailText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserInfoActivity.class);
                    intent.putExtra(UserInfoActivity.USER_ID_EXTRA, userId);
                    mContext.startActivity(intent);
                }
            });
        }

        return convertView;
    }

    class ViewHolder {
        SimpleDraweeView avatar;
        TextView userName;
        TextView userDescrip;
        RelativeLayout detailZone;
        TextView detailText;
    }
}
