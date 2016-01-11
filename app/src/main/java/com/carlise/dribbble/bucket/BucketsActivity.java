package com.carlise.dribbble.bucket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.carlise.dribbble.R;
import com.carlise.dribbble.application.BaseActivity;
import com.carlise.dribbble.shot.ShotsActivity;
import com.carlise.dribbble.utils.UserHelper;
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
public class BucketsActivity extends BaseActivity {
    private static final String TAG = BucketsActivity.class.getSimpleName();

    private ListView listView;
    private View header;

    private BucketListAdapter bucketListAdapter;
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

        listView = (ListView) findViewById(R.id.buckets_list);

        header = new View(this);
        AbsListView.LayoutParams headParam = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.toolbar_height));
        header.setLayoutParams(headParam);
        listView.addHeaderView(header);

        bucketListAdapter = new BucketListAdapter(this, buckets);
        listView.setAdapter(bucketListAdapter);
        listView.setDivider(null);

        progress = (ProgressBar) findViewById(R.id.buckets_progress);
        progress.setVisibility(View.VISIBLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BucketsActivity.this, ShotsActivity.class);
                intent.putExtra(ShotsActivity.SHOTS_TITLE_EXTRA, "Shots of " + buckets.get(position).name);
                intent.putExtra(ShotsActivity.BUCKET_ID, buckets.get(position).id);
                intent.putExtra(ShotsActivity.CALL_FROM, "bucket");
                startActivity(intent);
            }
        });

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
        bucketListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onInitToolBar(Toolbar toolbar) {
        super.onInitToolBar(toolbar);
        setTitle("Buckets");
    }
}
