package com.fdt.appserver.service;

import com.fdt.appserver.actor.EnterOrderActor;

/**
 * @author guo_d
 * @date 2018/09/03
 */
public interface TradingService {

    /**
     * 下单
     *
     * @param request
     */
    void enterOrder(EnterOrderActor.EnterOrderRequest request);

}
