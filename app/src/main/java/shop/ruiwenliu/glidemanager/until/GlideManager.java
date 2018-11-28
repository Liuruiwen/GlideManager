package shop.ruiwenliu.glidemanager.until;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.lang.reflect.Field;

import shop.ruiwenliu.glidemanager.R;
import shop.ruiwenliu.glidemanager.until.glide.GlideApp;
import shop.ruiwenliu.glidemanager.until.glide.GlideCacheUtil;

/**
 * Created by Amuse
 * Data:2018/11/27 0027
 * Desc:glide加载图片
 */

public class GlideManager {

    private Context mContext;

    public GlideManager(Context context) {
        this.mContext = context.getApplicationContext();
    }

    private static GlideManager glideManager;

    public static GlideManager getInstance(Context context) {
        if (glideManager == null) {
            glideManager = new GlideManager(context);
        }
        return glideManager;
    }

    /**
     * 加载普通图片
     *
     * @param url
     * @param image
     */
    public void loadImage(String url, ImageView image) {
        GlideApp.with(mContext)
                .load(url)
                .apply(new RequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(image);
    }


    /**
     * 加载普通图片
     *
     * @param url
     * @param image
     */
    public void loadImage(String url, ImageView image,int x,int y) {
        GlideApp.with(mContext)
                .load(url)
                .apply(new RequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).override(x, y))
                .into(image);
    }

    /**
     * 加载圆角图片
     *
     * @param url
     * @param image
     * @param round
     */
    public void loadRoundImage(String url, ImageView image, int round) {
        GlideApp.with(mContext)
                .load(url)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(round)))
                .into(image);
    }

    /**
     * 加载圆角图片
     * 通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗,设置图片压缩比例
     *
     * @param url
     * @param image
     * @param round
     * @param x
     * @param y
     */
    public void loadRoundImage(String url, ImageView image, int round, int x, int y) {

        GlideApp.with(mContext)
                .load(url)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(round)).override(50, 50))
                .into(image);
    }

    /**
     * 加载圆形图片
     *
     * @param url
     * @param image
     */
    public void loadCircleImage(String url, ImageView image) {
        GlideApp.with(mContext)
                .load(url)
                .apply(RequestOptions.circleCropTransform()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                        .skipMemoryCache(true)//不做内存缓存
                )
                .into(image);
    }

    /**
     * 加载圆形图片
     *
     * @param url
     * @param image
     * @param number 循环播放次数
     */
    public void loadGifImage(String url, final ImageView image, final int number) {
        GlideApp.with(mContext)
                .asGif()
                .load(url)
                .apply(new RequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .placeholder(R.drawable.placeholder)
                .fallback(R.drawable.placeholder)
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        Toast.makeText(mContext, "加载失败", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable gifDrawable, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {

                            //设置循环播放次数为1次
                            gifDrawable.setLoopCount(number);
//                        getGifduration(gifDrawable);//获取Gif时长

                        return false;
                    }
                }).into(image);

    }

    /**
     * 获取Gif时长
     */
    private void getGifduration(GifDrawable gifDrawable) {
        try {
            // 计算动画时长
            int duration = 0;
            //GifDecoder decoder = gifDrawable.getDecoder();//4.0开始没有这个方法了
            /***
             * 通过反射获取时长
             */
            Drawable.ConstantState state = gifDrawable.getConstantState();
            if (state != null) {
                //不能混淆GifFrameLoader和GifState类
                Object gifFrameLoader = getValue(state, "frameLoader");
                if (gifFrameLoader != null) {
                    Object decoder = getValue(gifFrameLoader, "gifDecoder");
                    if (decoder != null && decoder instanceof GifDecoder) {
                        for (int i = 0; i < gifDrawable.getFrameCount(); i++) {
                            duration += ((GifDecoder) decoder).getDelay(i);
                        }
                    }
                }
                Log.e("Glide4.7.1", "gif播放一次动画时长:" + duration);
            }
        } catch (Throwable e) {
        }
    }


    /**
     * 通过字段名从对象或对象的父类中得到字段的值
     *
     * @param object    对象实例
     * @param fieldName 字段名
     * @return 字段对应的值
     * @throws Exception
     */
    public static Object getValue(Object object, String fieldName) throws Exception {
        if (object == null) {
            return null;
        }
        if (TextUtils.isEmpty(fieldName)) {
            return null;
        }
        Field field = null;
        Class<?> clazz = object.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(object);
            } catch (Exception e) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }

        return null;
    }


}
