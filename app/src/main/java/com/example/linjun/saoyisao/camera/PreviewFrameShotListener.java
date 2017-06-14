package com.example.linjun.saoyisao.camera;

import com.example.linjun.saoyisao.util.Size;

/**
 * Created by linjun on 2017/6/1.
 */

public interface PreviewFrameShotListener {
    void onPreviewFrame(byte[] date, Size framwSize);
}
