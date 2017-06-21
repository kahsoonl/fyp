package com.ksleong.android.visitorapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Winter Leong on 14/6/2017.
 */

public class ListDataAdapter extends RecyclerView.Adapter<ListDataAdapter.ListDataAdapterViewHolder> {

    private String[] mPlaceName;

    private final ListDataAdapterOnClickHandler mClickHandler;

    public interface ListDataAdapterOnClickHandler {
        void onClick(String placeName);
    }

    public ListDataAdapter(ListDataAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class ListDataAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final TextView mPlaceTextView;

        public ListDataAdapterViewHolder(View view) {
            super(view);
            mPlaceTextView = (TextView) view.findViewById(R.id.tv_place_name);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String placeName = mPlaceName[adapterPosition];
            mClickHandler.onClick(placeName);
        }
    }

    @Override
    public ListDataAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.list_item, viewGroup, false);
        return new ListDataAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListDataAdapterViewHolder holder, int position) {
        String placeName = mPlaceName[position];
        holder.mPlaceTextView.setText(placeName);
    }

    @Override
    public int getItemCount() {
        if (null == mPlaceName) return 0;
        return mPlaceName.length;
    }

    public void setPlaceName(String[] placeName) {
        mPlaceName = placeName;
        notifyDataSetChanged();
    }
}
