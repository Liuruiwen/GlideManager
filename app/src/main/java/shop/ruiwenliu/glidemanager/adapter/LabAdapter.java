package shop.ruiwenliu.glidemanager.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import shop.ruiwenliu.glidemanager.R;

/**
 * Created by Amuse
 * Data:2018/11/27 0027
 * Desc:
 */

public class LabAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public LabAdapter() {
        super(R.layout.item_lab);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_lab, item);

    }
}
