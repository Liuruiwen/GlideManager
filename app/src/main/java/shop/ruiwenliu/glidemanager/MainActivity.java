package shop.ruiwenliu.glidemanager;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import shop.ruiwenliu.glidemanager.adapter.LabAdapter;
import shop.ruiwenliu.glidemanager.until.GlideManager;
import shop.ruiwenliu.glidemanager.until.glide.GlideApp;
import shop.ruiwenliu.glidemanager.until.glide.GlideCacheUtil;
import shop.ruiwenliu.glidemanager.until.glide.ProgressInterceptor;
import shop.ruiwenliu.glidemanager.until.glide.ProgressListener;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.rv)
    RecyclerView mRv;
    @BindView(R.id.tv_cache)
    TextView tvCache;
    @BindView(R.id.image)
    ImageView mImage;
    @BindView(R.id.iv_loading)
    ImageView ivLoading;
     private String gifUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1543295227547&di=0f2302ea269d28749089719ac3d5157b&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01f6d156de7cc66ac72531cb5fdbb6.gif";
    private String url = "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1543312516&di=6e4dacdfc8ca2ebd0a18ae25d3154717&src=http://pic1.win4000.com/wallpaper/2018-07-20/5b51756e43021.jpg";
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("加载中");
        mRv = findViewById(R.id.rv);
        mImage = findViewById(R.id.image);
        tvCache.setText(GlideCacheUtil.getInstance(MainActivity.this).getCacheSize());
        LabAdapter adapter = new LabAdapter();
        mRv.setLayoutManager(new GridLayoutManager(this, 4));
        mRv.setAdapter(adapter);
        List<String> list = Arrays.asList(getResources().getStringArray(R.array.arrat_list));
        adapter.setNewData(list);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position) {
                    case 0://加载图片
                        GlideManager.getInstance(MainActivity.this).loadImage(url, mImage);
                        break;
                    case 1://加载圆角图片
                        GlideManager.getInstance(MainActivity.this).loadRoundImage(url, mImage, 10);
                        break;
                    case 2://加载头像
                        GlideManager.getInstance(MainActivity.this).loadCircleImage(url, mImage);
                        break;
                    case 3://加载Gif图片
                        GlideManager.getInstance(MainActivity.this).loadGifImage(gifUrl,mImage,10);
                        break;
                    case 4://加载大图监听
                        loadingListener();
                        break;
                    case 5://加载长图
                        startActivity(TestActivity.getIntent(MainActivity.this));
                        break;
                    case 6:
                       startActivity(CarouselImageActivity.getIntent(MainActivity.this));
                        break;
                    case 7:

                        break;
                    case 8:
                        if(GlideCacheUtil.getInstance(MainActivity.this).clearImageDiskCache()){
                            Toast.makeText(MainActivity.this, "清除缓存成功", Toast.LENGTH_SHORT).show();
                        }else
                        {
                            Toast.makeText(MainActivity.this, "清除缓存失败", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });

    }


    private void loadingListener(){
      final  String maxUrl="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1543402301209&di=9fe3b89a03c145fea8c0ca9070b9ec33&imgtype=0&src=http%3A%2F%2Fimage.uczzd.cn%2F1741141614217358066.jpg%3Fid%3D0";

        ProgressInterceptor.addListener(maxUrl, new ProgressListener() {
            @Override
            public void onProgress(int progress) {
                progressDialog.setProgress(progress);
            }
        });
             GlideApp.with(this).load(maxUrl).apply(new RequestOptions()
                     .diskCacheStrategy(DiskCacheStrategy.NONE) //测试进度实现  采用不缓存
                     .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL))
                     .into(new SimpleTarget<Drawable>() {
                 @Override
                 public void onLoadStarted(@Nullable Drawable placeholder) {
                     super.onLoadStarted(placeholder);
                     progressDialog.show();
                 }

                 @Override
                 public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                     ivLoading.setImageDrawable(resource);
                     progressDialog.dismiss();
                     ProgressInterceptor.removeListener(maxUrl);
                 }
             });
    }


}
