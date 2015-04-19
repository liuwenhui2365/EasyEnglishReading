package com.wenhuiliu.EasyEnglishReading;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.*;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/4/19.
 */
public class MyHttpPost {
    private static final String TAG = MyHttpPost.class.getSimpleName();

    public static String post(String content) {
        String contentTag = null;
        org.apache.http.client.HttpClient httpClient = new DefaultHttpClient();
        org.apache.http.client.methods.HttpPost httpPost = new org.apache.http.client.methods.HttpPost("http://nactem7.mib.man.ac.uk/geniatagger/a.cgi");
        httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
        try {
            // 为httpPost设置HttpEntity对象
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("paragraph", content));
            HttpEntity entity = new UrlEncodedFormEntity(parameters);
            httpPost.setEntity(entity);
            // httpClient执行httpPost表单提交
            HttpResponse response = httpClient.execute(httpPost);
            // 得到服务器响应实体对象
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
                StringBuilder temp = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null){
//                    Log.d("文章内容",line);
                    temp.append(line);
                }
//                System.out.println(EntityUtils
//                        .toString(responseEntity, "utf-8"));
                contentTag = temp.toString();
                StringBuilder tempTag = new StringBuilder();
                Pattern expression = Pattern.compile("<table .*?>(.*?)</table>",Pattern.MULTILINE|Pattern.DOTALL);
                Matcher matcher = expression.matcher(contentTag);
                while (matcher.find()){
                    String tag = matcher.group();
                    tempTag.append(tag);
                }
//               二次过滤
                expression = Pattern.compile("<tr>.*?<td>(.*?)</td>.*?<td>(.*?)</td>.*?<td>(.*?)</td>.*?<td>(.*?)</td>.*?<td>(.*?)</td>.*?</tr>",Pattern.MULTILINE|Pattern.DOTALL);
                matcher = expression.matcher(tempTag);
//                清空上次内容
                temp.setLength(0);
                while (matcher.find()){
                    String tag = matcher.group(1);
//                    Log.d("捕获到",tag);
                    temp.append(tag+"_");
                    tag = matcher.group(3);
//                    Log.d("捕获到词性",tag);
                    temp.append(tag+" ");
                }

                contentTag = temp.toString();

//                Log.d(TAG,"表单上传成功！");
            } else {
                Log.d(TAG,"服务器无响应！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            httpClient.getConnectionManager().shutdown();
        }

        return contentTag;
    }
    /**
     * 直接通过 HTTP 协议提交数据到服务器，实现表单提交功能。
     * @param actionUrl 上传路径
     * @param params 请求参数key为参数名，value为参数值
     * @return 返回请求结果
     */
    public String post(String actionUrl, Set<Map.Entry<Object,Object>> params) {
        HttpURLConnection conn = null;
        DataOutputStream output = null;
        BufferedReader input = null;
        String lineEnd = System.getProperty("line.separator");    // The value is "\r\n" in Windows.
        try {
            URL url = new URL(actionUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(120000);
            conn.setDoInput(true);        // 允许输入
            conn.setDoOutput(true);        // 允许输出
            conn.setUseCaches(false);    // 不使用Cache
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            conn.connect();
            output = new DataOutputStream(conn.getOutputStream());

            addFormField(params, output);    // 添加表单字段内容

//            output.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);// 数据结束标志
//            output.flush();

            int code = conn.getResponseCode();
            if(code != 200) {
                throw new RuntimeException("请求‘" + actionUrl +"’失败！");
            }

            input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String oneLine;
            while((oneLine = input.readLine()) != null) {
                response.append(oneLine + lineEnd);
            }

            return response.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // 统一释放资源
            try {
                if(output != null) {
                    output.close();
                }
                if(input != null) {
                    input.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if(conn != null) {
                conn.disconnect();
            }
        }
    }

    private void addFormField(Set<Map.Entry<Object,Object>> params, DataOutputStream output) {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<Object, Object> param : params) {
            sb.append("Content-Disposition: form-data; name=\"" + param.getKey());
        }
        try {
            output.writeBytes(sb.toString());// 发送表单字段数据
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
