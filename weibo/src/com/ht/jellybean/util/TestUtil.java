package com.ht.jellybean.util;

import android.content.Context;

import java.io.*;

/**
 * Created by annuo on 2015/6/7.
 */
public class TestUtil {
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

}
