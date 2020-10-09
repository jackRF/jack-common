package org.jack.common.domain;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.CollectionUtils;

public class StockAccount {
    /**
     * 资金
     */
    private BigDecimal fund;
    /**
     * 股票持仓
     */
    private Map<Stock, Long> holdShares;
    public StockAccount(BigDecimal fund){
        this(fund,new HashMap<>());
    }
    public StockAccount(BigDecimal fund,Map<Stock, Long> holdShares){
        this.fund=fund;
        this.holdShares=holdShares;
    }
    public BigDecimal useMarketValue(Map<Stock, StockTrade> stockMarket){
        BigDecimal marketValue=fund;
        if(!CollectionUtils.isEmpty(holdShares)){
            for(Map.Entry<Stock, Long> entry:holdShares.entrySet()){
                StockTrade stockTrade=stockMarket.get(entry.getKey());
                if(stockTrade!=null){
                    marketValue=marketValue.add(stockTrade.getClosePrice().multiply(BigDecimal.valueOf(entry.getValue())));
                }
            }
        }
        return marketValue;
    }
    public BigDecimal getFund() {
        return fund;
    }

    public void setFund(BigDecimal fund) {
        this.fund = fund;
    }

    public Map<Stock, Long> getHoldShares() {
        return holdShares;
    }
    public void setHoldShares(Map<Stock, Long> holdShares) {
        this.holdShares = holdShares;
    }
}
