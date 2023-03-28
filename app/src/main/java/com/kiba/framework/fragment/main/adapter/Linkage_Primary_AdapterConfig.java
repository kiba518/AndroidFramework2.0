

package com.kiba.framework.fragment.main.adapter;


import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.kiba.framework.R;
import com.kunminx.linkage.adapter.viewholder.LinkagePrimaryViewHolder;
import com.kunminx.linkage.contract.ILinkagePrimaryAdapterConfig;


/**
 * Linkage左侧列表适配器类
 */
public class Linkage_Primary_AdapterConfig implements ILinkagePrimaryAdapterConfig {

    private static final int MARQUEE_REPEAT_LOOP_MODE = -1;
    private static final int MARQUEE_REPEAT_NONE_MODE = 0;
    private Context context;
    private OnPrimaryItemClickListener itemClickListener;

    public Linkage_Primary_AdapterConfig(OnPrimaryItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public Linkage_Primary_AdapterConfig setOnItemClickListener(OnPrimaryItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        return this;
    }

    /**
     * 在适配器创建时（onCreateViewHolder），会调用setContext，这里重写方法进行context截获。
     * @param context
     */
    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * 设置左侧列表布局
     * @return
     */
    @Override
    public int getLayoutId() {
        return R.layout.linkage_primary;
    }
    /**
     * 设置左侧列表TitleId
     * @return
     */
    @Override
    public int getGroupTitleViewId() {
        return R.id.tv_group;
    }
    /**
     * 设置左侧列表根ViewId
     * @return
     */
    @Override
    public int getRootViewId() {
        return R.id.layout_group;
    }

    @Override
    public void onBindViewHolder(LinkagePrimaryViewHolder holder, boolean selected, String title) {
        TextView tvTitle = ((TextView) holder.mGroupTitle);
        tvTitle.setText(title);

        tvTitle.setBackgroundColor(context.getResources().getColor(selected ? R.color.c_black_2 : R.color.c_black_1));
        tvTitle.setTextColor(ContextCompat.getColor(context, selected ? R.color.colorPrimary : R.color.c_grey_1));
        tvTitle.setEllipsize(selected ? TextUtils.TruncateAt.MARQUEE : TextUtils.TruncateAt.END);
        tvTitle.setFocusable(selected);
        tvTitle.setFocusableInTouchMode(selected);
        tvTitle.setMarqueeRepeatLimit(selected ? MARQUEE_REPEAT_LOOP_MODE : MARQUEE_REPEAT_NONE_MODE);
    }

    @Override
    public void onItemClick(LinkagePrimaryViewHolder holder, View view, String title) {
        if (itemClickListener != null) {
            itemClickListener.onPrimaryItemClick(holder, view, title);
        }
    }

    public interface OnPrimaryItemClickListener {
        /**
         * we suggest you get position by holder.getAdapterPosition
         *
         * @param holder primaryHolder
         * @param view   view
         * @param title  groupTitle
         */
        void onPrimaryItemClick(LinkagePrimaryViewHolder holder, View view, String title);
    }
}
