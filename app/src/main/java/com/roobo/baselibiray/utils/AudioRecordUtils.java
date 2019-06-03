package com.roobo.baselibiray.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class AudioRecordUtils {

    private final String TAG = AudioRecordUtils.class.getSimpleName();

    private static final int DEFAULT_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int DEFAULT_SAMPLE_RATE = 16000;
    private static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private Thread mCaptureThread;
    private boolean mIsCaptureStarted = false;
    private volatile boolean mIsLoopExit = false;
    private int mMinBufferSize;
    private AudioRecord mAudioRecord;

    private RecordListener mRecordListener;

    private String mVoicePath;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public interface RecordListener {
        void onRecordSuccess(String filePath);

        void onRecordFail(String message);
    }


    public AudioRecordUtils() {
    }

    public boolean isCaptureStarted() {
        return mIsCaptureStarted;
    }


    public void startRecord(RecordListener mRecordListener, String voiceName) {
        Log.d(TAG, "[startRecord]");
        if (mIsCaptureStarted) {
            Log.e(TAG, "[startRecord] AudioRecordUtils Capture already started !");
            return;
        }

        this.mRecordListener = mRecordListener;
        if (TextUtils.isEmpty(voiceName)) {
            Log.d(TAG, "[startRecord] voiceName is null");
            return;
        }
        if (!FileUtils.isSdcardExist()) {
            Log.d(TAG, "[startRecord] sdcard not exist");
            if (mRecordListener != null) {
                mRecordListener.onRecordFail("sdcard not exist");
            }
            return;
        }
        this.mVoicePath = FileUtils.createVoiceFile(voiceName);

        mMinBufferSize = AudioRecord.getMinBufferSize(DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT);
        if (mMinBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(TAG, "[startRecord] Invalid parameter !");
            return;
        }
        Log.d(TAG, "[startRecord] getMinBufferSize = " + mMinBufferSize + " bytes !");

        mAudioRecord = new AudioRecord(DEFAULT_SOURCE, DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT, mMinBufferSize * 2);
        if (mAudioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(TAG, "[startRecord] Failed to create audio record. 录音失败，请确认是否有权限或麦克风被占用");
            return;
        }

        if (mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
            Log.e(TAG, "[startRecord] AudioRecord initialize fail !");
            return;
        }

        mAudioRecord.startRecording();

        mIsLoopExit = false;
        mCaptureThread = new Thread(new AudioCaptureRunnable());
        mCaptureThread.start();
        mIsCaptureStarted = true;

        Log.d(TAG, "[startRecord] Start audio capture success !");
    }

    public void stopRecord() {
        Log.d(TAG, "[stopRecord]");
        if (!mIsCaptureStarted) {
            return;
        }

        mIsLoopExit = true;
        try {
            mCaptureThread.interrupt();
            mCaptureThread.join(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            mAudioRecord.stop();
        }

        mAudioRecord.release();
        mAudioRecord = null;
        mIsCaptureStarted = false;
        Log.d(TAG, "[stopRecord] Stop audio capture success !");
    }

    private class AudioCaptureRunnable implements Runnable {

        @Override
        public void run() {
            Log.d(TAG, "AudioRecordUtils begin recording... + mMinBufferSize=" + mMinBufferSize);

            OutputStream out = null;
            ByteArrayOutputStream baos = null;
            try {
                baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[mMinBufferSize];
                int bufferReadResult;
                while (!mIsLoopExit) {
                    bufferReadResult = mAudioRecord.read(buffer, 0,
                            mMinBufferSize);
                    if (bufferReadResult > 0) {
                        baos.write(buffer, 0, bufferReadResult);
                    }
                }
                buffer = baos.toByteArray();

                Log.d(TAG, "audio byte len=" + buffer.length);

                out = new FileOutputStream(new File(mVoicePath));
                out.write(getWavHeader(buffer.length));
                out.write(buffer);
                postSuccess();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                postFail(e.getLocalizedMessage());
            } catch (IOException e) {
                e.printStackTrace();
                postFail(e.getLocalizedMessage());
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public byte[] getWavHeader(long totalAudioLen) {
        int mChannels = 1;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = DEFAULT_SAMPLE_RATE;
        long byteRate = DEFAULT_SAMPLE_RATE * 2 * mChannels;

        byte[] header = new byte[44];
        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;  // format = 1
        header[21] = 0;
        header[22] = (byte) mChannels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * mChannels);  // block align
        header[33] = 0;
        header[34] = 16;  // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        return header;
    }

    private void postSuccess() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mRecordListener != null) {
                    mRecordListener.onRecordSuccess(mVoicePath);
                    mRecordListener = null;
                }
            }
        });
    }

    private void postFail(final String message) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mRecordListener != null) {
                    mRecordListener.onRecordFail(message);
                }
            }
        });
    }
}

