package com.example.liu.autotanslate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wenhuiliu.EasyEnglishReading.Translate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Administrator on 2015/4/14.
 * 实现ImageVIew播放动画
 */
public class MyGifView extends ImageView {

    public static final int IMAGE_TYPE_UNKNOWN = 0;
    public static final int IMAGE_TYPE_STATIC = 1;
    public static final int IMAGE_TYPE_DYNAMIC = 2;

    public static final int DECODE_STATUS_UNDECODE = 0;
    public static final int DECODE_STATUS_DECODING = 1;
    public static final int DECODE_STATUS_DECODED = 2;

    private GifDecoder decoder;
    private Bitmap bitmap;

    public int imageType = IMAGE_TYPE_UNKNOWN;
    public int decodeStatus = DECODE_STATUS_UNDECODE;

    private int wScreen;
    private int hScreen;
    private int width;
    private int height;

    private long time;
    private int index;

    private int resId;
    private String filePath;

    private boolean playFlag = false;

    public MyGifView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructor
     */
    public MyGifView(Context context) {
        super(context);
    }

    private InputStream getInputStream() {
        if (filePath != null)
            try {
                return new FileInputStream(filePath);
            } catch (FileNotFoundException e) {
            }
        if (resId > 0)
            return getContext().getResources().openRawResource(resId);
        return null;
    }

    /**
     * set gif file path
     *
     * @param filePath
     */
    public void setGif(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        DisplayMetrics dm =getResources().getDisplayMetrics();
        wScreen = dm.widthPixels;
        hScreen = dm.heightPixels;
        setGif(filePath, bitmap);
    }

    /**
     * set gif file path and cache image
     *
     * @param filePath
     * @param cacheImage
     */
    public void setGif(String filePath, Bitmap cacheImage) {
        this.resId = 0;
        this.filePath = filePath;
        imageType = IMAGE_TYPE_UNKNOWN;
        decodeStatus = DECODE_STATUS_UNDECODE;
        playFlag = false;
        bitmap = cacheImage;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        setLayoutParams(new LinearLayout.LayoutParams(width, height));
    }

    /**
     * set gif resource id
     *
     * @param resId
     */
    public void setGif(int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
        DisplayMetrics dm =getResources().getDisplayMetrics();
        wScreen = dm.widthPixels;
        hScreen = dm.heightPixels;
        setGif(resId, bitmap);
    }

    /**
     * set gif resource id and cache image
     *
     * @param resId
     * @param cacheImage
     */
    public void setGif(int resId, Bitmap cacheImage) {
        this.filePath = null;
        this.resId = resId;
        imageType = IMAGE_TYPE_UNKNOWN;
        decodeStatus = DECODE_STATUS_UNDECODE;
        playFlag = false;
        bitmap = cacheImage;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
//       显示播放动画的屏幕大小
        setLayoutParams(new LinearLayout.LayoutParams(wScreen, hScreen));
    }

    private void decode() {
        release();
        index = 0;

        decodeStatus = DECODE_STATUS_DECODING;

        new Thread() {
            @Override
            public void run() {
                decoder = new GifDecoder();
                decoder.read(getInputStream());
                if (decoder.width == 0 || decoder.height == 0) {
                    imageType = IMAGE_TYPE_STATIC;
                } else {
                    imageType = IMAGE_TYPE_DYNAMIC;
                }
                postInvalidate();
                time = System.currentTimeMillis();
                decodeStatus = DECODE_STATUS_DECODED;
            }
        }.start();
    }

    public void release() {
        decoder = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.d("屏幕宽度和高度", wScreen + "/" + hScreen);
//        Log.d("图片宽度和高度", width + "/" + height);
        float scale_x = wScreen / width;
        float scale_y = hScreen / height;
//        Log.d("缩放比",scale_x+":"+scale_y);
        try {
            if (decodeStatus == DECODE_STATUS_UNDECODE) {
                canvas.drawBitmap(bitmap, 0, 0, null);
                if (playFlag) {
                    decode();
                    invalidate();
                }
            } else if (decodeStatus == DECODE_STATUS_DECODING) {
                canvas.drawBitmap(bitmap, 0, 0, null);
                invalidate();
            } else if (decodeStatus == DECODE_STATUS_DECODED) {
                if (imageType == IMAGE_TYPE_STATIC) {
                    canvas.drawBitmap(bitmap, 0, 0, null);
                } else if (imageType == IMAGE_TYPE_DYNAMIC) {
                    if (playFlag) {
                        long now = System.currentTimeMillis();

                        if (time + decoder.getDelay(index) < now) {
                            time += decoder.getDelay(index);
                            incrementFrameIndex();
                        }
                        Bitmap bitmap = decoder.getFrame(index);
                        if (bitmap != null) {
//                        Log.d(Translate.class.getSimpleName(),"开始画了");
//                        TODO 关于拉缩放比例影响全屏显示
                            canvas.scale(scale_x-0.5f, scale_y-0.3f);
                            canvas.drawBitmap(bitmap, 0, 0, null);
                        }
                        invalidate();
                    } else {
                        Log.d(Translate.class.getSimpleName(), "开始画了1");
                        Bitmap bitmap = decoder.getFrame(index);
                        canvas.drawBitmap(bitmap, 0, 0, null);
                    }
                } else {
                    Log.d(Translate.class.getSimpleName(), "开始画了2");
                    canvas.drawBitmap(bitmap, 0, 0, null);
                }
            }
            playFlag = true;
            invalidate();
        }catch (NullPointerException w){
            Log.d(MyGifView.class.getSimpleName(),"空指针异常");
        }
    }

    private void incrementFrameIndex() {
        index++;
        if (index >= decoder.getFrameCount()) {
            index = 0;
        }
    }
}
