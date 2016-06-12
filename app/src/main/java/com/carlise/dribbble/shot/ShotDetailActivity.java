package com.carlise.dribbble.shot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carlise.dribbble.BuildConfig;
import com.carlise.dribbble.R;
import com.carlise.dribbble.application.SwipeBackActivity;
import com.carlise.dribbble.users.UserInfoActivity;
import com.carlise.dribbble.utils.AnimatorHelp;
import com.carlise.dribbble.utils.AuthUtil;
import com.carlise.dribbble.utils.ImageHelper;
import com.carlise.dribbble.view.RecyclerViewPro.HeaderViewRecyclerAdapter;
import com.carlisle.model.DribleComment;
import com.carlisle.model.DribleShot;
import com.carlisle.model.MarkResult;
import com.carlisle.provider.ApiFactory;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chengxin on 16/1/7.
 */
public class ShotDetailActivity extends SwipeBackActivity {
    private static final String TAG = ShotDetailActivity.class.getSimpleName();

    public static final String SHOT_ID_EXTRA_FIELD = "shot_extra";

    private int shotId;

    private LayoutInflater inflater;

    private Toolbar toolbar;
    private SimpleDraweeView avatar;
    private TextView authorName;
    private RelativeLayout navShare;
    private RelativeLayout navLike;
    private ImageView navLikeImg;
    private boolean liked;

    private RecyclerView recyclerView;
    private HeaderViewRecyclerAdapter recyclerAdapter;

    private LinearLayout commentsHeader;
    private SimpleDraweeView detailImage;
    private TextView shotTitle;
    private TextView shotDescription;

    private ImageView shotInfoLikeImg;
    private TextView shotInfoLikeText;
    private ImageView shotInfoCommentImg;
    private TextView shotInfoCommentText;
    private ImageView shotInfoViewImg;
    private TextView shotInfoViewText;

    private LinearLayout shotTag;
    private TextView shotTagText;
    private RelativeLayout shotComment;
    private ImageView shotComImg;
    private RelativeLayout shotBucket;
    private ImageView shotBucketImg;

    private LinearLayout shotScrollWall;

    private ArrayList<DribleComment> somments = new ArrayList<>();

