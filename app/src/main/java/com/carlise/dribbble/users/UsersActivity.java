package com.carlise.dribbble.users;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carlise.dribbble.R;
import com.carlise.dribbble.application.BaseActivity;
import com.carlise.dribbble.utils.UserHelper;
import com.carlisle.model.DribleUser;
import com.carlisle.model.FollowResult;
import com.carlisle.provider.ApiFactory;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chengxin on 16/1/11.
 */
public class UsersActivity extends BaseActivity {
    private static final String TAG = UsersActivity.class.getSimpleName();

    public static final String USERS_TITLE = "title_extra";
    public static final String TITLE_FOLLOWING = "Following";
    public static final String TITLE_FOLLOWER = "Follower";

    private ListView listView;
    private RelativeLayout footerLayout;
    private TextView showMore;
    private ProgressBar footProgress;

    private ArrayList<DribleUser> users = new ArrayList<>();
    private UserListAdapter usersAdapter;

    private ProgressBar progress;

    private LayoutInflater inflater;

    private int page = 1;
    private boolean canLoadMore = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_users);
        inflater = LayoutInflater.from(this);
        initView();
    }

    private void initView() {

        listView = (ListView) findViewById(R.id.users_list);

        progress = (ProgressBar) findViewById(R.id.users_progress);

        footerLayout = (RelativeLayout) inflater.inflate(R.layout.users_foot, listView, false);
        showMore = (TextView) footerLayout.findViewById(R.id.foot_load_more);

        footProgress = (ProgressBar) footerLayout.findViewById(R.id.footer_progress);

        listView.addFooterView(footerLayout);

        usersAdapter = new UserListAdapter(this, users);
        listView.setAdapter(usersAdapter);
        listView.setDivider(null);

        requestUsers();

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!listView.canScrollVertically(1) && firstVisibleItem > 0 && canLoadMore && page != 1) {
                    showMore.setVisibility(View.VISIBLE);
                    footerLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showMore.setVisibility(View.INVISIBLE);
                            footProgress.setVisibility(View.VISIBLE);
                            requestUsers();
                        }
                    });
                }
            }
        });
    }

    private Observer<List<FollowResult>> observable = new Observer<List<FollowResult>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            footProgress.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onNext(List<FollowResult> followResults) {
            footProgress.setVisibility(View.INVISIBLE);
            handleUsers(followResults);

            if (!followResults.isEmpty()) {
                canLoadMore = true;
            } else {
                canLoadMore = false;
            }

            page++;
        }
    };

    private void resuestFollowing() {
        ApiFactory.getDribleApi().fetchFollowing(UserHelper.getInstance(this).getDribleUser().id, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observable);
    }

    private void requestFollower() {
        ApiFactory.getDribleApi().fetchFollowers(UserHelper.getInstance(this).getDribleUser().id, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observable);
    }

    private void requestUsers() {
        if (getIntent().getStringExtra(USERS_TITLE).equals(TITLE_FOLLOWING)) {
            resuestFollowing();
        } else {
            requestFollower();
        }
    }

    private void handleUsers(List<FollowResult> followResults) {
        progress.setVisibility(View.INVISIBLE);
        if (followResults.size() <= 0) {
            return;
        }

        for (FollowResult followResult : followResults) {
            users.add(followResult.getFollow());
        }
        usersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onInitToolBar(Toolbar toolbar) {
        super.onInitToolBar(toolbar);
        setTitle(getIntent().getStringExtra(USERS_TITLE));
    }
}
