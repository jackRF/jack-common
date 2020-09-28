package org.jack.common.domain;

import java.math.BigDecimal;

public class StockTrade implements Comparable<StockTrade>{
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