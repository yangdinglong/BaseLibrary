package com.roobo.baselibiray.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by HP on 2019/3/8.
 */

public class FileUtils {

    public static final String TAG = FileUtils.class.getSimpleName();


    public static final String APP_NAME = "/meetpro";


    public static final String VOICE_PATH = APP_NAME + "/voice";

    public static boolean isSdcardExist() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 创建语音文件
     *
     * @return
     */
    public static String createVoiceFile(String voidFileName) {
        if (!isSdcardExist()) {
            Log.d(TAG, "[createVoiceFile]: isSdCardExist=false");
            return "";
        }
        String dirPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + VOICE_PATH;
        File dirPathFile = new File(dirPath);
        if(!dirPathFile.exists()) {
            boolean isSuccess = dirPathFile.mkdirs();
            if (!isSuccess) {
                return "";
            }
        }
        String path = dirPath + File.separator + voidFileName;//语音文件
        File mFile = new File(path);
        if (mFile.exists()) {
            return path;
        }
        try {
            mFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "[createVoiceFile]  IOException:" + e.getLocalizedMessage());
            path = "";
        }
        return path;
    }

    public static byte[] file2Bytes(File file) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(file);
            byte[] b = new byte[(int) file.length()];
            fis.read(b);
            fos = new FileOutputStream(file);
            fos.write(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
