package com.carlise.dribbble.shot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.carlise.dribbble.utils.UserHelper;
import com.carlise.dribbble.view.RecyclerViewPro.EndlessRecyclerOnScrollListener;
import com.carlise.dribbble.view.RecyclerViewPro.HeaderViewRecyclerAdapter;
import com.carlisle.model.DribleShot;
import com.carlisle.model.LikesResult;
import com.carlisle.provider.ApiFactory;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chengxin on 16/1/7.
 */
public class ShotsActivity extends BaseToolsBarActivity implements ShotListAdapter.OnClickListener {
    private static final String TAG = ShotsActivity.class.getSimpleName();

    public static final String SHOTS_TITLE_EXTRA = "title_extra";
    public static final String BUCKET_ID = "url_extra";
    public static final String CALL_FROM = "call_from";

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private HeaderViewRecyclerAdapter recyclerAdapter;
    private List<DribleShot> shots = new ArrayList<>();

    private RelativeLayout footerLayout;
    private ProgressBar footProgress;
    private TextView footContent;

    private ProgressBar progress;
    private LayoutInflater inflater;

    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_shots);
        initView();
        requestForShots();
    }

    private void initView() {
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.shots_swipe);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
            }
        });

        int toolbarH = (int) getResources().getDimension(R.dimen.toolbar_height);
        refreshLayout.setProgressViewOffset(true, toolbarH, toolbarH + 200);
        recyclerView = (RecyclerView) findViewById(R.id.shots_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progress = (ProgressBar) findViewById(R.id.shots_progress);
        progress.setVisibility(View.VISIBLE);

        final ShotListAdapter adapter = new ShotListAdapter(this, shots);
        adapter.setListener(this);
        recyclerAdapter = new HeaderViewRecyclerAdapter(adapter);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setHasFixedSize(true);

        inflater = LayoutInflater.from(this);
        footerLayout = (RelativeLayout) inflater.inflate(R.layout.footer_home_list, null, false);
        AbsListView.LayoutParams footParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.home_footer_height));
        footerLayout.setLayoutParams(footParams);
        footProgress = (ProgressBar) footerLayout.findViewById(R.id.footer_progress);
        footContent = (TextView) footerLayout.findViewById(R.id.footer_content);
        recyclerAdapter.addFooterView(footerLayout);

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) recyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int currentPage) {
                requestForShots();
            }
        });

    }

    private void reload() {
        page = 1;
        shots.clear();

        footProgress.setVisibility(View.VISIBLE);
        footContent.setVisibility(View.INVISIBLE);
        requestForShots();
    }

    private void requestForShots() {
        if (getIntent().getStringExtra(CALL_FROM).equals("like")) {
            fetchShotsFromLike();
        } else {
            fetchShotsFromBuckets();
        }
    }

    private void fetchShotsFromLike() {
        ApiFactory.getDribleApi().fetchShotsFromLike(UserHelper.getInstance(this).getDribleUser().id, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<LikesResult>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<LikesResult> likesResults) {
                        List<DribleShot> dribleShots = new ArrayList<DribleShot>();

                        for (LikesResult likesResult : likesResults) {
                            dribleShots.add(likesResult.shot);
                        }

                        handleShots(dribleShots);

                        page++;
                    }
                });
    }

    private void fetchShotsFromBuckets() {
        ApiFactory.getDribleApi().fetchShotsFromBuckets(getIntent().getLongExtra(BUCKET_ID, 0), page)
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
                        handleShots(dribleShots);

                        page++;
                    }
                });
    }

    private void handleShots(List<DribleShot> dribleShots) {
        progress.setVisibility(View.INVISIBLE);
        refreshLayout.setRefreshing(false);

        if (dribleShots.size() <= 0) {
            footProgress.setVisibility(View.INVISIBLE);
            footContent.setVisibility(View.VISIBLE);
            footContent.setText("Load finished");
        } else {
            footProgress.setVisibility(View.VISIBLE);
            footContent.setVisibility(View.INVISIBLE);
        }

        shots.addAll(dribleShots);
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onInitToolBar(Toolbar toolbar) {
        super.onInitToolBar(toolbar);
        setTitle(getIntent().getStringExtra(SHOTS_TITLE_EXTRA));
    }

    @Override
    public void onClick(View view, int position) {
        Intent intent = new Intent(this, ShotDetailActivity.class);
        intent.putExtra(ShotDetailActivity.SHOT_ID_EXTRA_FIELD, shots.get(position).id);
        startActivity(intent);
    }
}
