package io.qyi.e5.util.netRequest;

import com.google.common.collect.Maps;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * 封装请求
 *
 * @author weiximei
 */
public class OkHttpRequestUtils {

    /**
     * 定义请求客户端
     */
    private static OkHttpClient client = new OkHttpClient();

    /**
     * get 请求
     *
     * @param url 请求URL
     * @return
     * @throws Exception
     */
    public static String doGet(String url) throws Exception {
        return doGet(url, Maps.newHashMap());
    }


    /**
     * get 请求
     *
     * @param url   请求URL
     * @param query 携带参数参数
     * @return
     * @throws Exception
     */
    public static String doGet(String url, Map<String, Object> query) throws Exception {

        return doGet(url, Maps.newHashMap(), query);
    }

    /**
     * get 请求
     *
     * @param url    url
     * @param header 请求头参数
     * @param query  参数
     * @return
     */
    public static String doGet(String url, Map<String, Object> header, Map<String, Object> query) throws Exception {

        // 创建一个请求 Builder
        Request.Builder builder = new Request.Builder();
        // 创建一个 request
        Request request = builder.url(url).build();

        // 创建一个 HttpUrl.Builder
        HttpUrl.Builder urlBuilder = request.url().newBuilder();
        // 创建一个 Headers.Builder
        Headers.Builder headerBuilder = request.headers().newBuilder();

        // 装载请求头参数
        if (header != null) {
            Iterator<Map.Entry<String, Object>> headerIterator = header.entrySet().iterator();
            headerIterator.forEachRemaining(e -> {
                headerBuilder.add(e.getKey(), (String) e.getValue());
            });
        }

        if (query != null) {
            // 装载请求的参数
            Iterator<Map.Entry<String, Object>> queryIterator = query.entrySet().iterator();
            queryIterator.forEachRemaining(e -> urlBuilder.addQueryParameter(e.getKey(), (String) e.getValue()));
        }
        // 设置自定义的 builder
        // 因为 get 请求的参数，是在 URL 后面追加  http://xxxx:8080/user?name=xxxx?sex=1
        builder.url(urlBuilder.build()).headers(headerBuilder.build());

        try (Response execute = client.newCall(builder.build()).execute()) {
            return execute.body().string();
        }
    }

