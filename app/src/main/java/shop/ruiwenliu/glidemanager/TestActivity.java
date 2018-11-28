package shop.ruiwenliu.glidemanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import shop.ruiwenliu.glidemanager.until.glide.GlideApp;
import shop.ruiwenliu.glidemanager.until.glide.ProgressInterceptor;
import shop.ruiwenliu.glidemanager.until.glide.ProgressListener;

public class TestActivity extends AppCompatActivity {

    private String url = "http://wx2.sinaimg.cn/mw690/b1072857ly1fm2xa2am75j20rsa8xqv8.jpg";//超长图
    ProgressDialog progressDialog;
    @BindView(R.id.image)
    SubsamplingScaleImageView mImage;

    public static Intent getIntent(Context context) {
        return new Intent(context, TestActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("加载中");
        dwoonloadImage3(url);
    }

    public void dwoonloadImage3(final String path) {
        ProgressInterceptor.addListener(path, new ProgressListener() {
            @Override
            public void onProgress(int progress) {
                progressDialog.setProgress(progress);
            }
        });
        SimpleTarget<File> simpleTarget = new SimpleTarget<File>() {
            @Override
            public void onStart() {
                super.onStart();
                progressDialog.show();
            }

            @Override
            public void onResourceReady(File resource, Transition<? super File> transition) {
                mImage.setImage(ImageSource.uri(resource.getAbsolutePath()),
                        new ImageViewState(1.0f, new PointF(0, 0), 0));
                progressDialog.dismiss();
                ProgressInterceptor.removeListener(path);
            }
        };
        Glide.with(this).asFile().load(path).into(simpleTarget);
    }

    @Override
    protected void onDestroy() {
        if (mImage != null) {
            mImage.recycle();
        }
        super.onDestroy();
    }


    @OnClick({R.id.tv_back, R.id.tv_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_save:
                downImage(url);
                break;
        }
    }

    private void downImage(final String path){
        ProgressInterceptor.addListener(path, new ProgressListener() {
            @Override
            public void onProgress(int progress) {
                progressDialog.setProgress(progress);
            }
        });
        progressDialog.show();
        GlideApp.with(this).asFile().load(path).listener(new RequestListener<File>() {

            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                progressDialog.dismiss();
                ProgressInterceptor.removeListener(path);
                return false;
            }

            @Override
            public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                if (resource != null) {
                    progressDialog.dismiss();
                    ProgressInterceptor.removeListener(path);
                    saveImage(resource.getAbsolutePath());
                }
                return false;
            }
        }).submit();
    }

    private void saveImage(final String path) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> integer) throws Exception {
                try {
                    File folder = new File(AppConfig.DEFAULT_IMAGE_PATH);
                    if (!folder.exists()) {
                        folder.mkdir();
                    }
                    String fileName = System.currentTimeMillis() + ".jpg";
                    File file = new File(folder, fileName);
                    FileOutputStream fos = new FileOutputStream(file);
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    if (bitmap != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();
                        MediaStore.Images.Media.insertImage(TestActivity.this.getContentResolver(),
                                file.getAbsolutePath(), fileName, null);
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                Uri.fromFile(new File(file.getPath()))));
                    }
                    integer.onNext(1);
                } catch (Exception ex) {
                    integer.onNext(0);
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        switch (integer) {
                            case 1:
                                Toast.makeText(TestActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                                break;
                            case 0:
                                Toast.makeText(TestActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                })
        ;

    }
}
