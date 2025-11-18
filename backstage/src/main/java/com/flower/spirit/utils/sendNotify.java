package com.flower.spirit.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flower.spirit.config.Global;
import com.flower.spirit.entity.NotifyConfigEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * 发送通知类
 */
public class sendNotify {
    private static final Logger logger = LoggerFactory.getLogger(sendNotify.class);
    private static final RestTemplate restTemplate = new RestTemplate();

    // 通知渠道常量
    public static final String CHANNEL_QYWX = "qywx"; // 企业微信
    public static final String CHANNEL_DINGDING = "dingding"; // 钉钉
    public static final String CHANNEL_FEISHU = "feishu"; // 飞书
    public static final String CHANNEL_SCT = "sct"; // Server酱
    
    
    /**
     * 异步发送模版类型通知
     */
    public static void sendMessage(String title, String p) {
    	if(Global.notify!= null) {
            CompletableFuture.runAsync(() -> {
                try {
                    String contents = p+"\n"+"发生时间:"+DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
                    sendNotifyByChannel(title, contents, Global.notify);
                } catch (Exception e) {
                    logger.error("[异步通知] 发送失败: {}", e.getMessage());
                }
            });
    	}

    }
    
    
    /**
     * 异步发送通知
     */
    public static void sendMessage(int count, String p) {
    	if(Global.notify!= null) {
            CompletableFuture.runAsync(() -> {
                try {
                    String title = "StreamVault任务完成通知";
                    String contents = "任务"+p+"完成通知"+"\n"+"本次完成任务数为:"+count+"\n"+"完成时间:"+DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
                    sendNotifyByChannel(title, contents, Global.notify);
                } catch (Exception e) {
                    logger.error("[异步通知] 发送失败: {}", e.getMessage());
                }
            });
    	}

    }
    
    /**
     * 异步发送通知
     */
    public static void sendNotifyError(String link, String p,String message) {
    	if(Global.notify!= null) {
            CompletableFuture.runAsync(() -> {
                try {
                    String title = "StreamVault任务异常";
                    String contents = "来源地址:" + link + "\n" + "归属平台:" + p+ "\n"+"异常原因:"+ message;
                    sendNotifyByChannel(title, contents, Global.notify);
                } catch (Exception e) {
                    logger.error("[异步通知] 发送失败: {}", e.getMessage());
                }
            });
    	}

    }

    /**
     * 异步发送通知
     */
    public static void sendNotifyData(String content, String link, String p) {
    	if(Global.notify!= null) {
            CompletableFuture.runAsync(() -> {
                try {
                    String title = "StreamVault任务完成通知";
                    String contents = "视频:" + content + "\n" + "来源地址:" + link + "\n" + "归属平台:" + p;
                    sendNotifyByChannel(title, contents, Global.notify);
                } catch (Exception e) {
                    logger.error("[异步通知] 发送失败: {}", e.getMessage());
                }
            });
    	}

    }

    /**
     * 根据配置的渠道发送通知
     * 
     * @param title   通知标题
     * @param content 通知内容
     * @param config  通知配置
     * @return 是否发送成功
     */
    public static boolean sendNotifyByChannel(String title, String content, NotifyConfigEntity config) {
        if (config == null || config.getNotifychannel() == null || config.getNotifychannel().isEmpty()) {
            logger.error("[通知发送] 未配置通知渠道");
            return false;
        }

        try {
            switch (config.getNotifychannel().toLowerCase()) {
                case CHANNEL_QYWX:
                    return sendQywxNotify(title, content, config);
                case CHANNEL_DINGDING:
                    return sendDingdingNotify(title, content, config);
                case CHANNEL_FEISHU:
                    return sendFeishuNotify(title, content, config);
                case CHANNEL_SCT:
                    return sendSctNotify(title, content, config);
                default:
                    logger.error("[通知发送] 不支持的通知渠道: {}", config.getNotifychannel());
                    return false;
            }
        } catch (Exception e) {
            logger.error("[通知发送] 发送失败: {}", e.getMessage());
            return false;
        }
    }

