package com.android.dailyplanning.adapter;

import android.support.annotation.Nullable;

import com.android.dailyplanning.R;
import com.android.dailyplanning.entity.Plan;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class CompleteAdapter extends BaseQuickAdapter<Plan, BaseViewHolder> {

    public CompleteAdapter(@Nullable List<Plan> data) {
        super(R.layout.item_complete, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Plan item) {
        helper.setText(R.id.tv_label, "标签："+item.getLabel());
        helper.setText(R.id.tv_key_word, "关键词："+item.getKeyWord());
    }
}
