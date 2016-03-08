package com.carlise.dribbble.users;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carlise.dribbble.R;
import com.carlise.dribbble.application.BaseToolsBarActivity;
import com.carlise.dribbble.view.RecyclerViewPro.EndlessRecyclerOnScrollListener;
import com.carlise.dribbble.view.RecyclerViewPro.HeaderViewRecyclerAdapter;
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
public class UsersActivity extends BaseToolsBarActivity {
    private static final String TAG = UsersActivity.class.getSimpleName();

    public static final String USERS_TITLE = "title_extra";
    public static final String TITLE_FOLLOWING = "Following";
    public static final String TITLE_FOLLOWER = "Follower";
    public static final String USER_ID = "user_id";

    private RecyclerView recyclerView;
    private RelativeLayout footerLayout;
    private TextView showMore;
    private ProgressBar footProgress;

    private ArrayList<DribleUser> users = new ArrayList<>();
    private HeaderViewRecyclerAdapter recyclerAdapter;

    private ProgressBar progress;

    private LayoutInflater inflater;

    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_users);
        inflater = LayoutInflater.from(this);
        initView();
    }

    private void initView() {

        progress = (ProgressBar) findViewById(R.id.users_progress);

        footerLayout = (RelativeLayout) inflater.inflate(R.layout.footer_users, recyclerView, false);
        AbsListView.LayoutParams footParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
        footerLayout.setLayoutParams(footParams);
        showMore = (TextView) footerLayout.findViewById(R.id.foot_load_more);
        footProgress = (ProgressBar) footerLayout.findViewById(R.id.footer_progress);

        UserListAdapter usersAdapter = new UserListAdapter(this, users);
        recyclerAdapter = new HeaderViewRecyclerAdapter(usersAdapter);
        recyclerAdapter.addFooterView(footerLayout);

        recyclerView = (RecyclerView) findViewById(R.id.users_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);

        requestUsers();

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) recyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int currentPage) {
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
            page++;
        }
    };

    private void resuestFollowing() {
        ApiFactory.getDribleApi().fetchFollowing(getIntent().getIntExtra(USER_ID, 0), page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observable);
    }

    private void requestFollower() {
        ApiFactory.getDribleApi().fetchFollowers(getIntent().getIntExtra(USER_ID, 0), page)
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
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onInitToolBar(Toolbar toolbar) {
        super.onInitToolBar(toolbar);
        setTitle(getIntent().getStringExtra(USERS_TITLE));
    }
}
