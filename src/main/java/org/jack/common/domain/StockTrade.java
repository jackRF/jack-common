package org.jack.common.domain;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jack.common.core.Pair;

public class StockTrade implements Comparable<StockTrade> {
    /**
     * 时间
     */
    private String time;
    /**
     * 开盘价
     */
    private BigDecimal openPrice;
    /**
     * 收盘价
     */
    private BigDecimal closePrice;
    /**
     * 最高价
     */
    private BigDecimal maxPrice;
    /**
     * 最低价
     */
    private BigDecimal minPrice;
    /**
     * 成交量
     */
    private Long turnover;
    
    public static Pair<SmartStockStrategy.RateWeight,Pair<StockTrade[],BigDecimal>> mockStockTrade(StockAccount stockAccount, Stock stock,
            StockStrategy stockStrategy, List<StockTrade> stockTradeList) {
        int rateCount = stockStrategy.getRateStrategy().useRateCount();
        Collections.sort(stockTradeList);
        List<StockDecision> sdList = null;
        LinkedList<StockTrade> list = new LinkedList<>();
        for (StockTrade stockTrade : stockTradeList) {
            if (sdList != null) {
                for (StockDecision stockDecision : sdList) {
                    stockDecision.apply(stock, stockTrade, stockAccount);
                }
            }
            list.add(stockTrade);
            if (list.size() == rateCount + 1) {
                Long turnover = stockAccount.getHoldShares().get(stock);
                sdList = stockStrategy.apply(list, turnover == null ? 0l : turnover, stockAccount.getFund());
                list.removeFirst();
            }
        }
        StockTrade last=stockTradeList.get(stockTradeList.size() - 1);
        Map<Stock,StockTrade> stockMarket=new HashMap<>();
        stockMarket.put(stock, last);
        BigDecimal marketValue= stockAccount.useMarketValue(stockMarket);
        Pair<SmartStockStrategy.RateWeight,Pair<StockTrade[],BigDecimal>> wPair=new Pair<SmartStockStrategy.RateWeight,Pair<StockTrade[],BigDecimal>>();
        if(stockStrategy instanceof SmartStockStrategy){
            SmartStockStrategy smartStockStrategy=(SmartStockStrategy)stockStrategy;
            wPair.setV1(smartStockStrategy.getWeight());
        }
        wPair.setV2(new Pair<>(new StockTrade[]{stockTradeList.get(0),last} ,marketValue));
        return wPair;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public Long getTurnover() {
        return turnover;
    }

    public void setTurnover(Long turnover) {
        this.turnover = turnover;
    }

    @Override
    public int compareTo(StockTrade o) {
        return time.compareTo(o.time);
    }
}