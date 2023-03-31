package com.kiba.framework.fragment.main;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.kiba.framework.R;
import com.kiba.framework.control.popup.EasyPopup;
import com.kiba.framework.control.popup.HorizontalGravity;
import com.kiba.framework.control.popup.VerticalGravity;
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
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MainFragment extends BaseFragment {
    @BindView(R.id.linkage)
    LinkageRecyclerView linkage;
    private EasyPopup circlePop;
    @BindView(R.id.ll_header)
    LinearLayout ll_header;
    @BindView(R.id.img_1)
    AppCompatImageView img_1;
    @BindView(R.id.img_2)
    AppCompatImageView img_2;
    @BindView(R.id.img_3)
    AppCompatImageView img_3;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void onCreate() {


        initGlide();
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
            public void onDownLoad(View view, BaseGroupedItem<ElemeGroupedItem.ItemInfo> item) {
                circlePop.showAtAnchorView(ll_header, VerticalGravity.BELOW, HorizontalGravity.CENTER, 0, 0);
            }
        }));
        initCirclePop();
    }
    public void initCirclePop() {
        circlePop = new EasyPopup(getContext())
                .setContentView(R.layout.layout_friend_circle_comment)
                .setFocusAndOutsideEnable(true)
                .createPopup();

        TextView tvZan = circlePop.getView(R.id.tv_zan);
        TextView tvComment = circlePop.getView(R.id.tv_comment);
        tvZan.setOnClickListener(v -> {

            circlePop.dismiss();
        });

        tvComment.setOnClickListener(v -> {

            circlePop.dismiss();
        });
    }

    void initGlide(){
        Glide.with(context).load(R.drawable.header_temp).placeholder(R.drawable.header_temp)
                .apply(bitmapTransform(new RoundedCornersTransformation(200, 0, RoundedCornersTransformation.CornerType.ALL)))
                //缓存策略,跳过内存缓存【此处应该设置为false，否则列表刷新时会闪一下】
                .skipMemoryCache(false)
                //缓存策略,硬盘缓存-仅仅缓存最终的图像，即降低分辨率后的（或者是转换后的）
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //设置图片加载的优先级
                .priority(Priority.HIGH)
                .into(img_1);
        Glide.with(context).load(R.drawable.header_temp).placeholder(R.drawable.header_temp)
                .apply(bitmapTransform(new RoundedCornersTransformation(200, 0, RoundedCornersTransformation.CornerType.ALL)))
                //缓存策略,跳过内存缓存【此处应该设置为false，否则列表刷新时会闪一下】
                .skipMemoryCache(false)
                //缓存策略,硬盘缓存-仅仅缓存最终的图像，即降低分辨率后的（或者是转换后的）
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //设置图片加载的优先级
                .priority(Priority.HIGH)
                .into(img_2);
        Glide.with(context).load(R.drawable.header_temp).placeholder(R.drawable.header_temp)
                .apply(bitmapTransform(new RoundedCornersTransformation(200, 0, RoundedCornersTransformation.CornerType.ALL)))
                //缓存策略,跳过内存缓存【此处应该设置为false，否则列表刷新时会闪一下】
                .skipMemoryCache(false)
                //缓存策略,硬盘缓存-仅仅缓存最终的图像，即降低分辨率后的（或者是转换后的）
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //设置图片加载的优先级
                .priority(Priority.HIGH)
                .into(img_3);
    }

}