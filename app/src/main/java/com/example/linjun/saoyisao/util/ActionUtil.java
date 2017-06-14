package com.example.linjun.saoyisao.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;

/**
 * Created by linjun on 2017/6/14.
 */
public class ActionUtil {
    private static Intent intent;

    /**
     * 开启一个Intent
     *
     * @param context
     * @param activityClass
     */
    public static void actionStart(Context context, Class<?> activityClass) {
        intent = new Intent(context, activityClass);
        context.startActivity(intent);
    }

    /**
     * 开启一个Intent
     *
     * @param context
     * @param action
     * @param uri
     */
    public static void actionStart(Context context, String action, Uri uri) {
        intent = new Intent(action, uri);
        context.startActivity(intent);
    }

    /**
     * 开启一个带回调的Intent
     *
     * @param context
     * @param activityClass
     * @param requestCode
     */
    public static void actionStart(Context context, Class<?> activityClass, int requestCode) {
        intent = new Intent(context, activityClass);
        ((FragmentActivity) context).startActivityForResult(intent, requestCode);
    }

    /**
     * 开启一个带回调的Intent
     *
     * @param context
     * @param action
     * @param requestCode
     */
    public static void actionStart(Context context, String action, int requestCode) {
        intent = new Intent(action);
        ((FragmentActivity) context).startActivityForResult(intent, requestCode);
    }
}
