package org.atline.jarupdater.utils;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {
    private static Map<String, String> headers = new HashMap<String, String>();
    static {
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.1.2)");
        headers.put("Accept-Language", "zh-cn,zh;q=0.5");
        headers.put("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
        headers.put(
                "Accept",
                " image/gif, image/x-xbitmap, image/jpeg, "
                        + "image/pjpeg, application/x-silverlight, application/vnd.ms-excel, "
                        + "application/vnd.ms-powerpoint, application/msword, application/x-shockwave-flash, */*");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Accept-Encoding", "gzip, deflate");
    }

    public static boolean downloadBinary(String url, FileOutputStream fos) {
        if ("ok".equals(httpGet(url, fos))) {
            return true;
        } else {
            return false;
        }
    }

    public static String getInfo(String url) {
        return httpGet(url, null);
    }

    public static String httpGet(String url, FileOutputStream fos) {
        DefaultHttpClient httpclient = null;
        HttpGet httpGet = null;
        HttpResponse response = null;
        HttpEntity entity = null;
        String result = "";
        StringBuffer suf = new StringBuffer();
        try {
            httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                    CookiePolicy.BROWSER_COMPATIBILITY);
            httpGet = new HttpGet(url);

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }

            HttpConnectionParams.setConnectionTimeout(httpclient.getParams(),
                    30000);
            HttpConnectionParams.setSoTimeout(httpGet.getParams(),
                    30000);
            response = httpclient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                System.out.println("Status Code: " + statusCode);
                System.out.println("Status Reason: " + response.getStatusLine().getReasonPhrase());
                return "";
            } else {
                entity = response.getEntity();
                if (null != entity) {
                    if (null == fos) {
                        byte[] bytes = EntityUtils.toByteArray(entity);
                        result = new String(bytes, "UTF-8");
                    } else {
                        InputStream fis = entity.getContent();
                        IOUtils.copy(fis, fos);
                        fos.flush();
                        result = "ok";
                    }
                } else {
                }
                return result;
            }
        } catch (Exception e) {
            return "";
        } finally {
            if (null != httpclient) {
                httpclient.getConnectionManager().shutdown();
            }
        }
    }
}
