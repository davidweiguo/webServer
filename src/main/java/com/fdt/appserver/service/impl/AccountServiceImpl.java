package com.fdt.appserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fdt.appserver.actor.LoginActor;
import com.fdt.appserver.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import rsa.RSAUtils;

import javax.annotation.PostConstruct;

/**
 * @author guo_d
 * @date 2018/09/03
 */
@Service("accountService")
public class AccountServiceImpl extends BaseService implements AccountService {

    private static final Logger log = LoggerFactory.getLogger("AccountService");

    @PostConstruct
    private void init() {
        try {
            RSAUtils.initKey();
        } catch (Exception e) {
            log.error("Exception happens when initKey of RSAUtils: {}, System exits!!!", e.getMessage());
            System.exit(1);
        }
    }

    /**
     * 解析用户登录 token，如果能解析成功，则验证通过，否则登录失败
     * "token": "K4OLplmPt9ousKG9wUjJqgISUGoxSF9K2OXR1yNrsoZKWEm6plYk3GWXNnXIYT4l9V1CHXdFUAJv7o+lUV7BuNXnwzp0bLvxB7ZOdJ
     * L191QzHF5PAhmvMFZQlJ8uuns8oygHCamFmT0rjla33ZyyItp8RqoBuBMlgTf+0VWyUDw4S51CuLe+FXSb/ca/dgX3N4Zjx9zFMFaL
     * Z1SbfikoWvmRcwomR/q6HySZBp8V/ifW6Tg7Rbdxe/rZHQxmb2/IzaMX4TgCNHC5skF9691L/ez80r22lzht5DMi8i88ToSRB3f2zR
     * i7WjzI3SAEjVLukYM3dHQdUDewu69lwbGBtA=="
     * <p>
     * "acc": test1
     *
     * @param request 用户信息
     */
    @Override
    public void login(LoginActor.LoginRequest request) {
        log.info(request.getToken());
        boolean ok = true;
        String msg = "";
        String userId = null;
        if (StringUtils.hasText(request.getToken())) {
            try {
                byte[] bytes1 = RSAUtils.decryptByPrivateKey(request.getToken().getBytes(), true);
                JSONObject object = JSONObject.parseObject(new String(bytes1));
                String accKey = "acc";
                if (object.containsKey(accKey)) {
                    userId = object.get(accKey).toString();
                }
            } catch (Exception e) {
                ok = false;
                msg = "Invalid Token Value";
            }
        } else {
            ok = false;
            msg = "Empty Token!";
        }
        LoginActor.LoginResponse response = new LoginActor.LoginResponse(request.getConnectionId(), ok, msg);
        if (StringUtils.hasText(userId)) {
            response.setUserId(userId);
        }
        actorDispatcher.sendLoginResponse(response);
    }
}
