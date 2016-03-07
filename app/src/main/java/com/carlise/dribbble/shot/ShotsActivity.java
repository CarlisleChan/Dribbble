package com.carlise.dribbble.shot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
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

import com.carlise.dribbble.R;
import com.carlise.dribbble.application.BaseToolsBarActivity;
import com.carlise.dribbble.utils.UserHelper;
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
public class ShotsActivity extends BaseToolsBarActivity {
    private static final String TAG = ShotsActivity.class.getSimpleName();

    public static final String SHOTS_TITLE_EXTRA = "title_extra";
    public static final String BUCKET_ID = "url_extra";
    public static final String CALL_FROM = "call_from";

    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private ShotListAdapter shotsAdapter;
    private ArrayList<DribleShot> shots = new ArrayList<>();

    private RelativeLayout footerLayout;
    private ProgressBar footProgress;

    private ProgressBar progress;
    private LayoutInflater inflater;

    private int page = 1;
    private boolean canLoadMore = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_shots);
        initView();
        requestForShots(true);
    }

    private void initView() {
        inflater = LayoutInflater.from(this);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.shots_swipe);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestForShots(true);
            }
        });
        int toolbarH = (int) getResources().getDimension(R.dimen.toolbar_height);
        refreshLayout.setProgressViewOffset(true, toolbarH, toolbarH + 200);

        listView = (ListView) findViewById(R.id.shots_list);

        footerLayout = (RelativeLayout) inflater.inflate(R.layout.footer_home_list, null, false);
        AbsListView.LayoutParams footParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.home_footer_height));
        footerLayout.setLayoutParams(footParams);
        listView.addFooterView(footerLayout);
        footProgress = (ProgressBar) footerLayout.findViewById(R.id.footer_progress);

        progress = (ProgressBar) findViewById(R.id.shots_progress);
        progress.setVisibility(View.VISIBLE);

        shotsAdapter = new ShotListAdapter(this, shots);
        listView.setAdapter(shotsAdapter);
        listView.setDivider(null);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (!listView.canScrollVertically(1) && firstVisibleItem > 0 &&
                        (firstVisibleItem + visibleItemCount == totalItemCount && canLoadMore && page != 1)) {
                    footProgress.setVisibility(View.VISIBLE);
                    requestForShots(false);

                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ShotsActivity.this, ShotDetailActivity.class);
                intent.putExtra(ShotDetailActivity.SHOT_ID_EXTRA_FIELD, shots.get(position).id);
                startActivity(intent);
            }
        });

    }

    private void requestForShots(final boolean isFirst) {
        if (getIntent().getStringExtra(CALL_FROM).equals("like")) {
            fetchShotsFromLike(isFirst);
        } else {
            fetchShotsFromBuckets(isFirst);
        }
    }

    private void fetchShotsFromLike(final boolean isFirst) {
        ApiFactory.getDribleApi().fetchShotsFromLike(UserHelper.getInstance(this).getDribleUser().id, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<LikesResult>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        whenReuestDone();
                    }

                    @Override
                    public void onNext(List<LikesResult> likesResults) {
                        whenReuestDone();

                        List<DribleShot> dribleShots = new ArrayList<DribleShot>();

                        for (LikesResult likesResult : likesResults) {
                            dribleShots.add(likesResult.shot);
                        }

                        handleShots(dribleShots, isFirst);

                        if (likesResults.isEmpty()) {
                            canLoadMore = false;
                        } else {
                            canLoadMore = true;
                        }

                        page++;
                    }
                });
    }

    private void fetchShotsFromBuckets(final boolean isFirst) {
        ApiFactory.getDribleApi().fetchShotsFromBuckets(getIntent().getLongExtra(BUCKET_ID, 0), page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<DribleShot>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        whenReuestDone();
                    }

                    @Override
                    public void onNext(List<DribleShot> dribleShots) {
                        whenReuestDone();

                        handleShots(dribleShots, isFirst);

                        if (dribleShots.isEmpty()) {
                            canLoadMore = false;
                        } else {
                            canLoadMore = true;
                        }

                        page++;
                    }
                });
    }

    private void whenReuestDone() {
        progress.setVisibility(View.INVISIBLE);
        refreshLayout.setRefreshing(false);
        footProgress.setVisibility(View.INVISIBLE);
    }

    private void handleShots(List<DribleShot> dribleShots, boolean isFirst) {
        if (dribleShots.size() <= 0) {
            listView.setOnItemClickListener(null);
            TextView noShots = new TextView(this);
            noShots.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            noShots.setText("Load finished.");
            noShots.setTextColor(getResources().getColor(R.color.grey_text));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            noShots.setLayoutParams(params);
            footerLayout.addView(noShots);
        }

        if (isFirst) {
            shots.clear();
        }

        shots.addAll(dribleShots);
        shotsAdapter.notifyDataSetChanged();

    }

    @Override
    public void onInitToolBar(Toolbar toolbar) {
        super.onInitToolBar(toolbar);
        setTitle(getIntent().getStringExtra(SHOTS_TITLE_EXTRA));

    }
}
