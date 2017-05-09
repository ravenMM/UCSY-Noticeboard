package com.climbdev2016.noticeboard.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.models.CategoryModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zwe on 5/8/17.
 */

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder>{


    private Context mContext;
    private List<CategoryModel> mCategoryList;
    private OnItemClickListener itemClickListener;
    public CategoryRecyclerAdapter(Context context, List<CategoryModel> categoryList){
        this.mContext = context;
        this.mCategoryList = categoryList;
    }


    @Override
    public CategoryRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.category_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryRecyclerAdapter.ViewHolder holder, int position) {
        CategoryModel model = mCategoryList.get(position);
        holder.tvCategory.setText(model.getCategory());

    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
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
            itemClickListener.onItemClick(view,getAdapterPosition());
        }
    }
}
