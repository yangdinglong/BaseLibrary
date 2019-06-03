package com.roobo.baselibiray.utils;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import java.util.ArrayList;
import java.util.UUID;


/**
 * Created by HP on 2019/3/7.
 */

public class BluetoothUtils {

    public static final String TAG = BluetoothUtils.class.getSimpleName();

    public static final UUID UUID_WIFI_SERVICE = UUID.fromString("00001111-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_WRITE_CHARACTERISTIC = UUID.fromString("00002222-0000-1000-8000-00805f9b34fb");

    public static final String DEVICE_NAME_PREFIX = "MeetPro_";

    private static int STATUS_SCANNING = 1;
    private static int STATUS_READY = 0;

    public static final int WHAT_TIMEOUT_STOP_SCAN = 1;
    public static final int WHAT_SEND_INFO_SUCCESS = 2;
    public static final int WHAT_SEND_INFO_FAIL = 3;
    //扫描超时时间
    public static final long TIMEOUT_SCAN = 5;

    private int mStatus = STATUS_READY;

    private ArrayList<BluetoothDevice> mBTList = new ArrayList<>();

    private ScanResultListener mScanResultListener;

    private SendWifiInfoListener mSendWifiInfoListener;

    public static BluetoothUtils instance;

    private Context mContext;

    public interface ScanResultListener {
        void onScanResult(ArrayList<BluetoothDevice> deviceList);
    }

    public interface SendWifiInfoListener {
        void onSendFail(String result);

        void onSendSuccess(String result);
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_TIMEOUT_STOP_SCAN:
                    if (BluetoothUtils.this.mScanResultListener != null) {
                        mScanResultListener.onScanResult(mBTList);
                    }
                    stopScan();
                    break;
                case WHAT_SEND_INFO_FAIL:
                    if (mSendWifiInfoListener != null) {
                        mSendWifiInfoListener.onSendFail(msg.obj.toString());
                        mSendWifiInfoListener = null;
                    }
                    break;
                case WHAT_SEND_INFO_SUCCESS:
                    if (mSendWifiInfoListener != null) {
                        mSendWifiInfoListener.onSendSuccess(msg.obj.toString());
                        mSendWifiInfoListener = null;
                    }
                    break;
            }


        }
    };

    private BluetoothAdapter.LeScanCallback mBTCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            String deviceName = bluetoothDevice.getName();
            if (deviceName == null) {
                return;
            }
            Log.d(TAG, "[onLeScan]:device=" + deviceName);
            if (!deviceName.startsWith(DEVICE_NAME_PREFIX)) {
                return;
            }
            if (!containBluetoothDevice(bluetoothDevice)) {
                mBTList.add(bluetoothDevice);
            }
        }
    };

    private BluetoothUtils(Context context) {
        this.mContext = context.getApplicationContext();
        HandlerThread mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();
    }

    public static BluetoothUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (BluetoothUtils.class) {
                if (instance == null) {
                    instance = new BluetoothUtils(context);
                }
            }
        }
        return instance;
    }

    private boolean containBluetoothDevice(BluetoothDevice device) {
        Log.d(TAG, "[containBluetoothDevice]");
        for (BluetoothDevice ble : mBTList) {
            if (ble.equals(device)) {
                return true;
            }
        }
        return false;
    }

    /**
     * s
     * 需要定位服務
     *
     * @param scanResultListener
     */
    public void startScan(ScanResultListener scanResultListener) {
        this.mScanResultListener = scanResultListener;
        Log.d(TAG, "[startScan]");
        if (mStatus == STATUS_SCANNING) {
            Log.d(TAG, "BluetoothAdapter is scanning");
            return;
        }
        if (!mBTList.isEmpty()) {
            mBTList.clear();
        }
        mStatus = STATUS_SCANNING;
        BluetoothAdapter.getDefaultAdapter().startLeScan(mBTCallback);
        mHandler.sendEmptyMessageDelayed(WHAT_TIMEOUT_STOP_SCAN, TIMEOUT_SCAN * 1000);
    }

    public void stopScan() {
        if (mStatus == STATUS_READY) {
            Log.d(TAG, "BluetoothAdapter is already stop");
            return;
        }
        this.mScanResultListener = null;
        mStatus = STATUS_READY;
        mHandler.removeMessages(WHAT_TIMEOUT_STOP_SCAN);
        BluetoothAdapter.getDefaultAdapter().stopLeScan(mBTCallback);
    }

    public void sendWifiInfo(final BluetoothDevice bleDevice, final String SSID, final String password, final long timeStamp, SendWifiInfoListener sendMessageListener) {
        this.mSendWifiInfoListener = sendMessageListener;
        Log.d(TAG, "[sendWifiInfo] bleDevice:" + bleDevice.getName() + " SSID:" + SSID + " password:" + password);
        BleManager.getInstance().init((Application) (mContext.getApplicationContext()));
        BleManager.getInstance().connect(new BleDevice(bleDevice), new BleGattCallback() {
            @Override
            public void onStartConnect() {
                Log.d(TAG, "[onStartConnect]");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                Log.d(TAG, "[onConnectFail] exception:" + exception.getDescription());
                Message msg = Message.obtain();
                msg.what = WHAT_SEND_INFO_FAIL;
                msg.obj = "数据发送失败，请确认设备蓝牙正常";
                mHandler.sendMessage(msg);
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                Log.d(TAG, "[onConnectSuccess]");
                writeMessage(bleDevice, getSSIDAndPwd(SSID, password, timeStamp));
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                Log.d(TAG, "[onDisConnected]");
            }
        });
    }

    public void disconnectBleDevice(final BluetoothDevice bleDevice) {
        BleManager.getInstance().disconnect(new BleDevice(bleDevice));
    }

    public String getSSIDAndPwd(String ssid, String pwd, long timeStamp) {
        String userId = SharedPreferencesUtil.getUserId(mContext, "");
        String hostUrl = SharedPreferencesUtil.getHostUrl(mContext, "");
        StringBuilder builder = new StringBuilder();

        builder.append("1111(");
        builder.append(encode(hostUrl));
        builder.append('#');
        builder.append(encode(userId));
        builder.append('#');
        builder.append(encode(timeStamp + ""));
        builder.append('#');
        builder.append(encode(ssid));
        builder.append('#');
        if (!TextUtils.isEmpty(pwd)) {
            builder.append(encode(pwd));
            builder.append('#');
        }
        builder.append(")2222");
        String info = builder.toString();
        String infoSend = info.replaceAll("(.{16})", "$1DATA");
        return "DATA" + infoSend;
    }

    private String encode(String str) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            if (ch == '#') {
                builder.append('\\');
            } else if (ch == '\\') {
                builder.append('\\');
            }
            builder.append(ch);
        }
        return builder.toString();
    }

    private void writeMessage(BleDevice bleDevice, String data) {
        Log.d(TAG, "[writeMessage] bleDevice:" + bleDevice.getName() + " data:" + data);
        BleManager.getInstance().write(
                bleDevice,
                UUID_WIFI_SERVICE.toString(),
                UUID_WRITE_CHARACTERISTIC.toString(),
                data.getBytes(),
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        // 发送数据到设备成功（分包发送的情况下，可以通过方法中返回的参数可以查看发送进度）
                        Log.d(TAG, "[onWriteSuccess]");
                        Message msg = Message.obtain();
                        msg.what = WHAT_SEND_INFO_SUCCESS;
                        msg.obj = "数据发送成功";
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Log.d(TAG, "[onWriteFailure] exception:" + exception.getDescription());
                        Message msg = Message.obtain();
                        msg.what = WHAT_SEND_INFO_FAIL;
                        msg.obj = "数据发送失败，请确认设备蓝牙正常";
                        mHandler.sendMessage(msg);
                    }
                });
    }

    public void stopSendWifiInfo() {
        mSendWifiInfoListener = null;
    }
}
