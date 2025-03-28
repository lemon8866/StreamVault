package com.flower.spirit.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 这个方法中有大量遗弃方法不再调用   
 */
@SuppressWarnings("deprecation")
public class HttpUtil {

    /**
     * web 端
     * 
     * @param url
     * @param param
     * @return
     */
    public static String httpGet(String url, String param) {
        HttpClient httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        GetMethod getMethod = new GetMethod(url);
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
        getMethod.getParams().setParameter("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        String response = "";
        try {
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("请求出错: " + getMethod.getStatusLine());
            }
            byte[] responseBody = getMethod.getResponseBody();
            response = new String(responseBody, param);
        } catch (HttpException e) {
            System.out.println("请检查输入的URL!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("发生网络异常!");
            e.printStackTrace();
        } finally {
            /* 6 .释放连接 */
            getMethod.releaseConnection();
        }
        return response;
    }

    /**
     * 用于验证bili登录接口 并取出cookie
     * 
     * @param url
     * @param param
     * @return
     */
    public static String httpGetBypoll(String url, String param) {
        HttpClient httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        GetMethod getMethod = new GetMethod(url);
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
        getMethod.getParams().setParameter("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        String response = "";
        try {
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            byte[] responseBody = getMethod.getResponseBody();
            response = new String(responseBody, param);
            String code = JSONObject.parseObject(response).getJSONObject("data").getString("code");
            if (code.equals("0")) {
                // 登录成功
                Header[] headers = getMethod.getResponseHeaders();
                String cookie = "";
                for (Header h : headers) {
                    if (h.getName().equals("Set-Cookie")) {
                        String str = h.getValue().split(";")[0];
                        cookie = cookie + ";" + str;
                    }
                }
                return cookie.substring(1, cookie.length());
            } else {
                return null;
            }

        } catch (HttpException e) {
            System.out.println("请检查输入的URL!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("发生网络异常!");
            e.printStackTrace();
        } finally {
            /* 6 .释放连接 */
            getMethod.releaseConnection();
        }
        return null;
    }

    /**
     * json 字符串
     * 
     * @param url
     * @param param
     * @return
     */
    public static String getSerchPersion(String url, String param) {
        HttpClient httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        GetMethod getMethod = new GetMethod(url);
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
        getMethod.getParams().setParameter("user-agent",
                "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Mobile Safari/537.36");
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        String response = "";
        try {
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("请求出错: " + getMethod.getStatusLine());
            }
            byte[] responseBody = getMethod.getResponseBody();
            response = new String(responseBody, param);
        } catch (HttpException e) {
            System.out.println("请检查输入的URL!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("发生网络异常!");
            e.printStackTrace();
        } finally {
            /* 6 .释放连接 */
            getMethod.releaseConnection();
        }
        return response;
    }

    public static String httpGetBili(String url, String param, String cookie) {
        HttpClient httpClient = new HttpClient();

        httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        GetMethod getMethod = new GetMethod(url);

        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
        getMethod.getParams().setParameter("user-agent",
                "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Mobile Safari/537.36");
        getMethod.addRequestHeader("user-agent",
                "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Mobile Safari/537.36");
        if (null != cookie && !cookie.equals("")) {
            getMethod.addRequestHeader("cookie", cookie);
        }

        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        String response = "";
        try {
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("请求出错: " + getMethod.getStatusLine());
            }
            byte[] responseBody = getMethod.getResponseBody();
            response = new String(responseBody, param);
        } catch (HttpException e) {
            System.out.println("请检查输入的URL!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("发生网络异常!");
            e.printStackTrace();
        } finally {
            /* 6 .释放连接 */
            getMethod.releaseConnection();
        }
        return response;
    }

    /**
     * post请求
     * 
     * @param url
     * @param json
     * @return
     */
    public static JSONObject doPost(String url, JSONObject json) {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        JSONObject response = null;
        try {
            StringEntity s = new StringEntity(json.toString());
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json;charset=UTF-8");// 发送json数据需要设置contentType
            post.setEntity(s);
            HttpResponse res = client.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(res.getEntity());// 返回json格式：
                result = new String(result.getBytes("ISO-8859-1"), "utf-8");
                response = JSONObject.parseObject(result);
            }
        } catch (Exception e) {
            client.close();
        }
        return response;
    }

    public static JSONObject doPostNew(String url, JSONObject json) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.addHeader("Content-Type", "application/json");
        JSONObject response = null;
        try {
            post.setEntity(new StringEntity(json.toString(), "UTF-8"));
            HttpResponse res = httpClient.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(res.getEntity());
                result = new String(result.getBytes("ISO-8859-1"), "utf-8");
                response = JSONObject.parseObject(result);
            }
        } catch (Exception e) {

        } finally {
            try {
                httpClient.close();
            } catch (Exception e2) {
            }
        }
        return response;
    }

    public static void downBiliFromUrl(String urlStr, String fileName, String savePath) throws Exception {
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            // 创建保存目录
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                FileUtils.createDirectory(savePath);
            }

            // 创建文件
            File file = new File(saveDir + File.separator + fileName);

