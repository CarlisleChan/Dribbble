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
import com.carlise.dribbble.application.BaseActivity;
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
public class UserInfoActivity extends BaseActivity {
    private static final String TAG = UserInfoActivity.class.getSimpleName();

    private RelativeLayout mFollowZone;
    private TextView mFollowText;
    private boolean mFollowed = false;
    private boolean mCanChangeFollow = false;

    private SwipeRefreshLayout mSwipeRefresh;
    private boolean mCanLoadMore = true;
    private ListView mList;
    private RelativeLayout mHeader;
    private RelativeLayout mFooter;
    private ProgressBar mFootProgress;
    private ArrayList<DribleShot> mShots = new ArrayList<DribleShot>();
    private ShotListAdapter mShotAdapter;
    private LayoutInflater mInflater;

    private TextView mNavName;
    private SimpleDraweeView mUserAvatar;
    private TextView mUserName;
    private TextView mUserFollowerC;
    private RelativeLayout mUserFollowerZone;
    private TextView mUserFollowingC;
    private RelativeLayout mUserFollowingZone;
    private RelativeLayout mUserElseZone;

    private TextView mUserBio;

    private int mUserId;
    private DribleUser mDribleUser;
    public static final String USER_ID_EXTRA = "userId_extra";

    private RelativeLayout mProgressZone;

