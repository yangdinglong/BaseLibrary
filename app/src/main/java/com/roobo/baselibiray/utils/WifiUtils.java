package com.roobo.baselibiray.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by HP on 2019/3/7.
 */

public class WifiUtils {

    public static final String TAG = WifiUtils.class.getSimpleName();

    private static int STATUS_SCANNING = 1;
    private static int STATUS_READY = 0;

    private static WifiUtils instance;

    private Context mContext;

    private int mStatus = STATUS_READY;

    private WifiManager mWifiManager;

    private ScanResultListener mScanResultListener;

    private ArrayList<ScanResult> mWifiList = new ArrayList<>();

    private BroadcastReceiver mBroadcastReceiver;

    public interface ScanResultListener {
        void onResult(ArrayList<ScanResult> wifiList);
    }

    private WifiUtils(Context mContext) {
        this.mContext = mContext.getApplicationContext();
        mWifiManager = (WifiManager) this.mContext.getApplicationContext().getSystemService(WIFI_SERVICE);
    }

    public static WifiUtils getInstance(Context mContext) {
        if (instance == null) {
            synchronized (WifiUtils.class) {
                if (instance == null) {
                    instance = new WifiUtils(mContext);
                }
            }
        }
        return instance;
    }

    /**
     * 需要定位服務
     *
     * @param scanResultListener
     */
    public void startScan(ScanResultListener scanResultListener) {
        Log.d(TAG, "[startScan]");
        this.mScanResultListener = scanResultListener;
        if (!mWifiManager.isWifiEnabled()) {
            Log.d(TAG, "plz open wifi service");
            return;
        }

        if (mStatus == STATUS_SCANNING) {
            Log.d(TAG, "wifi is scanning");
            return;
        }
        mStatus = STATUS_SCANNING;
        if (!mWifiList.isEmpty()) {
            mWifiList.clear();
        }
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "[onReceive]");
                if (intent == null)
                    return;
                parseScanResults();
                if (mScanResultListener != null) {
                    mScanResultListener.onResult(mWifiList);
                }
                stopScan();
            }
        };
        mContext.registerReceiver(mBroadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager.startScan();
    }

    public void stopScan() {
        Log.d(TAG, "[stopScan]");
        if (mStatus == STATUS_READY) {
            Log.d(TAG, "wifimanager is already stop");
            return;
        }
        this.mScanResultListener = null;
        mStatus = STATUS_READY;
        if (mBroadcastReceiver != null) {
            mContext.unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }
    }

    private void parseScanResults() {
        List<ScanResult> scanResults = mWifiManager.getScanResults();
        if (scanResults == null || scanResults.isEmpty())
            return;
        Map<String, ScanResult> wifiMap = new HashMap<>(scanResults.size());
        for (ScanResult scanResult : scanResults) {
            if (wifiMap.get(scanResult.SSID) == null) {
                wifiMap.put(scanResult.SSID, scanResult);
            } else {
                if (!is5GHz(scanResult.frequency))
                    wifiMap.put(scanResult.SSID, scanResult);
            }
        }
        mWifiList.addAll(wifiMap.values());
    }

    public static boolean is5GHz(int freq) {
        return freq > 4900 && freq < 5900;
    }
}
