package com.vovo.handler;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Auther: liuzeheng@zhihu.com
 * @Date: 2024/11/18
 * @Description:
 */
public class CommonUtil {
    // 将大字符串保存到文件
    public static String saveLargeDataToFile(Context context, String data, String fileName) {
        File file = new File(context.getFilesDir(), "large_data.txt");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data.getBytes());
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static  String readFileContent(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        FileInputStream fis = null;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            fis = new FileInputStream(file);
            int character;
            while ((character = fis.read()) != -1) {
                stringBuilder.append((char) character);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }
}
