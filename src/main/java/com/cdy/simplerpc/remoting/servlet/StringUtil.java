package com.cdy.simplerpc.remoting.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * todo
 * Created by 陈东一
 * 2019/1/27 0027 14:44
 */
public class StringUtil {
    
    public static String inputStreamToString(InputStream inputStream) {
        try {
            try (ByteArrayOutputStream swapStream = new ByteArrayOutputStream();) {
                byte[] bytes = new byte[1024];
                int end = 0;
                if ((end = inputStream.read(bytes)) != -1) {
                    swapStream.write(bytes);
                }
                return swapStream.toString("UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
