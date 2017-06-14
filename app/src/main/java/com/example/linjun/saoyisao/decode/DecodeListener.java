package com.example.linjun.saoyisao.decode;
import android.graphics.Bitmap;


import com.google.zxing.Result;

import com.google.zxing.ResultPointCallback;

/**
 * Created by linjun on 2017/6/1.
 */

public interface DecodeListener extends ResultPointCallback{
    void onDecodeSuccess(Result result, LuminanceSource source, Bitmap bitmap);

     void onDecodeFailed(LuminanceSource source);

}
