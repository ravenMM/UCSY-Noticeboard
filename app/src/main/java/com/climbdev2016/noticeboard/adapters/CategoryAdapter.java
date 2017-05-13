package com.climbdev2016.noticeboard.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.climbdev2016.noticeboard.R;

/**
 * Created by zwe on 5/8/17.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

    private OnItemClickListener itemClickListener;

    private  String[] categories;

    public CategoryAdapter(String[] categories){
        this.categories = categories;
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.ViewHolder holder, int position) {
        holder.tvCategory.setText(categories[position]);

    }

    @Override
    public int getItemCount() {
        return categories.length;
    }

    public interface OnItemClickListener{
        void onItemClick(String category);
    }

    public void setOnItemCLickListener(final OnItemClickListener mOnItemCLickListener){
        this.itemClickListener = mOnItemCLickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvCategory;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCategory = (TextView) itemView.findViewById(R.id.category_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClick(categories[getAdapterPosition()]);
        }
    }
}
