package com.example.linjun.saoyisao.decode;

import android.graphics.Bitmap;

/**
 * Created by linjun on 2017/6/1.
 */

public abstract class LuminanceSource extends com.google.zxing.LuminanceSource {
    protected LuminanceSource(int width, int height) {
        super(width, height);
    }
    public abstract Bitmap renderCroppedGreyScaleBitmap();
}
