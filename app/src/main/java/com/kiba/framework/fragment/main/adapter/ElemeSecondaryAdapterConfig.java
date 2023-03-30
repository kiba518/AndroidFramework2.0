
package com.kiba.framework.fragment.main.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kiba.framework.R;
import com.kunminx.linkage.adapter.viewholder.LinkageSecondaryFooterViewHolder;
import com.kunminx.linkage.adapter.viewholder.LinkageSecondaryHeaderViewHolder;
import com.kunminx.linkage.adapter.viewholder.LinkageSecondaryViewHolder;
import com.kunminx.linkage.bean.BaseGroupedItem;
import com.kunminx.linkage.contract.ILinkageSecondaryAdapterConfig;


/**
 * Linkage右侧列表适配器类
 *
 */
public class ElemeSecondaryAdapterConfig implements ILinkageSecondaryAdapterConfig<ElemeGroupedItem.ItemInfo> {

    private static final int SPAN_COUNT = 2;

    private Context context;

    private OnSecondaryItemClickListener mItemClickListener;

    public ElemeSecondaryAdapterConfig(OnSecondaryItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public ElemeSecondaryAdapterConfig setOnItemClickListener(OnSecondaryItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
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
     * 横向布局文件
     * @return
     */
    @Override
    public int getGridLayoutId() {
        return R.layout.linkage_secondary_grid;
    }
    /**
     * 竖向布局文件 当前执行导入的是竖向布局
     * @return
     */
    @Override
    public int getLinearLayoutId() {
        return R.layout.linkage_secondary_linear;
    }

    /**
     * header布局，右侧的列表会根据左侧进行分类，header用来在右侧显示类别名
     * @return
     */
    @Override
    public int getHeaderLayoutId() {
        return R.layout.linkage_secondary_header;
    }
    /**
     * footer布局，在右侧列表页面最下方显示，这里设置为一个空布局
     * @return
     */
    @Override
    public int getFooterLayoutId() {
        return R.layout.linkage_secondary_footer;
    }

    @Override
    public int getHeaderTextViewId() {
        return R.id.secondary_header;
    }

    @Override
    public int getSpanCountOfGridMode() {
        return SPAN_COUNT;
    }

    @Override
    public void onBindViewHolder(final LinkageSecondaryViewHolder holder,
                                 final BaseGroupedItem<ElemeGroupedItem.ItemInfo> item) {

        String price =  item.info.getCost();
        String content =  item.info.getContent();
        String imgUrl =  item.info.getImgUrl();
        String title = item.info.getTitle();
        String group = item.info.getGroup();

        ((TextView) holder.getView(R.id.iv_goods_name)).setText(title);
        Glide.with(context).load(imgUrl).placeholder(R.drawable.game_ico).into((ImageView) holder.getView(R.id.iv_goods_img));
        ((TextView) holder.getView(R.id.iv_goods_detail)).setText(content);
        ViewGroup viewGroup = holder.getView(R.id.iv_goods_item);
        viewGroup.setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onSecondaryItemClick(holder, viewGroup, item);
            }
        });

        holder.getView(R.id.ll_down).setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onDownLoad(v, item);
            }
        });
    }

    @Override
    public void onBindHeaderViewHolder(LinkageSecondaryHeaderViewHolder holder,
                                       BaseGroupedItem<ElemeGroupedItem.ItemInfo> item) {

        ((TextView) holder.getView(R.id.secondary_header)).setText(item.header);
    }

    @Override
    public void onBindFooterViewHolder(LinkageSecondaryFooterViewHolder holder,
                                       BaseGroupedItem<ElemeGroupedItem.ItemInfo> item) {

    }


    public interface OnSecondaryItemClickListener {

        void onSecondaryItemClick(LinkageSecondaryViewHolder holder, ViewGroup view, BaseGroupedItem<ElemeGroupedItem.ItemInfo> item);

        void onDownLoad(View view, BaseGroupedItem<ElemeGroupedItem.ItemInfo> item);

    }
}
