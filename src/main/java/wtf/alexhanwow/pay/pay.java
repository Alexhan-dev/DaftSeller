package wtf.alexhanwow.pay;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class pay {
    // 业务类型前缀
    public enum OrderType {
        OR("普通订单"),
        SP("特殊订单"),
        EX("加急订单");

        private final String description;
        OrderType(String description) {
            this.description = description;
        }
    }

    // 码支付平台配置信息
    public static final String API_URL = "https://codepay.example.com/createOrder"; // 替换为实际API地址
    public static final String MERCHANT_ID = "YOUR_MERCHANT_ID"; // 替换为您的商户ID
    public static final String API_KEY = "YOUR_API_KEY"; // 替换为您的API密钥
    public static final String NOTIFY_URL = "https://yourdomain.com/payment/notify"; // 支付结果通知URL

    // 序列号生成器
    private static final AtomicLong sequence = new AtomicLong(0);
    private static final int MAX_SEQUENCE = 9999;

    // 日期时间格式
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HHmmss");

    // 随机数生成器
    private static final Random RANDOM = new Random();

    // 最后生成时间戳
    private static volatile long lastTimestamp = -1L;


    /**
     * 创建支付订单
     * @param amount 支付金额（元）
     * @return 支付链接的字符串
     */
    public static String createPayment(double amount) {
        try {
            // 生成唯一订单号
            String orderId = "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6);

            // 准备请求参数
            Map<String, String> params = new HashMap<>();
            params.put("merchant_id", MERCHANT_ID);
            params.put("order_id", orderId);
            params.put("amount", String.format("%.2f", amount));
            params.put("notify_url", NOTIFY_URL);

            // 生成签名
            String sign = generateSign(params);
            params.put("sign", sign);

            // 发送POST请求
            String response = sendPostRequest(API_URL, params);

            // 解析响应获取支付链接
            String payURL = parseResponse(response);
            return payURL != null ? payURL : "支付链接生成失败";
        } catch (Exception e) {
            e.printStackTrace();
            return "创建支付时发生错误";
        }
    }

    /**
     * 生成订单号
     * @param orderType 订单类型
     * @return 唯一订单号
     */
    public static synchronized String generate(OrderType orderType) {
        LocalDateTime now = LocalDateTime.now();

        // 日期部分
        String datePart = now.format(DATE_FORMAT);

        // 时间部分
        String timePart = now.format(TIME_FORMAT);

        // 毫秒部分 (3位)
        long currentMillis = System.currentTimeMillis();
        String millisPart = String.format("%03d", currentMillis % 1000);

        // 处理同一毫秒内的并发
        if (currentMillis == lastTimestamp) {
            if (sequence.getAndIncrement() > MAX_SEQUENCE) {
                // 等待到下一毫秒
                while (System.currentTimeMillis() <= currentMillis) {
                    Thread.yield();
                }
                sequence.set(0);
                return generate(orderType);
            }
        } else {
            sequence.set(0);
            lastTimestamp = currentMillis;
        }

        // 序列号部分 (4位)
        String sequencePart = String.format("%04d", sequence.getAndIncrement());

        // 随机码部分 (3位) 防止猜测
        String randomPart = String.format("%03d", RANDOM.nextInt(1000));

        // 组合所有部分
        return orderType.name() +
                datePart +
                timePart +
                millisPart +
                sequencePart +
                randomPart;
    }

    /**
     * 生成参数签名
     */
    private static String generateSign(Map<String, String> params) throws Exception {
        StringBuilder sb = new StringBuilder();
        // 按参数名排序拼接
        params.keySet().stream().sorted().forEach(key ->
                sb.append(key).append("=").append(params.get(key)).append("&")
        );
        sb.append("key=").append(API_KEY);

        // 计算MD5签名
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(sb.toString().getBytes(StandardCharsets.UTF_8));

        // 转换为十六进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }

    /**
     * 发送POST请求
     */
    private static String sendPostRequest(String urlString, Map<String, String> params) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // 构建请求体
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(param.getKey()).append('=').append(param.getValue());
        }

        // 发送请求
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = postData.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // 读取响应
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }

    /**
     * 解析API响应
     */
    private static String parseResponse(String response) {
        // 实际解析应根据API返回的JSON格式
        // 示例格式：{"code":200,"msg":"success","data":{"pay_url":"https://..."}}
        if (response.contains("\"code\":200") && response.contains("pay_url")) {
            int start = response.indexOf("pay_url") + 10;
            int end = response.indexOf("\"", start);
            return response.substring(start, end);
        }
        return null;
    }

    /**
     * 处理支付回调（由支付平台调用）
     * @param callbackParams 回调参数
     * @return 支付结果处理响应
     */
    public static String handlePaymentCallback(Map<String, String> callbackParams) {
        try {
            // 验证签名
            String receivedSign = callbackParams.remove("sign");
            String calculatedSign = generateSign(callbackParams);

            if (!calculatedSign.equals(receivedSign)) {
                return "sign_error";
            }

            // 处理支付成功逻辑
            String orderId = callbackParams.get("order_id");
            String status = callbackParams.get("status");

            if ("success".equals(status)) {
                // 更新订单状态等业务逻辑
                return "SUCCESS"; // 通知支付平台已处理
            }
            return "payment_failed";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

}
