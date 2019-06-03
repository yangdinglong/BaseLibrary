package com.roobo.baselibiray.utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.WIFI_SERVICE;

/***
 * 权限申请工具
 */

public class PermissionsUtils {

    public static final int REQUEST_CODE_PERMISSION = 1;

    /**
     * 跳转打开定位页面，只是打开手机定位功能
     *
     * @param activity
     */
    public static void openLocation(Activity activity) {
        if (isOpenLocation(activity)) {
            return;
        }
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivity(intent);
    }

    public static boolean isOpenLocation(Activity activity) {
        // 6.0以后BLE搜索还需要打开位置服务
        LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        boolean locationGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean locationNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!locationGPSEnabled && !locationNetworkEnable) {
            return false;
        }
        return true;
    }

    /**
     * 跳转打开蓝牙页面
     *
     * @param activity
     */
    public static void openBluetooth(Activity activity) {
        if (isOpenBluetooth()) {
            return;
        }
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivity(intent);
    }

    public static boolean isOpenBluetooth() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    public static void openWifi(Activity activity) {
        if (isOpenWifi(activity)) {
            return;
        }
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        activity.startActivity(intent);
    }

    public static boolean isOpenWifi(Activity activity) {
        WifiManager manager = (WifiManager) activity.getApplicationContext().getSystemService(WIFI_SERVICE);
        return manager.isWifiEnabled();
    }

    public static void requestLocationPermission(Activity activity, int requestCode) {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat
                .checkSelfPermission(activity,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
        }
    }

    public static void requestRecordPermission(Activity activity, int requestCode) {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat
                .checkSelfPermission(activity,
                        Manifest.permission.RECORD_AUDIO)) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.RECORD_AUDIO}, requestCode);
        }
    }

    public static void requestFilePermission(Activity activity, int requestCode) {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat
                .checkSelfPermission(activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
        }
    }

    public static void requestPermissions(Activity activity, String[] permission, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity,
                    permission, requestCode);
        }
    }

    public static boolean hasPermission(Activity activity, String permission) {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat
                .checkSelfPermission(activity,
                        permission)) {
            return true;
        }
        return false;
    }
}