    private boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_shot_detail);

        shotId = getIntent().getIntExtra(SHOT_ID_EXTRA_FIELD, -1);
        if (shotId == -1) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "didn't deliver the shot ID");
            }
            //finish();
        }

        initView();
        requestShot();
        requestComments();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!firstTime) {
            requestComments();
        } else {
            firstTime = false;
        }
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolBar(toolbar);

        avatar = (SimpleDraweeView) findViewById(R.id.shot_detail_avatar);
        authorName = (TextView) findViewById(R.id.shot_detail_author);
        navShare = (RelativeLayout) findViewById(R.id.nav_share);
        navLike = (RelativeLayout) findViewById(R.id.nav_like);
        navLikeImg = (ImageView) findViewById(R.id.nav_like_img);
        navLike.setVisibility(View.INVISIBLE);
        detailImage = (SimpleDraweeView) findViewById(R.id.shot_detail_img);

        recyclerView = (RecyclerView) findViewById(R.id.shot_detail_comments_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        inflater = LayoutInflater.from(this);
        commentsHeader = (LinearLayout) inflater.inflate(R.layout.header_shot_detail_comments, recyclerView, false);
        shotScrollWall = (LinearLayout) commentsHeader.findViewById(R.id.shot_detail_scroll_wall);
        shotTitle = (TextView) commentsHeader.findViewById(R.id.shot_detail_title);
        shotDescription = (TextView) commentsHeader.findViewById(R.id.shot_detail_description);
        shotInfoLikeImg = (ImageView) commentsHeader.findViewById(R.id.shot_detail_info_like_img);
        shotInfoLikeImg.setColorFilter(getResources().getColor(R.color.text_default_color));
        shotInfoLikeText = (TextView) commentsHeader.findViewById(R.id.shot_detail_info_like_text);
        shotInfoCommentImg = (ImageView) commentsHeader.findViewById(R.id.shot_detail_info_comment_img);
        shotInfoCommentImg.setColorFilter(getResources().getColor(R.color.text_default_color));
        shotInfoCommentText = (TextView) commentsHeader.findViewById(R.id.shot_detail_info_comment_text);
        shotInfoViewImg = (ImageView) commentsHeader.findViewById(R.id.shot_detail_info_view_img);
        shotInfoViewImg.setColorFilter(getResources().getColor(R.color.text_default_color));
        shotInfoViewText = (TextView) commentsHeader.findViewById(R.id.shot_detail_info_view_text);
        shotTag = (LinearLayout) commentsHeader.findViewById(R.id.shot_detail_tag_zone);
        shotTagText = (TextView) commentsHeader.findViewById(R.id.shot_detail_tag_text);
        shotComment = (RelativeLayout) commentsHeader.findViewById(R.id.shot_detail_comment_zone);
        shotComImg = (ImageView) commentsHeader.findViewById(R.id.shot_detail_comment_zone_img);
        shotComImg.setColorFilter(getResources().getColor(R.color.grey_text));
        shotBucket = (RelativeLayout) commentsHeader.findViewById(R.id.shot_detail_bucket_zone);
        shotBucketImg = (ImageView) commentsHeader.findViewById(R.id.shot_detail_bucket_zone_img);
        shotBucketImg.setColorFilter(getResources().getColor(R.color.grey_text));

        View footer = new View(this);
        AbsListView.LayoutParams footParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.comments_list_foot_height));
        footer.setLayoutParams(footParams);

        CommentAdapter adapter = new CommentAdapter(this, somments);
        recyclerAdapter = new HeaderViewRecyclerAdapter(adapter);
        recyclerAdapter.addHeaderView(commentsHeader);
        recyclerAdapter.addFooterView(footer);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void updateToolbarColor(Bitmap bitmap) {
        Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrant = palette.getVibrantSwatch();
                getActionBar().setBackgroundDrawable(new ColorDrawable(vibrant.getRgb()));
                toolbar.setBackgroundColor(vibrant.getTitleTextColor());
            }
        });
    }

    private int mLoadShotRetryCount = 5;

    private void requestShot() {

        if (AuthUtil.hasLogin(this)) { // because we examine the accesstoken, so will never in this switch
            return;
        }

        ApiFactory.getDribleApi().fetchShotDetail(shotId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DribleShot>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mLoadShotRetryCount > 0) {
                            mLoadShotRetryCount--;
                            requestShot();
                        } else {
                            Toast.makeText(ShotDetailActivity.this, "network errors", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onNext(DribleShot dribleShot) {
                        handleResponse(dribleShot);
                    }
                });
    }

    private void handleResponse(DribleShot dribleShot) {
        fillData(dribleShot);
    }

    private void fillData(final DribleShot shot) {

        if (!TextUtils.isEmpty(shot.user.avatarUrl)) {
            Uri uri = Uri.parse(shot.user.avatarUrl);
            avatar.setImageURI(uri);
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShotDetailActivity.this, UserInfoActivity.class);
                    intent.putExtra(UserInfoActivity.USER_ID_EXTRA, shot.user.id);
                    startActivity(intent);
                }
            });
        }

        if (!TextUtils.isEmpty(shot.user.name)) {
            authorName.setText(shot.user.name);
            authorName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShotDetailActivity.this, UserInfoActivity.class);
                    intent.putExtra(UserInfoActivity.USER_ID_EXTRA, shot.user.id);
                    startActivity(intent);
                }
            });
        }

        if (!TextUtils.isEmpty(shot.htmlUrl)) {
            navShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String shareUrl = shot.htmlUrl;
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
                    startActivity(shareIntent);
                }
            });
        }
        checkIfLike();
        navLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canChangeLike) {
                    if (!liked) {
                        requestLike();
                        AnimatorHelp.btnClick(navLikeImg, 300);
                        Toast.makeText(ShotDetailActivity.this, "requesting", Toast.LENGTH_SHORT).show();
                    } else {
                        requestUnLike();
                        AnimatorHelp.btnClick(navLikeImg, 300);
                        Toast.makeText(ShotDetailActivity.this, "requesting", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        Uri uri = Uri.parse(shot.images.getUrl());
        ImageHelper.setupImage(getResources(), uri, uri, detailImage);

        if (!TextUtils.isEmpty(shot.title)) {
            shotTitle.setText(shot.title);
        }

        if (!TextUtils.isEmpty(shot.description) && !shot.description.equals("null")) {
            shotDescription.setText(Html.fromHtml(shot.description));
            shotDescription.setMovementMethod(LinkMovementMethod.getInstance());
        }

        shotInfoLikeText.setText(String.valueOf(shot.likesCount));
        shotInfoCommentText.setText(String.valueOf(shot.commentsCount));
        shotInfoViewText.setText(String.valueOf(shot.viewsCount));

        ArrayList<String> tags = shot.tags;
        if (tags.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : tags) {
                stringBuilder.append(s + ", ");
            }

            shotTagText.setText(stringBuilder.toString().substring(0, stringBuilder.length() - 2));
        } else {
            shotTagText.setText("No one tag");
        }

        shotComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShotDetailActivity.this, InputActivity.class);
                intent.putExtra(InputActivity.INPUT_NAME, "Comment");
                intent.putExtra(InputActivity.SHOT_ID, shotId);
                startActivity(intent);
            }
        });

        shotBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShotDetailActivity.this, "developing...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfLike() {
        if (AuthUtil.hasLogin(this)) {
            navLikeImg.clearColorFilter();
            liked = false;
            return;
        }

        ApiFactory.getDribleApi().checkIfLike(shotId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MarkResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        updateLikeView(false);
                        navLike.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(MarkResult markResult) {
                        updateLikeView(true);
                        navLike.setVisibility(View.VISIBLE);
                    }
                });
    }

    private boolean canChangeLike = true;

    private void requestLike() {
        ApiFactory.getDribleApi().requestLike(shotId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MarkResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(MarkResult markResult) {
                        updateLikeView(true);
                        canChangeLike = true;
                        Toast.makeText(ShotDetailActivity.this, "Liked", Toast.LENGTH_SHORT).show();
                    }
                });

        canChangeLike = false;
    }

    private void requestUnLike() {
        ApiFactory.getDribleApi().requestUnLike(shotId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MarkResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(MarkResult markResult) {
                        updateLikeView(false);
                        canChangeLike = true;
                        Toast.makeText(ShotDetailActivity.this, "Unliked", Toast.LENGTH_SHORT).show();
                    }
                });
        canChangeLike = false;
    }

    private void updateLikeView(boolean like) {
        if (like) {
            navLikeImg.setColorFilter(getResources().getColor(R.color.pretty_red));
            liked = true;
        } else {
            navLikeImg.clearColorFilter();
            liked = false;
        }
    }

    private void requestComments() {
        ApiFactory.getDribleApi().fetchComments(shotId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<DribleComment>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(ShotDetailActivity.this, "error when get comments", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<DribleComment> dribleComments) {
                        handleComments(dribleComments);
                    }
                });

    }

    private void handleComments(List<DribleComment> dribleComments) {
        if (dribleComments != null && dribleComments.size() <= 0) {
            return;
        }
        somments.clear();
        somments.addAll(dribleComments);
        recyclerAdapter.notifyDataSetChanged();
    }

    public void initToolBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
