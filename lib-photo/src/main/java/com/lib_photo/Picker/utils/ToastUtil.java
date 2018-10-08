package com.lib_photo.Picker.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Describe:
 * Created by Rain on 2018/4/26.
 */

public class ToastUtil {
    private static Toast mToast;

    public static void showToast(Context context, String text) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
        mToast.setText(text);
        mToast.show();
    }
}
