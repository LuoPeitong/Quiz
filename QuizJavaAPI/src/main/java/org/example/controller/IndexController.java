package org.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class IndexController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String phone, @RequestParam String code, HttpSession session) {
        // 1️⃣ 从 session 取出验证码
        String sessionCode = (String) session.getAttribute("sms_code_" + phone);

        if (sessionCode == null) {
            return ResponseEntity.status(400).body("验证码已过期，请重新获取！");
        }

        if (!sessionCode.equals(code)) {
            return ResponseEntity.status(400).body("验证码错误！");
        }

        // 2️⃣ 验证通过，保存登录状态到 session
        session.setAttribute("login_user_phone", phone);
        session.setMaxInactiveInterval(60 * 60); // 登录有效期 1 小时

        // 3️⃣ 清除验证码，避免重复使用
        session.removeAttribute("sms_code_" + phone);

        // 4️⃣ 返回登录成功
        return ResponseEntity.ok("登录成功！");
    }
}
