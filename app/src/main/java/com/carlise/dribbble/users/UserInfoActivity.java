package com.carlise.dribbble.users;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carlise.dribbble.BuildConfig;
import com.carlise.dribbble.R;
import com.carlise.dribbble.application.BaseToolsBarActivity;
import com.carlise.dribbble.shot.ShotDetailActivity;
import com.carlise.dribbble.shot.ShotListAdapter;
import com.carlise.dribbble.utils.AuthUtil;
import com.carlisle.model.CheckFollowResult;
import com.carlisle.model.DribleShot;
import com.carlisle.model.DribleUser;
import com.carlisle.model.FollowResult;
import com.carlisle.provider.ApiFactory;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zhanglei on 15/7/24.
 */
public class UserInfoActivity extends BaseToolsBarActivity {
    private static final String TAG = UserInfoActivity.class.getSimpleName();

    private RelativeLayout followZone;
    private TextView followText;
    private boolean followed = false;
    private boolean canChangeFollow = false;

    private SwipeRefreshLayout refreshLayout;
    private boolean canLoadMore = true;
    private ListView listView;
    private RelativeLayout headerLayout;
    private RelativeLayout footerLayout;
    private ProgressBar footProgress;
    private ArrayList<DribleShot> dribleShots = new ArrayList<DribleShot>();
    private ShotListAdapter shotAdapter;
    private LayoutInflater inflater;

    private TextView navName;
    private SimpleDraweeView userAvatar;
    private TextView userName;
    private TextView userFollowerC;
    private RelativeLayout userFollowerZone;
    private TextView userFollowingC;
    private RelativeLayout userFollowingZone;
    private RelativeLayout userElseZone;

    private TextView userBio;

    private int userId;
    private DribleUser dribleUser;
    public static final String USER_ID_EXTRA = "userId_extra";

    private RelativeLayout progressZone;

