package com.carlise.dribbble.bucket;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carlise.dribbble.R;
import com.carlisle.model.DribleBucket;

import java.util.ArrayList;

/**
 * Created by chengxin on 16/3/8.
 */
public class BucketListAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<DribleBucket> buckets;
    private OnClickListener listener;

    public BucketListAdapter(Context context, ArrayList<DribleBucket> buckets) {
        this.context = context;
        this.buckets = buckets;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BucketViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bucket, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        BucketViewHolder bucketVH = (BucketViewHolder) holder;
        bucketVH.name.setText(buckets.get(position).name);
        bucketVH.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return buckets.size();
    }

    static class ViewHolder {
        TextView name;
    }

    public static class BucketViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public BucketViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.bucket_item_name);
        }
    }

    public interface OnClickListener {
        public void onClick(View view, int position);
    }
}
