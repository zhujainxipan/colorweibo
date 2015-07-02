package com.ht.jellybean.util;

import android.content.Context;
import java.io.*;

/**
 * Created by IntelliJ IDEA
 * Project: com.ht.mynote.utils
 * Author: 安诺爱成长
 * Email: 1399487511@qq.com
 * Date: 2015/5/2
 */

/**
 * 进行json字符串的缓存
 */

public class StringCacheUtil {

    //读取指定的名字对应的文件的内容
    public static String getContentByName(String title, Context context) {
        FileInputStream fin = null;
        InputStreamReader ir = null;
        BufferedReader br = null;
        StringBuffer sb = null;
        try {
            fin = context.openFileInput(title);
            ir = new InputStreamReader(fin);
            br = new BufferedReader(ir);
            String line = null;
            sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\r\n");
            }

            //防止取到空的数据，然后报数组越界
            if (sb.length() != 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (ir != null) {
                try {
                    ir.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    //添加一条记录
    public static void addFile(String title, String content, Context context) {
        FileOutputStream fout = null;
        OutputStreamWriter writer = null;
        PrintWriter pw = null;
        try {
            fout = context.openFileOutput(title, context.MODE_PRIVATE);
            writer = new OutputStreamWriter(fout);
            pw = new PrintWriter(writer);
            pw.print(content);
            pw.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //根据标题删除文件
    public static void delFileByName(String title, Context context) {
        File file = new File(context.getFilesDir().toString());
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (title.equals(files[i].getName())) {
                files[i].delete();
            }
        }

    }
}
