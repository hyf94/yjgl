/*
 * 原作者: https://blog.csdn.net/sbsujjbcy/article/details/50752595
 */
package com.sucsoft.yjgl.core.jsbridge;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.format.DateFormat;

import com.sucsoft.yjgl.core.AppManager;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class JSBridgeWebChromeClient extends WebChromeClient {
    private int REQUEST_CODE = 1234;

    private android.webkit.ValueCallback<Uri[]> mUploadCallbackAboveL;
    private android.webkit.ValueCallback<Uri> mUploadCallbackBelow;
    private Uri imageUri;


    @Override
    public boolean onJsPrompt(WebView webView, String url, String message, String defaultValue, JsPromptResult result) {
        result.confirm(JSBridge.callJava(webView, message));
        return true;
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        // (1)该方法回调时说明版本API >= 21，此时将结果赋值给 mUploadCallbackAboveL，使之 != null
        mUploadCallbackAboveL = filePathCallback;
        takePhoto();
        return true;
    }

    /**
     * 调用相机
     */
    private void takePhoto() {
        // 指定拍照存储位置的方式调起相机
        String filePath = Environment.getExternalStorageDirectory() + File.separator
                + Environment.DIRECTORY_PICTURES + File.separator;
        String fileName = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        imageUri = Uri.fromFile(new File(filePath + fileName));

//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        startActivityForResult(intent, REQUEST_CODE);

        // 选择图片（不包括相机拍照）,则不用成功后发刷新图库的广播
//        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//        i.addCategory(Intent.CATEGORY_OPENABLE);
//        i.setType("image/*");
//        startActivityForResult(Intent.createChooser(i, "Image Chooser"), REQUEST_CODE);

        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        Intent Photo = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent chooserIntent = Intent.createChooser(Photo, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

//        startActivityForResult(AppManager.getAppManager().currentActivity(),chooserIntent, REQUEST_CODE);
        AppManager.getAppManager().currentActivity().startActivityForResult(chooserIntent, REQUEST_CODE);
    }
}
