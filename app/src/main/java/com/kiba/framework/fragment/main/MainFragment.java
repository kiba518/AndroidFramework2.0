package com.kiba.framework.fragment.main;

import android.view.View;
import android.view.ViewGroup;

import com.kiba.framework.R;
import com.kiba.framework.fragment.main.adapter.Linkage_Primary_AdapterConfig;
import com.kiba.framework.fragment.main.adapter.ElemeGroupedItem;
import com.kiba.framework.fragment.main.adapter.ElemeSecondaryAdapterConfig;
import com.kiba.framework.fragment.base.BaseFragment;
import com.kiba.framework.fragment.main.adapter.FakeLinkageData;
import com.kiba.framework.utils.ToastUtils;
import com.kunminx.linkage.LinkageRecyclerView;
import com.kunminx.linkage.adapter.viewholder.LinkagePrimaryViewHolder;
import com.kunminx.linkage.adapter.viewholder.LinkageSecondaryViewHolder;
import com.kunminx.linkage.bean.BaseGroupedItem;

import java.util.List;

import butterknife.BindView;

public class MainFragment extends BaseFragment {
    @BindView(R.id.linkage)
    LinkageRecyclerView linkage;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initViews() {
        List<ElemeGroupedItem> items = FakeLinkageData.getElemeGroupItems();
        //一行代码完成初始化
        linkage.init(items, new Linkage_Primary_AdapterConfig(new Linkage_Primary_AdapterConfig.OnPrimaryItemClickListener() {
            @Override
            public void onPrimaryItemClick(LinkagePrimaryViewHolder holder, View view, String title) {

            }
        }), new ElemeSecondaryAdapterConfig(new ElemeSecondaryAdapterConfig.OnSecondaryItemClickListener() {
            @Override
            public void onSecondaryItemClick(LinkageSecondaryViewHolder holder, ViewGroup view, BaseGroupedItem<ElemeGroupedItem.ItemInfo> item) {
                ToastUtils.showToast(item.info.getTitle());
            }

            @Override
            public void onGoodAdd(View view, BaseGroupedItem<ElemeGroupedItem.ItemInfo> item) {
                ToastUtils.showToast("添加：" + item.info.getTitle());
            }
        }));
    }


}