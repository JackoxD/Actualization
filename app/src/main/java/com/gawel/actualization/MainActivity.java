package com.gawel.actualization;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vanstone.appsdk.client.ISdkStatue;
import com.vanstone.trans.api.FileApi;
import com.vanstone.trans.api.SystemApi;
import com.vanstone.trans.api.constants.GlobalConstants;
import com.vanstone.utils.CommonConvert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    TextView txt;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt = findViewById(R.id.txt);
        button = findViewById(R.id.button);

        button.setOnClickListener(v -> {
            if(isStoragePermissionGranted())
                actualization();
        });

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;
            txt.setText("Version name: " + version +
                    "\nVersion Code: " + verCode);
            if(verCode == 2)
                button.setEnabled(false);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//        txt.setText("To jest wersja zaktualizowana");



    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 501);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            actualization();
        }
    }

    private void actualization() {
        Log.d(TAG, "Rozpoczynanie aktualizacji");

        GlobalConstants.CurAppDir = this.getFilesDir().getAbsolutePath();

        SystemApi.SystemInit_Api(0, CommonConvert.StringToBytes(
                GlobalConstants.CurAppDir + "/" + "libs/ameabi"), this, new ISdkStatue() {
            @Override
            public void sdkInitSuccessed() {
               Log.d(TAG, "Pomyślnie załadowano potrzebne biblioteki");
                Log.d(TAG, "sdkInitSuccessed: installation start");
                Log.d(TAG, "sdkInitSuccessed: File size: " + FileApi.GetFileSize_Api("/mnt/sdcard/Download/debug/app-debug.apk"));

                SystemApi.silentInstallApk_Api("/mnt/sdcard/Download/debug/app-debug.apk", getPackageName(), (s, i, s1) -> {
                   Log.d(TAG, "Ukończono instalację z wynikiem: " + s1);
                    Log.v("rel_", "===========onInstallFinished==============");
                    Log.v("rel_", "pkg :" + s);
                    Log.v("rel_", "code :" + i);
                    Log.v("rel_", "msg: " + s1);

                });
            }

            @Override
            public void sdkInitFailed() {
               Log.d(TAG, "Błąd podczas ładowania potrzebnych bibliotek");
                Log.d(TAG, "sdkInitFailed: printer");
            }
        });
    }
}