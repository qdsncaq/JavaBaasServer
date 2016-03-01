package com.staryet.baas.admin.controller;

import com.staryet.baas.admin.service.ConsoleService;
import com.staryet.baas.common.entity.SimpleCode;
import com.staryet.baas.common.entity.SimpleError;
import com.staryet.baas.common.entity.SimpleResult;
import com.staryet.baas.config.AuthConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * API统计
 * Created by Codi on 15/10/20.
 */
@RestController
@RequestMapping(value = "/console")
public class ConsoleController {

    @Autowired
    private ConsoleService consoleService;
    @Autowired
    private AuthConfig authConfig;

    @RequestMapping(value = "/adminKey", method = RequestMethod.GET)
    @ResponseBody
    public SimpleResult adminKey(HttpServletRequest request) {
        SimpleResult result = SimpleResult.success();
        Cookie[] cookies = request.getCookies();
        String sessionToken = null;
        String username = null;
        for (Cookie cookie : cookies) {
            if ("JB-sessionToken".equals(cookie.getName())) {
                sessionToken = cookie.getValue();
            }
            if ("JB-username".equals(cookie.getName())) {
                username = cookie.getValue();
            }
        }
        if (StringUtils.isEmpty(sessionToken) || StringUtils.isEmpty(username)) {
            throw new SimpleError(SimpleCode.CONSOLE_NOT_LOGIN);
        } else {
            String sessionTokenExist = consoleService.getSessionToken(username);
            if (!sessionToken.equals(sessionTokenExist)) {
                throw new SimpleError(SimpleCode.CONSOLE_SESSIONTOKEN_ERROR);
            } else {
                result.put("key", authConfig.getKey());
            }
        }
        return result;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseBody
    public SimpleResult login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) {
        String sessionToken = consoleService.getSessionToken(username, password);
        response.addCookie(new Cookie("JB-username", username));
        response.addCookie(new Cookie("JB-sessionToken", sessionToken));
        return SimpleResult.success();
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    public SimpleResult logout(@RequestParam String username) {
        consoleService.removeSessionToken(username);
        return SimpleResult.success();
    }

}
