package com.fdt.appserver.actor;

import akka.actor.AbstractActor;
import com.fdt.appserver.service.TradingService;
import com.google.gson.annotations.Expose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author guo_d
 * @date 2018/09/03
 */
@Component("enterOrder")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EnterOrderActor extends AbstractActor {

    private static final Logger log = LoggerFactory.getLogger(EnterOrderActor.class);

    @Autowired
    private TradingService tradingService;

    @Override
    public Receive createReceive() {
        return this.receiveBuilder()
                .match(EnterOrderRequest.class, this::onEnterOrderRequest)
                .match(EnterOrderResponse.class, this::onEnterOrderResponse)
                .build();
    }

    private void onEnterOrderRequest(EnterOrderRequest request) {
        tradingService.enterOrder(request);
    }

    private void onEnterOrderResponse(EnterOrderResponse response) {

    }

    public static class EnterOrderRequest extends BaseData {
        @Expose
        private String customerId;

        @Expose
        private String clientId;

        @Expose
        private String symbol;

        @Expose
        private String amount;

        @Expose
        private String price;

        @Expose
        private String stopPrice;

        @Expose
        private String side;

        @Expose
        private String type;

        @Expose
        private String option;

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getStopPrice() {
            return stopPrice;
        }

        public void setStopPrice(String stopPrice) {
            this.stopPrice = stopPrice;
        }

        public String getSide() {
            return side;
        }

        public void setSide(String side) {
            this.side = side;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getOption() {
            return option;
        }

        public void setOption(String option) {
            this.option = option;
        }

        @Override
        public String toString() {
            return "EnterOrderRequest{" +
                    "customerId='" + customerId + '\'' +
                    ", clientId='" + clientId + '\'' +
                    ", symbol='" + symbol + '\'' +
                    ", amount='" + amount + '\'' +
                    ", price='" + price + '\'' +
                    ", stopPrice='" + stopPrice + '\'' +
                    ", side='" + side + '\'' +
                    ", type='" + type + '\'' +
                    ", option='" + option + '\'' +
                    '}';
        }
    }

    public static class EnterOrderResponse extends BaseResponse {

        private String orderId;

        public EnterOrderResponse(String connectionId, boolean ok, String msg) {
            super(connectionId, ok, msg);
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }
    }
}
