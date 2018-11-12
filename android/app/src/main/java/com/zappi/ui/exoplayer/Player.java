package com.zappi.ui.exoplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import static com.facebook.react.common.ReactConstants.TAG;

public class Player extends ReactContextBaseJavaModule implements ActivityEventListener {
    static final int REQUEST_ACTIVITY = 21;

    final ReactApplicationContext reactContext;
    private Promise myPromise;

    public Player(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.reactContext.addActivityEventListener(this);
    }

    @ReactMethod
    public void play(final String title,final String url,final String sub, final Boolean subShow, Promise promise) {
        Activity currentActivity = getCurrentActivity();

        if (currentActivity == null) {
            promise.reject("E_ACTIVITY_DOES_NOT_EXIST");
            return;
        }

        myPromise = promise;

        final Intent intent = new Intent(getCurrentActivity(), SimpleVideoStream.class);
        Bundle extras = new Bundle();
        extras.putString("title", title);
        extras.putString("url", url);
        extras.putString("sub", sub);
        extras.putBoolean("subShow", subShow);
        intent.putExtras(extras);
        if (intent.resolveActivity(this.reactContext.getPackageManager()) != null) {
            try {
                this.reactContext.startActivityForResult(intent, REQUEST_ACTIVITY, null);
            } catch (Exception ex) {
                myPromise.reject("E_FAILED_TO_PLAYER");
                myPromise = null;
            }
        }
    }

    @Override
    public String getName() {
        return "Player";
    }

    @Override
    public void onActivityResult(
            Activity activity,
            int requestCode,
            int resultCode,
            Intent data
    ) {
        this.onActivityResult(requestCode, resultCode, data);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (myPromise == null) return;
        Log.v(TAG, "onActivityResult: " + requestCode + " " + resultCode);
        if (Activity.RESULT_OK == requestCode) {
            myPromise.resolve("Ok.");
        } else if (Activity.RESULT_CANCELED == resultCode) {
            String errMsg = "Error";
            if (data != null && data.hasExtra("message")) {
                errMsg = data.getStringExtra("message");
            }
            myPromise.reject(errMsg);
        }
    }

    public void onNewIntent(Intent intent) {
    }

}
