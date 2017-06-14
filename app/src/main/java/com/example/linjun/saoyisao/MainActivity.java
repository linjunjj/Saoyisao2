package com.example.linjun.saoyisao;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.linjun.saoyisao.base.BaseAppCompatActivity;
import com.example.linjun.saoyisao.camera.CameraManager;
import com.example.linjun.saoyisao.camera.PreviewFrameShotListener;
import com.example.linjun.saoyisao.decode.DecodeThread;
import com.example.linjun.saoyisao.decode.LuminanceSource;
import com.example.linjun.saoyisao.decode.PlanarYUVLuminanceSource;
import com.example.linjun.saoyisao.decode.RGBLuminanceSource;
import com.example.linjun.saoyisao.util.DocumentUtil;
import com.example.linjun.saoyisao.util.Size;
import com.example.linjun.saoyisao.view.CaptureView;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends BaseAppCompatActivity {

    @BindView(R.id.sv_preview)
    SurfaceView svPreview;
    @BindView(R.id.cv_capture)
    CaptureView cvCapture;
    @BindView(R.id.btn_album)
    Button btnAlbum;
    @BindView(R.id.cb_capture_flash)
    CheckBox cbCaptureFlash;
    @BindView(R.id.layout_capture)
    RelativeLayout layoutCapture;
    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;
    private static final long VIBRATE_DURATION = 200L;
    private static final int REQUEST_CODE_ALBUM = 0;
    public static final String EXTRA_RESULT = "result";
    public static final String EXTRA_BITMAP = "bitmap";
    private CameraManager mCameraManager;
    private DecodeThread mDecodeThread;
    private Rect previewFrameRect = null;
    private boolean isDecoding = false;
    private  String scandata;
    @Override
    protected int getLayoutId() {
        ButterKnife.bind(this);
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        cbCaptureFlash.setOnCheckedChangeListener(this);
        cbCaptureFlash.setEnabled(false);
        btnAlbum.setOnClickListener(this);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            btnAlbum.setVisibility(View.GONE);
        }
         svPreview.getHolder().addCallback((SurfaceHolder.Callback) MainActivity.this);
        mCameraManager = new CameraManager(this);
        mCameraManager.setPreviewFrameShotListener((PreviewFrameShotListener) this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCameraManager.initCamera(holder);
        if (!mCameraManager.isCameraAvailable()) {
            Toast.makeText(MainActivity.this, "相机启动失败！！", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (mCameraManager.isFlashlightAvailable()) {
            btnAlbum.setEnabled(true);
        }
        mCameraManager.startPreview();
        if (!isDecoding) {
            mCameraManager.requestPreviewFrameShot();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCameraManager.stopPreview();
        if (mDecodeThread != null) {
            mDecodeThread.cancel();
        }
        mCameraManager.release();
    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mCameraManager.enableFlashlight();
        } else {
            mCameraManager.disableFlashlight();
        }
    }

    @Override
    public void onPreviewFrame(byte[] date, Size dataSize) {
        if (mDecodeThread != null) {
            mDecodeThread.cancel();
        }
        if (previewFrameRect == null) {
            previewFrameRect = mCameraManager.getPreviewFrameRect(cvCapture.getFrameRect());
        }
        PlanarYUVLuminanceSource luminanceSource = new PlanarYUVLuminanceSource(date, dataSize, previewFrameRect);
        mDecodeThread = new DecodeThread(luminanceSource, MainActivity.this);
        isDecoding = true;
        mDecodeThread.execute();
    }

    @Override
    public void onDecodeSuccess(Result result, LuminanceSource source, Bitmap bitmap) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(VIBRATE_DURATION);
        isDecoding = false;
        if(bitmap.getWidth()>100||bitmap.getHeight()>100){
            Matrix matrix = new Matrix();
            matrix.postScale(100f/bitmap.getWidth(),100f/bitmap.getHeight());
            Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            bitmap.recycle();
            bitmap = resizeBmp;
        }
           scandata=result.getText();
        Pattern pattern = Pattern.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
      if (pattern.matcher(scandata).matches()){
          Intent intent= new Intent();
          intent.setAction("android.intent.action.VIEW");
          Uri content_url = Uri.parse(scandata);
          intent.setData(content_url);
          startActivity(intent);
      }else {
        Intent intent1=new Intent(MainActivity.this,Viewdata.class);
          intent1.putExtra("date", scandata);
          startActivity(intent1);
      }

    }

    @Override
    public void onDecodeFailed(LuminanceSource source) {
        if (source instanceof RGBLuminanceSource) {

        }
        isDecoding = false;
        mCameraManager.requestPreviewFrameShot();
    }

    @Override
    public void foundPossibleResultPoint(ResultPoint resultPoint) {
        cvCapture.addPossibleResultPoint(resultPoint);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_album:
                Intent intent = null;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                }
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                intent.putExtra("return-data", true);
                startActivityForResult(intent, REQUEST_CODE_ALBUM);
                break;
            default:
                break;
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ALBUM && resultCode == RESULT_OK && data != null) {
            Bitmap cameraBitmap = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String path = DocumentUtil.getPath(MainActivity.this, data.getData());
                cameraBitmap = DocumentUtil.getBitmap(path);
            } else {
                // Not supported in SDK lower that KitKat
            }
            if (cameraBitmap != null) {
                if (mDecodeThread != null) {
                    mDecodeThread.cancel();
                }
                int width = cameraBitmap.getWidth();
                int height = cameraBitmap.getHeight();


                int[] pixels = new int[width * height];
                cameraBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                RGBLuminanceSource luminanceSource = new RGBLuminanceSource(pixels, new Size(width, height));
                mDecodeThread = new DecodeThread(luminanceSource, MainActivity.this);
                isDecoding = true;
                mDecodeThread.execute();
            }
        }
    }
}