    private Handler handler = new Handler();

    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_user_info);
        inflater = LayoutInflater.from(this);
        userId = getIntent().getIntExtra(USER_ID_EXTRA, 69311);
        initView();

        requestUserInfo();
        if (AuthUtil.getMe(this).id != userId) {
            checkIfFollowing();
        }
    }

    private void initView() {
        navName = (TextView) findViewById(R.id.user_info_nav_name);
        navName.setAlpha(0x0);

        followZone = (RelativeLayout) findViewById(R.id.nav_follow);
        followText = (TextView) findViewById(R.id.nav_follow_text);
        followZone.setVisibility(View.INVISIBLE);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.user_info_swipe);
        listView = (ListView) findViewById(R.id.user_info_list);

        headerLayout = (RelativeLayout) inflater.inflate(R.layout.drawer_user_info_header, listView, false);
        listView.addHeaderView(headerLayout);
        listView.setDivider(null);

        footerLayout = (RelativeLayout) inflater.inflate(R.layout.footer_home_list, listView, false);
        footProgress = (ProgressBar) footerLayout.findViewById(R.id.footer_progress);
        AbsListView.LayoutParams footParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
        footerLayout.setLayoutParams(footParams);
        listView.addFooterView(footerLayout);

        shotAdapter = new ShotListAdapter(this, dribleShots);
        listView.setAdapter(shotAdapter);

        if (AuthUtil.getMe(this).id != userId) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(UserInfoActivity.this, ShotDetailActivity.class);
                    intent.putExtra(ShotDetailActivity.SHOT_ID_EXTRA_FIELD, dribleShots.get(position - 1).id);
                    startActivity(intent);
                }
            });
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestUserShots();
            }
        });
        refreshLayout.setProgressViewOffset(true, (int) getResources().getDimension(R.dimen.toolbar_height),
                (int) (getResources().getDimension(R.dimen.toolbar_height) + 150));

        refreshLayout.setColorSchemeResources(R.color.pretty_blue,
                R.color.pretty_green);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                handleNavNameAlpha(firstVisibleItem);


                if (!listView.canScrollVertically(1) && (firstVisibleItem + visibleItemCount == totalItemCount)
                        && firstVisibleItem > 0 && canLoadMore) {
                    canLoadMore = false;
                    requestUserShots();
                }
            }
        });

        userAvatar = (SimpleDraweeView) findViewById(R.id.user_info_avatar);
        userName = (TextView) findViewById(R.id.user_info_name);
        userFollowerZone = (RelativeLayout) findViewById(R.id.user_info_follower_zone);
        userFollowerC = (TextView) findViewById(R.id.follower_count);
        userFollowingZone = (RelativeLayout) findViewById(R.id.user_info_following_zone);
        userFollowingC = (TextView) findViewById(R.id.following_count);
        userElseZone = (RelativeLayout) findViewById(R.id.user_info_else);
        userBio = (TextView) findViewById(R.id.user_info_bio_text);

        progressZone = (RelativeLayout) findViewById(R.id.progress_zone);
        progressZone.setVisibility(View.VISIBLE);

        followZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canChangeFollow) {
                    if (followed) {
                        requestChangeFollow(false);
                        updateFollowView(false);
                    } else {
                        requestChangeFollow(true);
                        updateFollowView(true);
                    }
                }
            }
        });
    }

    private void handleNavNameAlpha(int firstVisibleItem) {
        int height = headerLayout.getHeight();
        int delta = height - headerLayout.getBottom();
        if (delta >= height / 2 && delta < height * 3 / 4) {
            float ratio = (delta - height / 2) / (float) (height / 4);
            ratio = Math.min(ratio, 1);
            navName.setAlpha(ratio);
        } else if (delta < height / 2) {
            navName.setAlpha(0);
        } else {
            navName.setAlpha(1);
        }
    }

    private void requestUserInfo() {
        ApiFactory.getDribleApi().getUserInfo(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DribleUser>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(UserInfoActivity.this, "get userinfo error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNext(DribleUser dribleUser) {
                        handleUserInfo(dribleUser);
                    }
                });
    }

    private void handleUserInfo(final DribleUser user) {
        dribleUser = user;
        // Log.e("userinfo: " + user);
        if (!TextUtils.isEmpty(user.name)) {
            navName.setText(user.name);
            userName.setText(user.name);
        }

        if (!TextUtils.isEmpty(user.avatarUrl)) {
            Uri avatarUri = Uri.parse(user.avatarUrl);
            userAvatar.setImageURI(avatarUri);
        }

        int followerCount = user.followersCount;
        String followerCStr = followerCount > 1000 ? (String.valueOf(followerCount / 1000) + "K") : String.valueOf(followerCount);
        userFollowerC.setText(followerCStr);
        int followingCount = user.followingsCount;
        String followingCStr = followingCount > 1000 ? (String.valueOf(followingCount / 1000) + "K") : String.valueOf(followingCount);
        userFollowingC.setText(followingCStr);

        userFollowerZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, UsersActivity.class);
                intent.putExtra(UsersActivity.USERS_TITLE, UsersActivity.TITLE_FOLLOWER);
                startActivity(intent);
            }
        });
        userFollowingZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, UsersActivity.class);
                intent.putExtra(UsersActivity.USERS_TITLE, UsersActivity.TITLE_FOLLOWING);
                startActivity(intent);
            }
        });

        userElseZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.links != null && !TextUtils.isEmpty(user.links.twitter)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(user.links.twitter));
                    startActivity(intent);
                }
            }
        });

        if (!TextUtils.isEmpty(user.bio)) {
            userBio.setText(Html.fromHtml(user.bio));
            userBio.setMovementMethod(LinkMovementMethod.getInstance());
        }

        progressZone.setVisibility(View.INVISIBLE);
        requestUserShots();
        refreshLayout.setRefreshing(true);

    }

    private void checkIfFollowing() {
        ApiFactory.getDribleApi().checkIfMeFollow(userId, new Callback<CheckFollowResult>() {
            @Override
            public void success(CheckFollowResult checkFollowResult, retrofit.client.Response response) {
                if (response.getStatus() != 0 && response.getStatus() == 204) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            canChangeFollow = true;
                            updateFollowView(true);
                            followZone.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {
                canChangeFollow = true;
                int statusCode = error.getResponse().getStatus();
                if (statusCode != 0 && statusCode == 404) {
                    updateFollowView(false);
                }

                followZone.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateFollowView(boolean followed) {
        if (followed) {
            this.followed = true;
            followText.setText("Following");
            followText.setTextColor(getResources().getColor(R.color.content_back));
            followText.setBackgroundResource(R.drawable.following_btn_back);
        } else {
            this.followed = false;
            followText.setText("Follow");
            followText.setTextColor(getResources().getColor(R.color.pretty_green));
            followText.setBackgroundResource(R.drawable.unfollow_btn_back);
        }
    }

    private void requestFollow() {
        ApiFactory.getDribleApi().requestFollow(userId, new Callback<FollowResult>() {
            @Override
            public void success(FollowResult followResult, retrofit.client.Response response) {
                canChangeFollow = true;
                final boolean success = response.getStatus() == 204;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {
                            Toast.makeText(UserInfoActivity.this, "follow success", Toast.LENGTH_SHORT).show();
                            updateFollowView(true);
                        } else {
                            Toast.makeText(UserInfoActivity.this, "reqeust fail", Toast.LENGTH_SHORT).show();
                            canChangeFollow = true;
                            updateFollowView(false);
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void cancelFollow() {
        ApiFactory.getDribleApi().cancelFollow(userId, new Callback<FollowResult>() {
            @Override
            public void success(FollowResult followResult, retrofit.client.Response response) {
                canChangeFollow = true;
                final boolean success = response.getStatus() == 204;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {
                            Toast.makeText(UserInfoActivity.this, "unfollow success", Toast.LENGTH_SHORT).show();
                            updateFollowView(false);
                        } else {
                            Toast.makeText(UserInfoActivity.this, "reqeust fail", Toast.LENGTH_SHORT).show();
                            canChangeFollow = true;
                            updateFollowView(true);
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void requestChangeFollow(final boolean follow) {
        if (follow) {
            requestFollow();
        } else {
            cancelFollow();
        }
        canChangeFollow = false;
    }

    private void requestUserShots() {
        ApiFactory.getDribleApi().fetchUserShots(userId, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<DribleShot>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<DribleShot> dribleShots) {
                        handleUserShots(dribleShots);
                        page++;
                    }
                });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        }, 30000);

        if (page != 1) {
            footProgress.setVisibility(View.VISIBLE);
        }
    }

    private void handleUserShots(List<DribleShot> dribleShots) {
        refreshLayout.setRefreshing(false);
        if (dribleShots.size() <= 0) {
            canLoadMore = false;
            listView.setOnItemClickListener(null);
            TextView noShots = new TextView(this);
            noShots.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            noShots.setText("No shots yet");
            noShots.setTextColor(getResources().getColor(R.color.grey_text));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            noShots.setLayoutParams(params);
            footProgress.setVisibility(View.INVISIBLE);
            footerLayout.addView(noShots);
            return;
        }

        if (page == 1) {
            this.dribleShots.clear();
        }

        for (DribleShot dribleShot : dribleShots) {
            dribleShot.user = dribleUser;
            this.dribleShots.add(dribleShot);
        }

        shotAdapter.notifyDataSetChanged();
        footProgress.setVisibility(View.INVISIBLE);
        canLoadMore = true;
    }

    @Override
    public void onInitToolBar(Toolbar toolbar) {
        super.onInitToolBar(toolbar);
        RelativeLayout viewGroup = (RelativeLayout) toolbar.findViewById(R.id.toolbar_container);
        View view = LayoutInflater.from(this).inflate(R.layout.title_user_info, viewGroup, false);
        viewGroup.removeAllViews();
        viewGroup.addView(view);
    }
}
