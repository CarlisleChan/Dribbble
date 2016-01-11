package com.carlise.dribbble.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.carlise.dribbble.R;
import com.carlise.dribbble.shot.ShotDetailActivity;
import com.carlise.dribbble.shot.ShotListAdapter;
import com.carlise.dribbble.utils.AuthUtil;
import com.carlise.dribbble.utils.PreferenceKey;
import com.carlisle.model.DribleShot;
import com.carlisle.provider.ApiFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chengxin on 16/1/7.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();

    public static final String TAB_INDEX_FIELD = "tab_index";
    private static final int RETRY_COUNT = 5;

    private int index;
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private RelativeLayout footerLayout;
    private ProgressBar footProgress;

    private ShotListAdapter listAdapter;
    private ArrayList<DribleShot> shotList;

    private HashMap<Integer, Integer> leftItemsH = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> midItemsH = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> rigItemsH = new HashMap<Integer, Integer>();
    private int lastScrollY;

    private Runnable timeOut = new Runnable() {
        @Override
        public void run() {
            refreshLayout.setRefreshing(false);
        }
    };

    private boolean canScroll = true;
    private boolean canLoadMore = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        index = getArguments().getInt(TAB_INDEX_FIELD);

        if (listView == null) {
            refreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.home_content_fragment, container, false);
            listView = (ListView) refreshLayout.findViewById(R.id.home_content_fragment);

            footerLayout = (RelativeLayout) inflater.inflate(R.layout.footer_home_list, null, false);
            AbsListView.LayoutParams footParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.home_footer_height));
            footerLayout.setLayoutParams(footParams);
            listView.addFooterView(footerLayout);
            footProgress = (ProgressBar) footerLayout.findViewById(R.id.footer_progress);

            listView.setDivider(null);
            listView.setFriction(ViewConfiguration.getScrollFriction() /** 0.7f*/);

            requestForList();

            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                }
            });

            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    int index = getArguments().getInt(TAB_INDEX_FIELD);
                    int scrollHeight = 0;
                    if (listView.getChildAt(0) != null) {
                        switch (index) {
                            case 0:
                                leftItemsH.put(firstVisibleItem, listView.getChildAt(0).getHeight());
                                break;
                            case 1:
                                midItemsH.put(firstVisibleItem, listView.getChildAt(0).getHeight());
                                break;
                            case 2:
                                rigItemsH.put(firstVisibleItem, listView.getChildAt(0).getHeight());
                                break;
                        }


                        HashMap<Integer, Integer> heights = index == 0 ? leftItemsH : index == 1 ? midItemsH : rigItemsH;
                        for (int i = 0; i < firstVisibleItem; i++) {
                            if (heights.containsKey(i)) {
                                scrollHeight += heights.get(i);
                            } else { // This should not occur,
                                Log.e(TAG, "This should not occur, you can slow down scroll speed to fix it");
                                heights.put(i, heights.get(i - 1));
                                scrollHeight += heights.get(i);
                            }
                        }
                        scrollHeight += -listView.getChildAt(0).getTop();
                    }

                    if (onScrollListListener != null && canScroll) {
                        onScrollListListener.onListScroll(scrollHeight - lastScrollY);
                    }
                    lastScrollY = scrollHeight;
                    if (firstVisibleItem + visibleItemCount == totalItemCount && !listView.canScrollVertically(1)
                            && listView.getAdapter() != null && canLoadMore) {
                        canLoadMore = false;
                        requestForList();
                    }
                }
            });
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    requestForList();
                    page = 1;
                }
            });
            refreshLayout.setColorSchemeResources(R.color.pretty_blue,
                    R.color.pretty_green);

        }

        return refreshLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private OnScrollListListener onScrollListListener;

    public void setOnScrollListListener(OnScrollListListener listener) {
        onScrollListListener = listener;
    }

    public interface OnScrollListListener {
        void onListScroll(int scrollDisY);
    }

    String sort = null;
    String list = null;
    int page = 1;

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

                        if (dribleShots.isEmpty()) {
                            canLoadMore = false;
                        } else {
                            canLoadMore = true;
                        }
                        page++;
                    }
                });

        if (canLoadMore) {
            footProgress.setVisibility(View.VISIBLE);
        }

        new Handler().removeCallbacks(timeOut);
        new Handler().postDelayed(timeOut, 10000);
    }

    private void handleResponse(List<DribleShot> dribleShots) {
        try {
            if (shotList == null) {
                shotList = new ArrayList<>();
            }
            if (listAdapter == null) {
                listAdapter = new ShotListAdapter(getActivity(), shotList);
                listView.setAdapter(listAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getActivity(), ShotDetailActivity.class);
                        intent.putExtra(ShotDetailActivity.SHOT_ID_EXTRA_FIELD, shotList.get(position).id);
                        startActivity(intent);
                        getActivity().overridePendingTransition(0, 0);
                    }
                });
            }

            if (page == 1) {
                shotList.clear();
            }

            shotList.addAll(dribleShots);
            listAdapter.notifyDataSetChanged();
            canLoadMore = true;
            footProgress.setVisibility(View.INVISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

}
