package com.carlise.dribbble.shot;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carlise.dribbble.R;
import com.carlise.dribbble.users.UserInfoActivity;
import com.carlisle.model.DribleShot;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by zhanglei on 15/7/28.
 */
public class ShotListAdapter extends BaseAdapter {
    private ArrayList<DribleShot> mDribleShots = new ArrayList<DribleShot>();
    private LayoutInflater mInflater;
    private Context mContext;

    public ShotListAdapter(Context context, ArrayList<DribleShot> mDribleShots) {
        this.mDribleShots = mDribleShots;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDribleShots.size();
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
        final ShotViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_shot_list, parent, false);
            holder = new ShotViewHolder();
            holder.itemHeader = (TextView) convertView.findViewById(R.id.item_header_text);
            holder.itemImage = (SimpleDraweeView) convertView.findViewById(R.id.item_image_view);
            holder.itemTitle = (TextView) convertView.findViewById(R.id.item_title);
            holder.itemAuthor = (RelativeLayout) convertView.findViewById(R.id.author);
            holder.itemAvatar = (SimpleDraweeView) convertView.findViewById(R.id.item_author_avatar);
            holder.itemAuthName = (TextView) convertView.findViewById(R.id.item_author_name);
            holder.itemCreate = (TextView) convertView.findViewById(R.id.item_create_date);
            holder.itemLikesCount = (TextView) convertView.findViewById(R.id.item_likes_count);
            convertView.setTag(holder);
        } else {
            holder = (ShotViewHolder) convertView.getTag();
        }
        final DribleShot dribleShot = mDribleShots.get(position);
        if (dribleShot.tags != null && dribleShot.tags.size() > 0 && !TextUtils.isEmpty(dribleShot.tags.get(0))) {
            holder.itemHeader.setText(dribleShot.tags.get(0));
            holder.itemHeader.setVisibility(View.VISIBLE);
        } else {
            holder.itemHeader.setVisibility(View.INVISIBLE);
        }
        String imgStr = dribleShot.images.getUrl();
        Uri imgUri = Uri.parse(imgStr);
        if (imgStr.endsWith(".gif")) {
            setupGif(imgUri, holder.itemImage);
        } else {
            holder.itemImage.setImageURI(imgUri);
        }

        holder.itemTitle.setText(dribleShot.title);
        Uri avatarUri = Uri.parse(dribleShot.user.avatarUrl);
        holder.itemAvatar.setImageURI(avatarUri);
        holder.itemAuthName.setText(dribleShot.user.name);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        holder.itemCreate.setText(formatter.format(dribleShot.createdAt.getTime()));
        holder.itemLikesCount.setText("" + dribleShot.likesCount);

        holder.itemHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "will show list about this tag", Toast.LENGTH_LONG).show();
            }
        });

        holder.itemAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserInfoActivity.class);
                intent.putExtra(UserInfoActivity.USER_ID_EXTRA, dribleShot.user.id);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    private void setupGif(Uri imgUri, SimpleDraweeView imageView) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(imgUri)
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .build();
        imageView.setController(controller);
    }

    class ShotViewHolder {
        TextView itemHeader;
        SimpleDraweeView itemImage;
        TextView itemTitle;
        RelativeLayout itemAuthor;
        SimpleDraweeView itemAvatar;
        TextView itemAuthName;
        TextView itemCreate;
        TextView itemLikesCount;
    }
}
