package shop.ruiwenliu.glidemanager.until.glide;

import android.content.Context;
import android.os.Environment;
import android.os.RecoverySystem;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ViewTarget;

import java.io.InputStream;

import okhttp3.OkHttpClient;
import shop.ruiwenliu.glidemanager.R;

/**
 * Created by Amuse
 * Data:2018/11/27 0027
 * Desc:glide缓存处理
 */
@GlideModule
public class GlideCache extends AppGlideModule {
    public static final int GLIDE_DISK_SIZE = 1024 * 1024 * 200;
    public static final int GLIDE_MEMORY_SIZE =5 * 1024 * 1024;
    public static final String GLIDE_DISK_NAME="GlideManagerCache";

    @Override
    public boolean isManifestParsingEnabled() {
//        return super.isManifestParsingEnabled();
        return false;
    }

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

        //自定义缓存目录，磁盘缓存给150M 另外一种设置缓存方式
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, GLIDE_DISK_NAME, GLIDE_DISK_SIZE));
        builder.setMemoryCache(new LruResourceCache( GLIDE_MEMORY_SIZE));
        //配置图片缓存格式 默认格式为8888
        builder.setDefaultRequestOptions(RequestOptions.formatOf(DecodeFormat.PREFER_ARGB_8888));
    }


    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new ProgressInterceptor());
        OkHttpClient okHttpClient = builder.build();
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(okHttpClient));
    }

}

