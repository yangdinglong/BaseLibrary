package com.roobo.baselibiray.utils;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class MD5Util {
    private static MD5Util INSTANCE = new MD5Util();

    private MD5Util() {

    }

    public synchronized static MD5Util getInstance() {
        return INSTANCE;
    }

    public static String md5(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return getInstance().getStringHash(str);
    }

    public String getStringHash(String source) {

        String hash = null;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(source.getBytes());
            hash = getStreamHash(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hash;
    }

    public String getFileHash(String file) {

        String hash = null;
        try {
            FileInputStream in = new FileInputStream(file);
            hash = getStreamHash(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hash;
    }

    public String getStreamHash(InputStream stream) {

        String hash = null;
        byte[] buffer = new byte[1024];
        BufferedInputStream in = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            in = new BufferedInputStream(stream);
            int numRead = 0;
            while ((numRead = in.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            in.close();
            hash = toHexString(md5.digest());
        } catch (Exception e) {
            if (in != null)
                try {
                    in.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            e.printStackTrace();
        }
        return hash;
    }

    private String toHexString(byte[] b) {

        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
            sb.append(hexChar[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    private char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static void main(String[] args) {

    }
}