package com.fdt.appserver.service;

import com.fdt.appserver.actor.LoginActor;

/**
 * @author guo_d
 * @date 2018/09/04
 */
public interface AccountService {

    /**
     * 用户登录验证
     *
     * @param request 用户信息
     */
    void login(LoginActor.LoginRequest request);
}
