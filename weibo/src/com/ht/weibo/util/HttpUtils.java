package com.ht.weibo.util;


/*
 * ʱ�䣺150414
 * ���ܣ�����httpЭ��get�������ӷ������Ĺ�����д�����ر�ע����������쳣�Ĵ���ʽ����Ҫ��
 */
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
  
public class HttpUtils {
  
    /**
     * getInputStream������Ϊ�˻�ȡ��ȡ����������Դ���ֽ�������
     * @param path��url�ַ���
     * @return InputStream
     */
    public static InputStream getInputStream(String path) {
        URL url = null;
        try {
            url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // ��������ʽ
            conn.setRequestMethod("GET");
            // �������ӵĳ�ʱʱ��
            conn.setConnectTimeout(5000);
            // ���ÿ��Դӷ������˶�ȡ����
            conn.setDoInput(true);
            // ��ȡ�������˵���Ӧ��
            if (conn.getResponseCode() == 200) {
                // ��ö�ȡ����������Դ���ֽ�������
                InputStream in = conn.getInputStream();
                return in;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
  
  
    public static String changeToString(InputStream in, String encode) {
        // ���ݵ���������-�ڴ�(����)-�ֽ����������м���
        //������������������һ���
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = in.read(b)) != -1) {
                baos.write(b, 0, len);//��b������Ϊbaos������Դ��д��boas�ڲ��Ļ���������
            }
            //�����ڲ����ֽ�����
            byte[] arr = baos.toByteArray();
            return new String(arr, encode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
  
}