    private Handler mHandler = new Handler();

    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_user_info);
        mInflater = LayoutInflater.from(this);
        mUserId = getIntent().getIntExtra(USER_ID_EXTRA, 69311);
        initView();

        requestUserInfo();
        if (AuthUtil.getMe(this).id != mUserId) {
            checkIfFollowing();
        }
    }

    private void initView() {
        mNavName = (TextView) findViewById(R.id.user_info_nav_name);
        mNavName.setAlpha(0x0);

        mFollowZone = (RelativeLayout) findViewById(R.id.nav_follow);
        mFollowText = (TextView) findViewById(R.id.nav_follow_text);
        mFollowZone.setVisibility(View.INVISIBLE);

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.user_info_swipe);
        mList = (ListView) findViewById(R.id.user_info_list);

        mHeader = (RelativeLayout) mInflater.inflate(R.layout.drawer_user_info_header, mList, false);
        mList.addHeaderView(mHeader);
        mList.setDivider(null);

        mFooter = (RelativeLayout) mInflater.inflate(R.layout.footer_home_list, mList, false);
        mFootProgress = (ProgressBar) mFooter.findViewById(R.id.footer_progress);
        AbsListView.LayoutParams footParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
        mFooter.setLayoutParams(footParams);
        mList.addFooterView(mFooter);

        mShotAdapter = new ShotListAdapter(this, mShots);
        mList.setAdapter(mShotAdapter);

        if (AuthUtil.getMe(this).id != mUserId) {
            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(UserInfoActivity.this, ShotDetailActivity.class);
                    intent.putExtra(ShotDetailActivity.SHOT_ID_EXTRA_FIELD, mShots.get(position - 1).id);
                    startActivity(intent);
                }
            });
        }

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestUserShots();
            }
        });
        mSwipeRefresh.setProgressViewOffset(true, (int) getResources().getDimension(R.dimen.toolbar_height),
                (int) (getResources().getDimension(R.dimen.toolbar_height) + 150));

        mSwipeRefresh.setColorSchemeResources(R.color.pretty_blue,
                R.color.pretty_green);

        mList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                handleNavNameAlpha(firstVisibleItem);


                if (!mList.canScrollVertically(1) && (firstVisibleItem + visibleItemCount == totalItemCount)
                        && firstVisibleItem > 0 && mCanLoadMore) {
                    mCanLoadMore = false;
                    requestUserShots();
                }
            }
        });

        mUserAvatar = (SimpleDraweeView) findViewById(R.id.user_info_avatar);
        mUserName = (TextView) findViewById(R.id.user_info_name);
        mUserFollowerZone = (RelativeLayout) findViewById(R.id.user_info_follower_zone);
        mUserFollowerC = (TextView) findViewById(R.id.follower_count);
        mUserFollowingZone = (RelativeLayout) findViewById(R.id.user_info_following_zone);
        mUserFollowingC = (TextView) findViewById(R.id.following_count);
        mUserElseZone = (RelativeLayout) findViewById(R.id.user_info_else);
        mUserBio = (TextView) findViewById(R.id.user_info_bio_text);

        mProgressZone = (RelativeLayout) findViewById(R.id.progress_zone);
        mProgressZone.setVisibility(View.VISIBLE);

        mFollowZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCanChangeFollow) {
                    if (mFollowed) {
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
        int height = mHeader.getHeight();
        int delta = height - mHeader.getBottom();
        if (delta >= height / 2 && delta < height * 3 / 4) {
            float ratio = (delta - height / 2) / (float) (height / 4);
            ratio = Math.min(ratio, 1);
            mNavName.setAlpha(ratio);
        } else if (delta < height / 2) {
            mNavName.setAlpha(0);
        } else {
            mNavName.setAlpha(1);
        }
    }

    private void requestUserInfo() {
        ApiFactory.getDribleApi().getUserInfo(mUserId)
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
        mDribleUser = user;
        // Log.e("userinfo: " + user);
        if (!TextUtils.isEmpty(user.name)) {
            mNavName.setText(user.name);
            mUserName.setText(user.name);
        }

        if (!TextUtils.isEmpty(user.avatar_url)) {
            Uri avatarUri = Uri.parse(user.avatar_url);
            mUserAvatar.setImageURI(avatarUri);
        }

        int followerCount = user.followers_count;
        String followerCStr = followerCount > 1000 ? (String.valueOf(followerCount / 1000) + "K") : String.valueOf(followerCount);
        mUserFollowerC.setText(followerCStr);
        int followingCount = user.followings_count;
        String followingCStr = followingCount > 1000 ? (String.valueOf(followingCount / 1000) + "K") : String.valueOf(followingCount);
        mUserFollowingC.setText(followingCStr);

        mUserFollowerZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, UsersActivity.class);
                intent.putExtra(UsersActivity.USERS_TITLE, UsersActivity.TITLE_FOLLOWER);
                startActivity(intent);
            }
        });
        mUserFollowingZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, UsersActivity.class);
                intent.putExtra(UsersActivity.USERS_TITLE, UsersActivity.TITLE_FOLLOWING);
                startActivity(intent);
            }
        });

        mUserElseZone.setOnClickListener(new View.OnClickListener() {
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
            mUserBio.setText(Html.fromHtml(user.bio));
            mUserBio.setMovementMethod(LinkMovementMethod.getInstance());
        }

        mProgressZone.setVisibility(View.INVISIBLE);
        requestUserShots();
        mSwipeRefresh.setRefreshing(true);

    }

    private void checkIfFollowing() {
        ApiFactory.getDribleApi().checkIfMeFollow(mUserId, new Callback<CheckFollowResult>() {
            @Override
            public void success(CheckFollowResult checkFollowResult, retrofit.client.Response response) {
                if (response.getStatus() != 0 && response.getStatus() == 204) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCanChangeFollow = true;
                            updateFollowView(true);
                            mFollowZone.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {
                mCanChangeFollow = true;
                int statusCode = error.getResponse().getStatus();
                if (statusCode != 0 && statusCode == 404) {
                    updateFollowView(false);
                }

                mFollowZone.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateFollowView(boolean followed) {
        if (followed) {
            mFollowed = true;
            mFollowText.setText("Following");
            mFollowText.setTextColor(getResources().getColor(R.color.content_back));
            mFollowText.setBackgroundResource(R.drawable.following_btn_back);
        } else {
            mFollowed = false;
            mFollowText.setText("Follow");
            mFollowText.setTextColor(getResources().getColor(R.color.pretty_green));
            mFollowText.setBackgroundResource(R.drawable.unfollow_btn_back);
        }
    }

    private void requestFollow() {
        ApiFactory.getDribleApi().requestFollow(mUserId, new Callback<FollowResult>() {
            @Override
            public void success(FollowResult followResult, retrofit.client.Response response) {
                mCanChangeFollow = true;
                final boolean success = response.getStatus() == 204;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {
                            Toast.makeText(UserInfoActivity.this, "follow success", Toast.LENGTH_SHORT).show();
                            updateFollowView(true);
                        } else {
                            Toast.makeText(UserInfoActivity.this, "reqeust fail", Toast.LENGTH_SHORT).show();
                            mCanChangeFollow = true;
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
        ApiFactory.getDribleApi().cancelFollow(mUserId, new Callback<FollowResult>() {
            @Override
            public void success(FollowResult followResult, retrofit.client.Response response) {
                mCanChangeFollow = true;
                final boolean success = response.getStatus() == 204;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {
                            Toast.makeText(UserInfoActivity.this, "unfollow success", Toast.LENGTH_SHORT).show();
                            updateFollowView(false);
                        } else {
                            Toast.makeText(UserInfoActivity.this, "reqeust fail", Toast.LENGTH_SHORT).show();
                            mCanChangeFollow = true;
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
        mCanChangeFollow = false;
    }

    private void requestUserShots() {
        ApiFactory.getDribleApi().fetchUserShots(mUserId, page)
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
                mSwipeRefresh.setRefreshing(false);
            }
        }, 30000);

        if (page != 1) {
            mFootProgress.setVisibility(View.VISIBLE);
        }
    }

    private void handleUserShots(List<DribleShot> dribleShots) {
        mSwipeRefresh.setRefreshing(false);
        if (dribleShots.size() <= 0) {
            mCanLoadMore = false;
            mList.setOnItemClickListener(null);
            TextView noShots = new TextView(this);
            noShots.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            noShots.setText("No shots yet");
            noShots.setTextColor(getResources().getColor(R.color.grey_text));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            noShots.setLayoutParams(params);
            mFooter.addView(noShots);
            return;
        }

        if (page == 1) {
            mShots.clear();
        }

        for (DribleShot dribleShot : dribleShots) {
            dribleShot.user = mDribleUser;
            mShots.add(dribleShot);
        }

        mShotAdapter.notifyDataSetChanged();
        mCanLoadMore = true;
        mFootProgress.setVisibility(View.INVISIBLE);
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
