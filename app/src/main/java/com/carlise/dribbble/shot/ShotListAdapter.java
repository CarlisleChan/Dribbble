package com.carlise.dribbble.shot;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

/**
 * Created by chengxin on 16/3/7.
 */
public class ShotListAdapter extends RecyclerView.Adapter {
    private List<DribleShot> dribleShots = new ArrayList<DribleShot>();
    private Context context;
    private OnClickListener listener;

    public ShotListAdapter(Context context) {
        this.context = context;
    }

    public ShotListAdapter(Context context, List<DribleShot> dribleShots) {
        this.dribleShots = dribleShots;
        this.context = context;
    }

    public void setData(List<DribleShot> dribleShots) {
        this.dribleShots = dribleShots;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ShotViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shot_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ShotViewHolder shotViewHolder = (ShotViewHolder) holder;
        final DribleShot dribleShot = dribleShots.get(position);
        if (dribleShot.tags != null && dribleShot.tags.size() > 0 && !TextUtils.isEmpty(dribleShot.tags.get(0))) {
            shotViewHolder.itemHeader.setText(dribleShot.tags.get(0));
            shotViewHolder.itemHeader.setVisibility(View.VISIBLE);
        } else {
            shotViewHolder.itemHeader.setVisibility(View.INVISIBLE);
        }
        String imgStr = dribleShot.images.getUrl();
        Uri imgUri = Uri.parse(imgStr);
        if (imgStr.endsWith(".gif")) {
            setupGif(imgUri, shotViewHolder.itemImage);
        } else {
            shotViewHolder.itemImage.setImageURI(imgUri);
        }

        shotViewHolder.itemTitle.setText(dribleShot.title);
        Uri avatarUri = Uri.parse(dribleShot.user.avatarUrl);
        shotViewHolder.itemAvatar.setImageURI(avatarUri);
        shotViewHolder.itemAuthName.setText(dribleShot.user.name);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        shotViewHolder.itemCreate.setText(formatter.format(dribleShot.createdAt.getTime()));
        shotViewHolder.itemLikesCount.setText("" + dribleShot.likesCount);

        shotViewHolder.itemHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "will show list about this tag", Toast.LENGTH_LONG).show();
            }
        });

        shotViewHolder.itemAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.putExtra(UserInfoActivity.USER_ID_EXTRA, dribleShot.user.id);
                context.startActivity(intent);
            }
        });

        shotViewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dribleShots.size();
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

    static class ShotViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout itemLayout;
        TextView itemHeader;
        SimpleDraweeView itemImage;
        TextView itemTitle;
        RelativeLayout itemAuthor;
        SimpleDraweeView itemAvatar;
        TextView itemAuthName;
        TextView itemCreate;
        TextView itemLikesCount;

        public ShotViewHolder(View itemView) {
            super(itemView);
            itemLayout = (RelativeLayout) itemView.findViewById(R.id.rl_item);
            itemHeader = (TextView) itemView.findViewById(R.id.item_header_text);
            itemImage = (SimpleDraweeView) itemView.findViewById(R.id.item_image_view);
            itemTitle = (TextView) itemView.findViewById(R.id.item_title);
            itemAuthor = (RelativeLayout) itemView.findViewById(R.id.author);
            itemAvatar = (SimpleDraweeView) itemView.findViewById(R.id.item_author_avatar);
            itemAuthName = (TextView) itemView.findViewById(R.id.item_author_name);
            itemCreate = (TextView) itemView.findViewById(R.id.item_create_date);
            itemLikesCount = (TextView) itemView.findViewById(R.id.item_likes_count);
        }
    }

    public interface OnClickListener {
        public void onClick(View view, int position);
    }
}
