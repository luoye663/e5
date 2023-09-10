package io.qyi.e5.util.netRequest;

import okhttp3.*;

import javax.net.ssl.X509TrustManager;
import java.net.Proxy;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpClientUtil {
    private static int connTimeOut = 5 * 1000;
    private static int readTimeOut = 20 * 1000;
    private static int writeTimeOut = 10 * 1000;
    public static OkHttpClient client = null;

    @Value("${e5.system.proxy_enable}")
    private static boolean proxy_enable;

    @Value("${e5.system.proxy_host}")
    private static String proxy_host;

    @Value("${e5.system.proxy_port}")
    private static String proxy_port;

    static {
        X509TrustManager manager = SSLSocketClientUtil.getX509TrustManager();

        if (proxy_enable) {
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy_host, Integer.parseInt(proxy_port)));

            client = new OkHttpClient.Builder()
                    .connectTimeout(connTimeOut, TimeUnit.SECONDS)
                    .readTimeout(readTimeOut, TimeUnit.SECONDS)
                    .writeTimeout(writeTimeOut, TimeUnit.SECONDS)
                    .proxy(proxy) // 设置代理
                    .retryOnConnectionFailure(true)
                    .sslSocketFactory(SSLSocketClientUtil.getSocketFactory(manager), manager)
                    .hostnameVerifier(SSLSocketClientUtil.getHostnameVerifier())
                    .build();
        } else {
            client = new OkHttpClient.Builder()
                    .connectTimeout(connTimeOut, TimeUnit.SECONDS)
                    .readTimeout(readTimeOut, TimeUnit.SECONDS)
                    .writeTimeout(writeTimeOut, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .sslSocketFactory(SSLSocketClientUtil.getSocketFactory(manager), manager)// 忽略校验
                    .hostnameVerifier(SSLSocketClientUtil.getHostnameVerifier())// 忽略校验
                    .build();
        }
    }

    public static String doGet(String host, String path, Map<String, String> headers, Map<String, String> querys)
            throws Exception {
        StringBuffer url = new StringBuffer(host + (path == null ? "" : path));
        if (querys != null) {
            url.append("?");
            Iterator iterator = querys.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> e = (Map.Entry) iterator.next();
                url.append((String) e.getKey()).append("=").append((String) e.getValue() + "&");
            }
            url = new StringBuffer(url.substring(0, url.length() - 1));
        }
        Request.Builder requestBuilder = new Request.Builder();
        if (headers != null && headers.size() > 0) {
            Iterator iterator = headers.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                requestBuilder.addHeader(key, (String) headers.get(key));
            }
        }
        Request request = (requestBuilder).url(url.toString()).build();
        Response response = client.newCall(request).execute();
        String responseStr = response.body() == null ? "" : response.body().string();
        return responseStr;
    }

    public static String doPost(String url, Map<String, Object> headers, Map<String, Object> querys) throws Exception {
        FormBody.Builder formbody = new FormBody.Builder();
        if (null != querys) {
            Iterator iterator = querys.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> elem = (Map.Entry) iterator.next();
                formbody.add((String) elem.getKey(), (String) elem.getValue());
            }
        }

        RequestBody body = formbody.build();
        Request.Builder requestBuilder = (new Request.Builder()).url(url);
        if (headers != null && headers.size() > 0) {
            Iterator iteratorHeader = headers.keySet().iterator();
            while (iteratorHeader.hasNext()) {
                String key = (String) iteratorHeader.next();
                requestBuilder.addHeader(key, (String) headers.get(key));
            }
        }

        Request requet = requestBuilder.post(body).build();
        Response response = client.newCall(requet).execute();
        String responseStr = response.body() == null ? "" : response.body().string();
        return responseStr;
    }

    public static String doPost(String url, Map<String, String> headers, String sendMessage) throws Exception {

        RequestBody body = FormBody.create(MediaType.parse("application/json"), sendMessage);
        ;
        Request.Builder requestBuilder = (new Request.Builder()).url(url);
        if (headers != null && headers.size() > 0) {
            Iterator iteratorHeader = headers.keySet().iterator();
            while (iteratorHeader.hasNext()) {
                String key = (String) iteratorHeader.next();
                requestBuilder.addHeader(key, (String) headers.get(key));
            }
        }

        Request requet = requestBuilder.post(body).build();
        Response response = client.newCall(requet).execute();
        String responseStr = response.body() == null ? "" : response.body().string();
        return responseStr;
    }

    public static String doPut(String host, String path, Map<String, String> headers, Map<String, String> querys)
            throws Exception {
        FormBody.Builder builder = new FormBody.Builder();
        Iterator iterator = querys.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, String> elem = (Map.Entry) iterator.next();
            builder.add((String) elem.getKey(), (String) elem.getValue());
        }

        RequestBody body = builder.build();
        Request.Builder requestBuilder = (new Request.Builder()).url(host + path);
        if (headers != null && headers.size() > 0) {
            Iterator iteratorHeader = headers.keySet().iterator();
            while (iteratorHeader.hasNext()) {
                String key = (String) iteratorHeader.next();
                requestBuilder.addHeader(key, (String) headers.get(key));
            }
        }

        Request requet = requestBuilder.put(body).build();
        Response response = client.newCall(requet).execute();
        String responseStr = response.body() == null ? "" : response.body().string();
        return responseStr;
    }
}