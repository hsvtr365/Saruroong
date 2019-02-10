package com.example.siji.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.siji.R;

import java.util.ArrayList;

/**
 * Created by siji on 2017-11-13.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<MyData> mDataset;

    //아이템 클릭 시 실행될 함수
    private ItemClick itemClick;
    public interface ItemClick{
        public void onClick(View view, int position);
    }
    //아이템 클릭시 실행 함수 등록 함수
    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImageView;
        public TextView mTextView;
        View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;

            mImageView = (ImageView) view.findViewById(R.id.msgWriteItemIV01);
            mTextView = (TextView) view.findViewById(R.id.msgWriteItemTV01);
        }
    }

    public MyAdapter(ArrayList<MyData> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_write_image, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int positons = position;
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position).text);
        holder.mImageView.setImageResource(mDataset.get(position).img);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClick != null) {
                    itemClick.onClick(view, positons);
                    System.out.println(positons);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
//        return locations.size();
        return mDataset.size();
    }

    @Override
    public long getItemId(int position){
        return position;
    }
}

