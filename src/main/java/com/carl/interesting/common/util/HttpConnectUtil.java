package com.carl.interesting.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.ws.rs.core.MediaType;

import net.sf.json.JSONObject;

/**
 * communicate by HTTP protocol utility
 * 
 * @author Carl Liu
 * @version [version, 29 Mar 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class HttpConnectUtil {
    private static final String DEFAULT_CHARSET = "UTF-8";
    
    private static final String DEFAULT_METHOD = "POST";
    
    /**
     * HTTP connect timeout is 5 second
     */
    private static final int CONNECT_TIME_OUT = 5000;
    
    /**
     * HTTP read timeout is 10 mins
     */
    private static final int READ_TIME_OUT = 10 * 60 * 1000;
    
    /**
     * 用于和其他服务器端进行信息交互方法
     * 
     * @param url 其他服务器的链接
     * @param params 传递的参数
     * @param params [String signature & String customer]
     * @return
     * @throws IOException [explain parameter]
     * @return String <pre>
     * 正确：[{"result":"true","activatedTime":"typedef long"}
     * 错误：{"result":false, "error":{"code": , "message": }}]
     * </pre>
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static String sendRequest(String url, JSONObject params,
            String method, String charset, String contentType)
            throws IOException {
        String res = "{}";
        URL localURL;
        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;
        try {
            localURL = new URL(url);
            URLConnection connection = localURL.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            httpURLConnection.setConnectTimeout(CONNECT_TIME_OUT);
            httpURLConnection.setReadTimeout(READ_TIME_OUT);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setRequestProperty("Accept-Charset", charset);
            httpURLConnection.setRequestProperty("Content-Type", contentType);
            httpURLConnection.setRequestProperty("Content-Length",
                    params.toString().getBytes().length + "");
            outputStream = httpURLConnection.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream, charset);
            outputStreamWriter.write(params.toString());
            outputStreamWriter.flush();
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, charset);
            reader = new BufferedReader(inputStreamReader);
            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
            res = resultBuffer.toString();
        }
        finally {
            if (reader != null) {
                reader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
        return res;
    }
    
    /**
     * HTTP request ,mediaType=application/json
     * 
     * @param url
     * @param params
     * @param method
     * @param charset
     * @return
     * @throws IOException [explain parameter]
     * @return String [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static String sendRequest(String url, JSONObject params,
            String method, String charset) throws IOException {
        return sendRequest(url,
                params,
                method,
                charset,
                MediaType.APPLICATION_JSON);
    }
    
    /**
     * HTTP request ,charset=UTF-8,mediaType=application/json
     * 
     * @param url
     * @param params
     * @param method
     * @return
     * @throws IOException [explain parameter]
     * @return String [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static String sendRequest(String url, JSONObject params,
            String method) throws IOException {
        return sendRequest(url, params, method, DEFAULT_CHARSET);
    }
    
    /**
     * HTTP request ,method=POST,charset=UTF-8,mediaType=application/json
     * 
     * @param url
     * @param params
     * @return
     * @throws IOException [explain parameter]
     * @return String [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static String sendRequest(String url, JSONObject params)
            throws IOException {
        return sendRequest(url, params, DEFAULT_METHOD);
    }
    // /**
    // * HTTP 特殊字符转换
    // */
    // private static String httpWildcardEncode(String txt) {
    // if (txt != null) {
    // txt = StringUtils.replace(txt, "%", "%25");
    // txt = StringUtils.replace(txt, "+", "%2B");
    // txt = StringUtils.replace(txt, " ", "%20");
    // txt = StringUtils.replace(txt, "/", "%2F");
    // txt = StringUtils.replace(txt, "?", "%3F");
    // txt = StringUtils.replace(txt, "#", "%23");
    // txt = StringUtils.replace(txt, "&", "%26");
    // txt = StringUtils.replace(txt, "=", "%3D");
    // }
    // return txt;
    // }
}
