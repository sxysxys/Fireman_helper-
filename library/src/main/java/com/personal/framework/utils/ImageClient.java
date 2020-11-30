package com.personal.framework.utils;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ImageClient {
    private final String TAG = ImageClient.class.getSimpleName();
    private Context context;
    private int placeholderResId;
    private int errorResId;
    private Drawable placeholderDrawable;
    private Drawable errorDrawable;
    private Object tag;
    private int targetWidth;
    private int targetHeight;
    private boolean centerCrop;
    private boolean skipCache = false;

    private BitmapTransformation transformation;
    private long signKey = -1L;

    ImageClient(Context context) {
        this.context = context;
    }

    public static ImageClient with(Context context) {
        return new ImageClient(context);
    }

    public ImageClient placeholder(int placeholderResId) {
        this.placeholderResId = placeholderResId;
        return this;
    }

    public ImageClient transform(BitmapTransformation bitmapTransformation) {
        this.transformation = bitmapTransformation;
        return this;
    }

    public ImageClient error(int errorResId) {
        this.errorResId = errorResId;
        return this;
    }

    public ImageClient placeholderDrawable(Drawable placeholderDrawable) {
        this.placeholderDrawable = placeholderDrawable;
        return this;
    }

    public ImageClient errorDrawable(Drawable errorDrawable) {
        this.errorDrawable = errorDrawable;
        return this;
    }

    public ImageClient tag(Object tag) {
        this.tag = tag;
        return this;
    }

    public ImageClient resize(int targetWidth, int targetHeight) {
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        return this;
    }

    public ImageClient resizeDimen(int targetWidthResId, int targetHeightResId) {
        this.targetWidth = this.context.getResources().getDimensionPixelSize(targetWidthResId);
        this.targetHeight = this.context.getResources().getDimensionPixelSize(targetHeightResId);
        return this;
    }

    public ImageClient centerCrop(boolean centerCrop) {
        this.centerCrop = centerCrop;
        return this;
    }

    public void showImage(Object url, ImageView imageView, final ImageClient.Callback callback) {
        if (imageView == null) {
            LogUtil.w(this.TAG, "imageView is null");
        } else {
            this.getRequestCreator(url, false).listener(new RequestListener() {
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                    if (null != callback) {
                        callback.onFailure();
                    }

                    return false;
                }

                public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                    if (null != callback) {
                        Bitmap bmp = null;
                        if (resource instanceof Drawable) {
                            bmp = ImageUtil.drawableToBitmap((Drawable) resource);
                        }

                        callback.onSuccess(bmp);
                    }

                    return false;
                }
            }).into(imageView);
        }
    }

    public void showImage(Object url, ImageView imageView) {
        this.showImage(url, imageView, (ImageClient.Callback) null);
    }

    public ImageClient skipCache(boolean skipCache) {
        this.skipCache = skipCache;
        return this;
    }

    public ImageClient signKey(long version) {
        this.signKey = version;
        return this;
    }

    public ImageClient pause(String tag) {
        Glide.with(this.context).pauseRequests();
        return this;
    }

    public ImageClient resume(String tag) {
        Glide.with(this.context).resumeRequests();
        return this;
    }

    public Bitmap getBitmap(String url) {
        try {
            return (Bitmap) Glide.with(this.context).asBitmap().load(url).apply(this.getOptions()).submit().get();
        } catch (InterruptedException var3) {
            var3.printStackTrace();
        } catch (ExecutionException var4) {
            var4.printStackTrace();
        }

        return null;
    }

    private RequestOptions getOptions() {
        RequestOptions options = new RequestOptions();
        if (this.placeholderResId != 0) {
            options.placeholder(this.placeholderResId);
        } else if (this.placeholderDrawable != null) {
            options.placeholder(this.placeholderDrawable);
        }

        if (this.errorResId != 0) {
            options.error(this.errorResId);
        } else if (this.errorDrawable != null) {
            options.error(this.errorDrawable);
        }

        if (this.targetWidth > 0 && this.targetHeight > 0) {
            options.override(this.targetWidth, this.targetHeight);
        }
        if (this.skipCache) {
            options.skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
        }
        if (centerCrop && transformation != null) {
            options.transforms(new CenterCrop(),transformation);
        } else {
            if (centerCrop) {
                options.centerCrop();
            }
            if (transformation != null) {
                options.transform(transformation);
            }
        }
        return options;
    }

    @Nullable
    private RequestBuilder getRequestCreator(Object url, boolean asBitmap) {
        RequestBuilder requestBuilder;
        if (asBitmap) {
            requestBuilder = Glide.with(this.context).asBitmap().load(url);
        } else {
            requestBuilder = Glide.with(this.context).load(url);
        }

        return requestBuilder.apply(this.getOptions());
    }

    public void fetch(@NonNull String url) {
        this.fetch(url, (ImageClient.Callback) null);
    }

    public void fetch(@NonNull String url, final ImageClient.Callback callback) {
        this.getRequestCreator(url, true).into(new SimpleTarget<Bitmap>() {
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                if (null != callback) {
                    callback.onFailure();
                }

                super.onLoadFailed(errorDrawable);
            }

            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                if (null != callback) {
                    callback.onSuccess(resource);
                }

            }
        });
    }

    public void showTargeImage(Object url, ImageView imageView) {
        if (imageView == null) {
            LogUtil.w(this.TAG, "imageView is null");
        } else {
            this.getRequestCreator(url, true).into(new ImageClient.MyBitmapImageViewTarget(imageView));
        }
    }

    public void clearCache() {
        Glide.get(this.context).clearMemory();
        Glide.get(this.context).clearDiskCache();
    }

    private class MyBitmapImageViewTarget extends BitmapImageViewTarget {
        private MyBitmapImageViewTarget(ImageView view) {
            super(view);
        }

        public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            super.onResourceReady(resource, transition);
            if (resource != null && ((ImageView) this.view).getScaleType() != ScaleType.CENTER_CROP) {
                ((ImageView) this.view).setScaleType(ScaleType.CENTER_CROP);
            }

        }

        protected void setResource(Bitmap resource) {
            super.setResource(resource);
        }

        public void onLoadFailed(@Nullable Drawable errorDrawable) {
            if (errorDrawable != null && this.view != null && ((ImageView) this.view).getScaleType() != ScaleType.CENTER_CROP) {
                ((ImageView) this.view).setScaleType(ScaleType.CENTER_CROP);
            }

            super.onLoadFailed(errorDrawable);
        }

        public void onLoadStarted(Drawable placeholder) {
            if (placeholder != null && this.view != null && ((ImageView) this.view).getScaleType() != ScaleType.CENTER_CROP) {
                ((ImageView) this.view).setScaleType(ScaleType.CENTER_CROP);
            }

            super.onLoadStarted(placeholder);
        }

        public void onLoadCleared(Drawable placeholder) {
            if (placeholder != null && this.view != null && ((ImageView) this.view).getScaleType() != ScaleType.CENTER_CROP) {
                ((ImageView) this.view).setScaleType(ScaleType.CENTER_CROP);
            }
            super.onLoadCleared(placeholder);
        }
    }

    public interface Callback {
        void onSuccess(Bitmap var1);

        void onFailure();
    }
}
