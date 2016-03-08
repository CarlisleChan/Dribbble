package com.carlise.dribbble.bucket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import com.carlise.dribbble.R;
import com.carlise.dribbble.application.BaseToolsBarActivity;
import com.carlise.dribbble.shot.ShotsActivity;
import com.carlise.dribbble.utils.UserHelper;
import com.carlise.dribbble.view.RecyclerViewPro.HeaderViewRecyclerAdapter;
import com.carlisle.model.DribleBucket;
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
public class BucketsActivity extends BaseToolsBarActivity implements BucketListAdapter.OnClickListener {
    private static final String TAG = BucketsActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private View header;

    private HeaderViewRecyclerAdapter recyclerAdapter;
    private ArrayList<DribleBucket> buckets = new ArrayList<>();

    private ProgressBar progress;

    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_bucket);

        initView();
        requestForBuckets();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.buckets_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        BucketListAdapter adapter = new BucketListAdapter(this, buckets);
        adapter.setListener(this);
        recyclerAdapter = new HeaderViewRecyclerAdapter(adapter);
        recyclerView.setAdapter(adapter);

        header = new View(this);
        AbsListView.LayoutParams headParam = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.toolbar_height));
        header.setLayoutParams(headParam);
        recyclerAdapter.addHeaderView(header);

        progress = (ProgressBar) findViewById(R.id.buckets_progress);
        progress.setVisibility(View.VISIBLE);
    }

    private void requestForBuckets() {
        ApiFactory.getDribleApi().fetchBuckets(UserHelper.getInstance(this).getDribleUser().id, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<DribleBucket>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        progress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onNext(List<DribleBucket> dribleBuckets) {
                        progress.setVisibility(View.INVISIBLE);
                        handleBuckets(dribleBuckets);
                    }
                });
    }

    private void handleBuckets(List<DribleBucket> dribleBuckets) {
        if (dribleBuckets.size() <= 0) {
            return;
        }
        buckets.addAll(dribleBuckets);
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onInitToolBar(Toolbar toolbar) {
        super.onInitToolBar(toolbar);
        setTitle("Buckets");
    }

    @Override
    public void onClick(View view, int position) {
        Intent intent = new Intent(BucketsActivity.this, ShotsActivity.class);
        intent.putExtra(ShotsActivity.SHOTS_TITLE_EXTRA, "Shots of " + buckets.get(position).name);
        intent.putExtra(ShotsActivity.BUCKET_ID, buckets.get(position).id);
        intent.putExtra(ShotsActivity.CALL_FROM, "bucket");
        startActivity(intent);
    }
}
