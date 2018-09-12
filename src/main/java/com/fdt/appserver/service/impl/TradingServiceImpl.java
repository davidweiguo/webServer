package com.fdt.appserver.service.impl;

import com.fdt.appserver.actor.EnterOrderActor;
import com.fdt.appserver.service.TradingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author guo_d
 * @date 2018/9/4
 */
@Service("tradingService")
public class TradingServiceImpl extends BaseService implements TradingService {

    private static final Logger log = LoggerFactory.getLogger("TradingService");

    @Override
    public void enterOrder(EnterOrderActor.EnterOrderRequest request) {
        // TODO - 连接下游 PB 进行下单
        log.info(request.toString());

    }
}


