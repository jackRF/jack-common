package org.jack.common.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StockDecision {
    /**
     * 买入价
     */
    private BigDecimal purchasePrice;
    /**
     * 卖出价
     */
    private BigDecimal sellOutPrice;
    /**
     * 数量
     */
    private Long turnover; 
    /**
     * 相对收益率
     */
    private Double relativeYield;
    public void apply(Stock stock,StockTrade stockTrade,StockAccount stockAccount){
        BigDecimal fund=stockAccount.getFund();
        BigDecimal maxPrice=stockTrade.getMaxPrice();
        BigDecimal minPrice=stockTrade.getMinPrice();
        Long holdTurnover=stockAccount.getHoldShares().get(stock);
        if(holdTurnover==null){
            holdTurnover=0l;
        }
        if(maxPrice.compareTo(sellOutPrice)>=0){
            if(holdTurnover>=turnover&&turnover>0){
                fund=fund.add(sellOutPrice.max(stockTrade.getOpenPrice()).multiply(BigDecimal.valueOf(turnover)));
                holdTurnover-=turnover;
            }
        }
        if(minPrice.compareTo(purchasePrice)<=0){
            long count=fund.divide(purchasePrice,1,RoundingMode.FLOOR).divide(BigDecimal.valueOf(300),0,RoundingMode.FLOOR).intValue()*100;
            if(count>0){
                fund=fund.subtract(purchasePrice.min(stockTrade.getOpenPrice()).multiply(BigDecimal.valueOf(count)));
                holdTurnover+=count;
            }
        }
        stockAccount.setFund(fund);
        stockAccount.getHoldShares().put(stock, holdTurnover);
    }
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getSellOutPrice() {
        return sellOutPrice;
    }

    public void setSellOutPrice(BigDecimal sellOutPrice) {
        this.sellOutPrice = sellOutPrice;
    }

    public Long getTurnover() {
        return turnover;
    }

    public void setTurnover(Long turnover) {
        this.turnover = turnover;
    }

    public Double getRelativeYield() {
        return relativeYield;
    }

    public void setRelativeYield(Double relativeYield) {
        this.relativeYield = relativeYield;
    }
}
