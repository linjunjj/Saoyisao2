package com.example.linjun.saoyisao.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;

import com.example.linjun.saoyisao.camera.PreviewFrameShotListener;
import com.example.linjun.saoyisao.decode.DecodeListener;

/**
 * Created by linjun on 2017/6/2.
 */

public   abstract   class BaseAppCompatActivity extends AppCompatActivity implements SurfaceHolder.Callback, PreviewFrameShotListener, DecodeListener,
        CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    protected abstract int getLayoutId();
    protected abstract void initView();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(getLayoutId());
        initView();
    }


    private void fullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            );
        }
    }



}
