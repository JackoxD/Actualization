package com.gawel.actualization;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.vanstone.appsdk.client.ISdkStatue;
import com.vanstone.trans.api.FileApi;
import com.vanstone.trans.api.SystemApi;
import com.vanstone.trans.api.constants.GlobalConstants;
import com.vanstone.utils.CommonConvert;

import java.util.Observable;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt = findViewById(R.id.txt);

//        txt.setText("To jest wersja zaktualizowana");

        actualization();
    }

    private void actualization() {
        txt.setText("Rozpoczynanie aktualizacji");

        GlobalConstants.CurAppDir = this.getFilesDir().getAbsolutePath();

        SystemApi.SystemInit_Api(0, CommonConvert.StringToBytes(
                GlobalConstants.CurAppDir + "/" + "libs/ameabi"), this, new ISdkStatue() {
            @Override
            public void sdkInitSuccessed() {
                txt.setText("Pomyślnie załadowano potrzebne biblioteki");
                Log.d(TAG, "sdkInitSuccessed: installation start");
                Log.d(TAG, "sdkInitSuccessed: File size: " + FileApi.GetFileSize_Api("/mnt/sdcard/Download/debug/app-debug.apk"));

                SystemApi.silentInstallApk_Api("/mnt/sdcard/Download/debug/app-debug.apk", getPackageName(), (s, i, s1) -> {
                    txt.setText("Ukończono instalację z wynikiem: " + s1);
                    Log.v("rel_", "===========onInstallFinished==============");
                    Log.v("rel_", "pkg :" + s);
                    Log.v("rel_", "code :" + i);
                    Log.v("rel_", "msg: " + s1);
                });
            }

            @Override
            public void sdkInitFailed() {
                txt.setText("Błąd podczas ładowania potrzebnych bibliotek");
                Log.d(TAG, "sdkInitFailed: printer");
            }
        });
    }
}