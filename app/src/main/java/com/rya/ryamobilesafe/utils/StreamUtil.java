package com.rya.ryamobilesafe.utils;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class StreamUtil {

    private static final String TAG = "StreamUtil";

    /**
     * @param is     流对象
     * @return      流转换成字符串     null-异常
     */
    public static String stream2String(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = -1;

        try {
            while ((len = is.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            return baos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