            // 建立连接
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 BiliDroid/7.25.0 (bbcallen@gmail.com)");
            conn.setRequestProperty("referer", "https://www.bilibili.com");
            // 获取文件大小
            int fileSize = conn.getContentLength();
            // 打开输入流
            inputStream = conn.getInputStream();
            outputStream = new FileOutputStream(file);
            // 使用缓冲流复制数据
            byte[] buffer = new byte[8192]; // 8KB 缓冲区
            int bytesRead;
            long totalRead = 0;
            long lastLogTime = System.currentTimeMillis();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
                // 每秒记录一次下载进度
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastLogTime > 1000) {
                    if (fileSize > 0) {
                        int progress = (int) (totalRead * 100 / fileSize);
                        // 记录下载进度，但不频繁输出日志
                        if (progress % 10 == 0) {
                            // 计算下载速度
                            double speed = (totalRead / 1024.0) / ((currentTime - conn.getConnectTimeout()) / 1000.0);
                            // 使用SLF4J记录信息
                            if (LoggerFactory.getLogger(HttpUtil.class).isDebugEnabled()) {
                                LoggerFactory.getLogger(HttpUtil.class).debug(
                                        "下载进度: {}%, 速度: {:.2f} KB/s, 文件: {}", progress, speed, fileName);
                            }
                        }
                    }
                    lastLogTime = currentTime;
                }
            }
            // 确保数据全部写入
            outputStream.flush();
        } finally {
            // 关闭资源，使用try-with-resources简化
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                // 仅记录关闭时的错误
                if (LoggerFactory.getLogger(HttpUtil.class).isDebugEnabled()) {
                    LoggerFactory.getLogger(HttpUtil.class).debug("关闭资源时出错: {}", e.getMessage());
                }
            }
        }
    }

    @SuppressWarnings("unused")
    private static class InputStreamWithProgress extends FilterInputStream {
        private long totalBytes;
        private long bytesRead;

        protected InputStreamWithProgress(InputStream in, long totalBytes) {
            super(in);
            this.totalBytes = totalBytes;
            this.bytesRead = 0;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int bytesRead = super.read(b, off, len);
            if (bytesRead != -1) {
                this.bytesRead += bytesRead;
            }
            return bytesRead;
        }

        public int getProgress() {
            if (totalBytes > 0) {
                return (int) ((bytesRead * 100) / totalBytes);
            } else {
                return 0;
            }
        }
    }

    public static void downDouFromUrl(String urlStr, String fileName, String savePath, String cookie) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestProperty("User-Agent", DouUtil.ua);
            if (cookie != null) {
                conn.setRequestProperty("cookie", cookie);
            }
            InputStream input = conn.getInputStream();
            byte[] getData = readInputStream(input);
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                FileUtils.createDirectory(savePath);
            }
            File file = new File(saveDir + File.separator + fileName);
            FileOutputStream output = new FileOutputStream(file);
            output.write(getData);
            if (output != null) {
                output.close();
            }
            if (input != null) {
                input.close();
            }
        } catch (Exception e) {

        }
    }

    public static void downLoadFromUrl(String urlStr, String fileName, String savePath) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Linux; Android 10; SM-G981B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.162 Mobile Safari/537.36");
            InputStream input = conn.getInputStream();
            byte[] getData = readInputStream(input);
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                FileUtils.createDirectory(savePath);
            }
            File file = new File(saveDir + File.separator + fileName);
            FileOutputStream output = new FileOutputStream(file);
            output.write(getData);
            if (output != null) {
                output.close();
            }
            if (input != null) {
                input.close();
            }
        } catch (Exception e) {

        }
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[10240];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
    
    
    public static void downloadFile(String urlStr, String fileName, String savePath, Map<String, String> headers) throws IOException {
        HttpURLConnection conn = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        File saveDir = new File(savePath);
        File file = null;

        try {
            // 创建保存目录
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            file = new File(saveDir, fileName);

            // 建立连接
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);

            // 设置请求头
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            // 获取文件大小
            int fileLength = conn.getContentLength();
            
            bis = new BufferedInputStream(conn.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(file));

            byte[] buffer = new byte[8192];
            int len;
            long downloaded = 0;
            long lastProgressTime = System.currentTimeMillis();
            long startTime = System.currentTimeMillis();

            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
                downloaded += len;

                // 每500ms更新一次进度
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastProgressTime >= 15000) {
                    // 计算进度
                    int progress = (int) (downloaded * 100 / fileLength);
                    // 计算速度 (KB/s)
                    double speed = (downloaded / 1024.0) / ((currentTime - startTime) / 1000.0);
                    // 计算剩余时间（秒）
                    long remainingTime = (long) ((fileLength - downloaded) / (speed * 1024));
                    
                    LoggerFactory.getLogger(HttpUtil.class).info(
                        String.format("下载进度: %d%%, 速度: %.2f KB/s, 预计剩余时间: %d秒, 文件: %s", 
                        progress, speed, remainingTime, fileName)
                    );
                    
                    lastProgressTime = currentTime;
                }
            }
            // 确保数据写入
            bos.flush();
        } finally {
            // 关闭资源
            try {
                if (bos != null) bos.close();
                if (bis != null) bis.close();
                if (conn != null) conn.disconnect();
            } catch (IOException e) {
                LoggerFactory.getLogger(HttpUtil.class).error("关闭资源时出错: " + e.getMessage());
            }
        }
    }

}