    /**
     * post 请求， 请求参数 并且 携带文件上传
     *
     * @param url
     * @param header
     * @param parameter
     * @param file         文件
     * @param fileFormName 远程接口接收 file 的参数
     * @return
     * @throws Exception
     */
    public static String doPost(String url, Map<String, Object> header, Map<String, Object> parameter, File file, String fileFormName) throws Exception {

        // 创建一个请求 Builder
        Request.Builder builder = new Request.Builder();
        // 创建一个 request
        Request request = builder.url(url).build();

        // 创建一个 Headers.Builder
        Headers.Builder headerBuilder = request.headers().newBuilder();

        // 装载请求头参数
        Iterator<Map.Entry<String, Object>> headerIterator = header.entrySet().iterator();
        headerIterator.forEachRemaining(e -> headerBuilder.add(e.getKey(), (String) e.getValue()));

        // 或者 FormBody.create 方式，只适用于接口只接收文件流的情况
        // RequestBody requestBody = FormBody.create(MediaType.parse("application/octet-stream"), file);
        MultipartBody.Builder requestBuilder = new MultipartBody.Builder();

        // 状态请求参数
        Iterator<Map.Entry<String, Object>> queryIterator = parameter.entrySet().iterator();
        queryIterator.forEachRemaining(e -> requestBuilder.addFormDataPart(e.getKey(), (String) e.getValue()));

        if (null != file) {
            // application/octet-stream
            requestBuilder.addFormDataPart(StringUtils.isNotBlank(fileFormName) ? fileFormName : "file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
        }

        // 设置自定义的 builder
        builder.headers(headerBuilder.build()).post(requestBuilder.build());

        // 然后再 build 一下
        try (Response execute = client.newCall(builder.build()).execute()) {
            return execute.body().string();
        }
    }
    /**
     * post 请求， 请求参数
     *
     * @param url
     * @param header
     * @param parameter
     * @return
     * @throws Exception
     */
    public static String doPostFrom(String url, Map<String, Object> header, Map<String, Object> parameter) throws Exception {

        // 创建一个请求 Builder
        Request.Builder builder = new Request.Builder();
        // 创建一个 request
        Request request = builder.url(url).build();

        // 创建一个 Headers.Builder
        Headers.Builder headerBuilder = request.headers().newBuilder();

        // 装载请求头参数
        Iterator<Map.Entry<String, Object>> headerIterator = header.entrySet().iterator();
        headerIterator.forEachRemaining(e -> headerBuilder.add(e.getKey(), (String) e.getValue()));

        // 或者 FormBody.create 方式，只适用于接口只接收文件流的情况
        // RequestBody requestBody = FormBody.create(MediaType.parse("application/octet-stream"), file);
        FormBody.Builder FormBody = new FormBody.Builder();

        // 状态请求参数
        Iterator<Map.Entry<String, Object>> queryIterator = parameter.entrySet().iterator();
        queryIterator.forEachRemaining(e -> FormBody.add(e.getKey(), (String) e.getValue()));


        // 设置自定义的 builder
        builder.headers(headerBuilder.build()).post(FormBody.build());

        // 然后再 build 一下
        try (Response execute = client.newCall(builder.build()).execute()) {
            return execute.body().string();
        }
    }

    /**
     * post 请求， 请求参数 并且 携带文件上传二进制流
     *
     * @param url
     * @param header
     * @param parameter
     * @param fileName     文件名
     * @param fileByte     文件的二进制流
     * @param fileFormName 远程接口接收 file 的参数
     * @return
     * @throws Exception
     */
    public static String doPost(String url, Map<String, Object> header, Map<String, Object> parameter, String fileName, byte[] fileByte, String fileFormName) throws Exception {
        // 创建一个请求 Builder
        Request.Builder builder = new Request.Builder();
        // 创建一个 request
        Request request = builder.url(url).build();

        // 创建一个 Headers.Builder
        Headers.Builder headerBuilder = request.headers().newBuilder();

        // 装载请求头参数
        Iterator<Map.Entry<String, Object>> headerIterator = header.entrySet().iterator();
        headerIterator.forEachRemaining(e -> {
            headerBuilder.add(e.getKey(), (String) e.getValue());
        });

        MultipartBody.Builder requestBuilder = new MultipartBody.Builder();

        // 状态请求参数
        Iterator<Map.Entry<String, Object>> queryIterator = parameter.entrySet().iterator();
        queryIterator.forEachRemaining(e -> {
            requestBuilder.addFormDataPart(e.getKey(), (String) e.getValue());
        });

        if (fileByte.length > 0) {
            // application/octet-stream
            requestBuilder.addFormDataPart(StringUtils.isNotBlank(fileFormName) ? fileFormName : "file", fileName, RequestBody.create(MediaType.parse("application/octet-stream"), fileByte));
        }

        // 设置自定义的 builder
        builder.headers(headerBuilder.build()).post(requestBuilder.build());

        try (Response execute = client.newCall(builder.build()).execute()) {
            return execute.body().string();
        }
    }


    /**
     * post 请求  携带文件上传
     *
     * @param url
     * @param file
     * @return
     * @throws Exception
     */
    public static String doPost(String url, File file, String fileFormName) throws Exception {
        return doPost(url, Maps.newHashMap(), Maps.newHashMap(), file, fileFormName);
    }

    /**
     * post 请求
     *
     * @param url
     * @param header    请求头
     * @param parameter 参数
     * @return
     * @throws Exception
     */
    public static String doPost(String url, Map<String, Object> header, Map<String, Object> parameter) throws Exception {
        return doPostFrom(url, header, parameter);
    }

    /**
     * post 请求
     *
     * @param url
     * @param parameter 参数
     * @return
     * @throws Exception
     */
    public static String doPost(String url, Map<String, Object> parameter) throws Exception {
        return doPost(url, Maps.newHashMap(), parameter, null, null);
    }

    /**
     * JSON数据格式请求
     *
     * @param url
     * @param header
     * @param json
     * @return
     */
    private static String json(String url, Map<String, Object> header, String json) throws IOException {
        // 创建一个请求 Builder
        Request.Builder builder = new Request.Builder();
        // 创建一个 request
        Request request = builder.url(url).build();

        // 创建一个 Headers.Builder
        Headers.Builder headerBuilder = request.headers().newBuilder();

        // 装载请求头参数
        Iterator<Map.Entry<String, Object>> headerIterator = header.entrySet().iterator();
        headerIterator.forEachRemaining(e -> {
            headerBuilder.add(e.getKey(), (String) e.getValue());
        });

        // application/octet-stream
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), json);

        // 设置自定义的 builder
        builder.headers(headerBuilder.build()).post(requestBody);

        try (Response execute = client.newCall(builder.build()).execute()) {
            return execute.body().string();
        }
    }

    /**
     * post请求  参数JSON格式
     *
     * @param url
     * @param header 请求头
     * @param json   JSON数据
     * @return
     * @throws IOException
     */
    public static String doPost(String url, Map<String, Object> header, String json) throws IOException {
        return json(url, header, json);
    }

    /**
     * post请求  参数JSON格式
     *
     * @param url
     * @param json JSON数据
     * @return
     * @throws IOException
     */
    public static String doPost(String url, String json) throws IOException {
        return json(url, Maps.newHashMap(), json);
    }
}
