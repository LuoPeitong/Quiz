package org.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@CrossOrigin
@RestController
public class SmsController {

    // 短信平台配置
    private final String SMS_URL = "http://118.190.26.130:9001/smsSend.do";
    private final String USERNAME = "690864";
    private final String PLAIN_PASSWORD = "mqzsyp";

    @RequestMapping("/api/sendCode")
    public ResponseEntity<?> sendCode(@RequestParam String phone, HttpSession session) {
        try {
            // 生成 4 位验证码
            String code = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));

            // 1. 计算加密 password
            String md5Pass = md5(PLAIN_PASSWORD);
            String fullPass = md5(USERNAME + md5Pass);

            // 2. 短信内容
            String content = String.format("验证码：%s，有效期10分钟。如非本人操作，请忽略。", code);
            String encodedContent = URLEncoder.encode(content, StandardCharsets.UTF_8.toString());

            // 3. 拼接请求参数
            String url = SMS_URL + "?username=" + USERNAME +
                    "&password=" + fullPass +
                    "&mobile=" + phone +
                    "&content=" + encodedContent;

            // 4. 发送 HTTP 请求
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            // 5. 将验证码保存到 session
            session.setAttribute("sms_code_" + phone, code);
            session.setMaxInactiveInterval(300); // 5分钟

            // 6. 返回结果
            return ResponseEntity.ok("短信发送成功: " + response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("短信发送失败: " + e.getMessage());
        }
    }

    // MD5 加密工具
    private String md5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        for (byte b : messageDigest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }
}
