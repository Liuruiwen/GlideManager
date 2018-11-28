package shop.ruiwenliu.glidemanager.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import shop.ruiwenliu.glidemanager.R;
import shop.ruiwenliu.glidemanager.until.GlideManager;

/**
 * Created by Amuse
 * Data:2018/11/28 0028
 * Desc:
 */

public class CarouselImageAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public CarouselImageAdapter() {
        super(R.layout.item_image);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        GlideManager.getInstance(mContext).loadImage(item, (ImageView) helper.getView(R.id.image), 400, 400);

    }
}