    private static boolean sendSctNotify(String title, String content, NotifyConfigEntity config) {
    	 HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    	 String url = "https://sctapi.ftqq.com/"+config.getSctkey()+".send";
    	 MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
         params.add("title", title);
         params.add("desp", content);
         HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
         try {
             String response = restTemplate.postForObject(url, request, String.class);
             logger.error("[通知发送] 发送成功: {}", response);
         } catch (Exception e) {
             e.printStackTrace();
         }
		return false;
	}


	/**
     * 企业微信通知
     */
    private static boolean sendQywxNotify(String title, String content, NotifyConfigEntity config) {
        if (config.getQywxkey() == null || config.getQywxkey().isEmpty()) {
            logger.warn("[企业微信通知] 未配置企业微信key");
            return false;
        }

        try {
            String url = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=" + config.getQywxkey();

            Map<String, Object> body = new HashMap<>();
            Map<String, Object> text = new HashMap<>();
            text.put("content", title + "\n" + content);
            body.put("msgtype", "text");
            body.put("text", text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            String response = restTemplate.postForObject(url, request, String.class);

            logger.info("[企业微信通知] 发送结果: {}", response);
            return true;
        } catch (Exception e) {
            logger.error("[企业微信通知] 发送失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 钉钉通知
     */
    private static boolean sendDingdingNotify(String title, String content, NotifyConfigEntity config) {
        if (config.getDingdingKey() == null || config.getDingdingKey().isEmpty()) {
            logger.warn("[钉钉通知] 未配置钉钉key");
            return false;
        }

        try {
            String baseUrl = "https://oapi.dingtalk.com/robot/send?access_token=" + config.getDingdingKey();

            // 如果配置了安全密钥，添加签名
            if (config.getDingdingSecret() != null && !config.getDingdingSecret().isEmpty()) {
                long timestamp = Instant.now().toEpochMilli();
                String stringToSign = timestamp + "\n" + config.getDingdingSecret();
                Mac mac = Mac.getInstance("HmacSHA256");
                mac.init(new SecretKeySpec(config.getDingdingSecret().getBytes("UTF-8"), "HmacSHA256"));
                byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
                String sign = Base64.getEncoder().encodeToString(signData);
                baseUrl += "&timestamp=" + timestamp + "&sign=" + sign;
            }

            Map<String, Object> markdown = new HashMap<>();
            markdown.put("title", title);
            markdown.put("content", "### " + title + "\n" + content);

            Map<String, Object> body = new HashMap<>();
            body.put("msgtype", "text");
            body.put("text", markdown);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            String response = restTemplate.postForObject(baseUrl, request, String.class);

            logger.info("[钉钉通知] 发送结果: {}", response);
            return true;
        } catch (Exception e) {
            logger.error("[钉钉通知] 发送失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 飞书通知
     */
    private static boolean sendFeishuNotify(String title, String content, NotifyConfigEntity config) {
        if (config.getFeishuKey() == null || config.getFeishuKey().isEmpty()) {
            logger.warn("[飞书通知] 未配置飞书key");
            return false;
        }

        try {
            String url = "https://open.feishu.cn/open-apis/bot/v2/hook/" + config.getFeishuKey();
            Map<String, Object> body = new HashMap<>();
            Map<String, Object> text = new HashMap<>();
            text.put("text", title + "\n" + content);
            body.put("msg_type", "text");
            body.put("content", text);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            String response = restTemplate.postForObject(url, request, String.class);

            logger.info("[飞书通知] 发送结果: {}", response);
            return true;
        } catch (Exception e) {
            logger.error("[飞书通知] 发送失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 构建通知内容
     */
    public static String buildContent(String content, String author) {
        if (author != null && !author.isEmpty()) {
            return content + "\n\n" + author;
        }
        return content;
    }

    public static void main(String[] args) {
        sendNotifyData("biaoti", "链接", "渠道");
    }
}
