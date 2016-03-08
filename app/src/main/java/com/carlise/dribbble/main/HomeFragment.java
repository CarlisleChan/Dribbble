package com.carlise.dribbble.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.carlise.dribbble.R;
import com.carlise.dribbble.event.BackToUpEvent;
import com.carlise.dribbble.shot.ShotDetailActivity;
import com.carlise.dribbble.shot.ShotListAdapter;
import com.carlise.dribbble.utils.AuthUtil;
import com.carlise.dribbble.utils.PreferenceKey;
import com.carlise.dribbble.view.RecyclerViewPro.EndlessRecyclerOnScrollListener;
import com.carlise.dribbble.view.RecyclerViewPro.HeaderViewRecyclerAdapter;
import com.carlisle.dribbble.com.rx.RxBus;
import com.carlisle.model.DribleShot;
import com.carlisle.provider.ApiFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by chengxin on 16/1/7.
 */
public class HomeFragment extends Fragment implements ShotListAdapter.OnClickListener {
    private static final String TAG = HomeFragment.class.getSimpleName();

    public static final String TAG_1 = "Top Shots";
    public static final String TAG_2 = "Latest";
    public static final String TAG_3 = "Animation";

    public static final String TAB_INDEX_FIELD = "tab_index";

    private int index;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private RelativeLayout footerLayout;
    private ProgressBar footProgress;

    private HeaderViewRecyclerAdapter recyclerAdapter;
    private List<DribleShot> shotList = new ArrayList<>();

    private HashMap<Integer, Integer> leftItemsH = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> midItemsH = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> rigItemsH = new HashMap<Integer, Integer>();

    private Runnable timeOut = new Runnable() {
        @Override
        public void run() {
            refreshLayout.setRefreshing(false);
        }
    };

    private CompositeSubscription subscriptions;

    private int firstVisibleItemPosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        index = getArguments().getInt(TAB_INDEX_FIELD);
        subscriptions = new CompositeSubscription();

        ShotListAdapter adapter = new ShotListAdapter(getActivity(), shotList);
        adapter.setListener(this);
        recyclerAdapter = new HeaderViewRecyclerAdapter(adapter);

        refreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_home_content, container, false);
        recyclerView = (RecyclerView) refreshLayout.findViewById(R.id.home_content_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);

        footerLayout = (RelativeLayout) inflater.inflate(R.layout.footer_home_list, null, false);
        AbsListView.LayoutParams footParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.home_footer_height));
        footerLayout.setLayoutParams(footParams);
        footProgress = (ProgressBar) footerLayout.findViewById(R.id.footer_progress);
        recyclerAdapter.addFooterView(footerLayout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            recyclerView.setNestedScrollingEnabled(true);
        }

        requestForList();

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) recyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int currentPage) {
                requestForList();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                int index = getArguments().getInt(TAB_INDEX_FIELD);
                if (recyclerView.getChildAt(0) != null) {
                    switch (index) {
                        case 0:
                            leftItemsH.put(firstVisibleItemPosition, recyclerView.getChildAt(0).getHeight());
                            break;
                        case 1:
                            midItemsH.put(firstVisibleItemPosition, recyclerView.getChildAt(0).getHeight());
                            break;
                        case 2:
                            rigItemsH.put(firstVisibleItemPosition, recyclerView.getChildAt(0).getHeight());
                            break;
                    }
                }
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
            }
        });
        refreshLayout.setColorSchemeResources(R.color.pretty_blue, R.color.pretty_green);

        return refreshLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View view, int position) {
        Intent intent = new Intent(getActivity(), ShotDetailActivity.class);
        intent.putExtra(ShotDetailActivity.SHOT_ID_EXTRA_FIELD, shotList.get(position).id);
        startActivity(intent);
        getActivity().overridePendingTransition(0, 0);
    }

    String sort = null;
    String list = null;
    int page = 1;

    private void reload() {
        page = 1;
        shotList.clear();
        refreshLayout.setRefreshing(true);
        requestForList();
    }

    private void requestForList() {
        final String accessToken = AuthUtil.getAccessToken(getActivity());
        if (TextUtils.isEmpty(accessToken)) {
            refreshLayout.setRefreshing(false);
            return;
        }

        switch (index) {
            case 0:
                // url += ("?" + "page=2");
                break;
            case 1:
                sort = PreferenceKey.REQUEST_SORT_RECENT;
                break;
            case 2:
                sort = PreferenceKey.REQUEST_SORT_RECENT;
                list = PreferenceKey.REQUEST_LIST_ANIMATED;
                break;
        }

        ApiFactory.getDribleApi().fetchOneShots(page, sort, list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<DribleShot>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        refreshLayout.setRefreshing(false);
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "errors", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onNext(List<DribleShot> dribleShots) {
                        refreshLayout.setRefreshing(false);
                        handleResponse(dribleShots);

                        page++;
                    }
                });

        new Handler().removeCallbacks(timeOut);
        new Handler().postDelayed(timeOut, 10000);
    }

    private void handleResponse(List<DribleShot> dribleShots) {
        footProgress.setVisibility(View.VISIBLE);
        shotList.addAll(dribleShots);
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout.setRefreshing(false);

        subscriptions.add(RxBus.getInstance().toObservable().subscribe(new Action1() {
            @Override
            public void call(Object o) {
                if (o instanceof BackToUpEvent) {
                    if (((BackToUpEvent) o).position == index) {
                        if (firstVisibleItemPosition == 0) {
                            reload();
                        } else {
                            recyclerView.smoothScrollToPosition(0);
                        }
                    }
                }
            }
        }));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscriptions != null) {
            subscriptions.unsubscribe();
        }
    }
}
