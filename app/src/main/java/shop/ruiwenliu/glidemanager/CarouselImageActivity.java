package shop.ruiwenliu.glidemanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import shop.ruiwenliu.glidemanager.adapter.CarouselImageAdapter;

public class CarouselImageActivity extends AppCompatActivity {

    @BindView(R.id.rv)
    RecyclerView mRv;
    private CarouselImageAdapter mAdapter;
    private Disposable mDisposable = null;
    public static Intent getIntent(Context context) {
        return new Intent(context, CarouselImageActivity.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_scroll_recycler_view);
        ButterKnife.bind(this);
        mAdapter = new CarouselImageAdapter();
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(mAdapter);
        List<String> list = new ArrayList<>();
        list.add("https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1543312516&di=6e4dacdfc8ca2ebd0a18ae25d3154717&src=http://pic1.win4000.com/wallpaper/2018-07-20/5b51756e43021.jpg");
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1543402301209&di=9fe3b89a03c145fea8c0ca9070b9ec33&imgtype=0&src=http%3A%2F%2Fimage.uczzd.cn%2F1741141614217358066.jpg%3Fid%3D0");
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1543413541798&di=b51c12fcc7cc9ed3ba31ec8da26c5d87&imgtype=0&src=http%3A%2F%2Fp2.ssl.cdn.btime.com%2Ft01c3b3e1cd402f9263.jpg%3Fsize%3D605x553");
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1543413569168&di=ec641be7442c517c506a7449cb26a46e&imgtype=0&src=http%3A%2F%2Fdingyue.nosdn.127.net%2FexHtyKm1Za9k3nmSSvlM2zlFik0g8OawV3zB9O6KmKpPN1540874537509compressflag.jpg");
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1543413649829&di=4aac376d9a6239002d0002a9724f0d9d&imgtype=0&src=http%3A%2F%2Fi0.hdslb.com%2Fbfs%2Farchive%2F4085f9b3a4160171f68b1c97da6b3a54e82ee47d.jpg");
        list.add("https://gss0.bdstatic.com/94o3dSag_xI4khGkpoWK1HF6hhy/baike/s%3D500/sign=dbfd29ced862853596e0d221a0ee76f2/838ba61ea8d3fd1f0199f1063d4e251f95ca5f7d.jpg");
        list.add("https://gss0.bdstatic.com/94o3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike272%2C5%2C5%2C272%2C90/sign=49c633a25c82b2b7b392319650c4a08a/e4dde71190ef76c64f6d508d9816fdfaae5167dd.jpg");
        mAdapter.setNewData(list);
        startVerticalScroll();
    }

    /**
     * 开始滚动
     */
    public void startVerticalScroll() {
            if (mDisposable == null || mDisposable.isDisposed()) {
                mDisposable = Observable.interval(5000, 50000, TimeUnit.MICROSECONDS)
                        // carouselTimes的延迟，carouselTimes的循环时间
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(@NonNull Long aLong) throws Exception {
                                //垂直移动偏移2个像素
                                mRv.scrollBy(0, 2);
                                //如果移动到底部
                                if (isSlideToBottom(mRv)) {
                                    //跳至顶部
                                    mRv.scrollToPosition(0);
                                    //如果没有移动到底部
                                }

                            }
                        });
            }

    }

    /**
     * 停止事件，避免内存溢出
     */
    public void stopCarousel() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    /**
     * 判断Recycler是否滑动至最底部  是返回true  不是返回false
     */
    public boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return false;
        }
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()
                >= recyclerView.computeVerticalScrollRange()) {
            return true;
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCarousel();
    }
}
