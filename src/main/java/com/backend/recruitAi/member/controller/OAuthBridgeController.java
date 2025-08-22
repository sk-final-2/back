// com.backend.recruitAi.security.OAuthBridgeController
package com.backend.recruitAi.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class OAuthBridgeController {

    public static final String ATTR_TARGET   = "OAUTH_MOBILE_TARGET";
    public static final String ATTR_REDIRECT = "OAUTH_MOBILE_REDIRECT_URI";

    // 앱에서 호출: /oauth2/authorize/{provider}?target=mobile&redirect_uri=<딥링크>
    @GetMapping("/oauth2/authorize/{provider}")
    public void bridge(@PathVariable String provider,
                       @RequestParam(required = false) String target,
                       @RequestParam(name = "redirect_uri", required = false) String redirectUri,
                       HttpServletRequest req,
                       HttpServletResponse res) throws IOException {

        if ("mobile".equalsIgnoreCase(target) && redirectUri != null) {
            var session = req.getSession(true);
            session.setAttribute(ATTR_TARGET, "mobile");
            session.setAttribute(ATTR_REDIRECT, redirectUri);
        }
        // 진짜 Spring Security 엔드포인트로 넘김
        res.sendRedirect("/oauth2/authorization/" + provider);
    }
}
