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
     * 买入相对收益率
     */
    private Double relativePurchaseRate;
    /**
     * 卖出相对收益率
     */
    private Double relativeSellOutRate;
    /**
     * 数量
     */
    private Long turnover;
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
    public static StockDecision of(Double relativePurchaseRate,Double relativeSellOutRate,BigDecimal basePrice){
        StockDecision stockDecision = new StockDecision();
        stockDecision.setPurchasePrice(BigDecimal.valueOf(1).subtract(BigDecimal.valueOf(relativePurchaseRate)).multiply(basePrice));
        stockDecision.setSellOutPrice(BigDecimal.valueOf(1).add(BigDecimal.valueOf(relativeSellOutRate)).multiply(basePrice));
        stockDecision.setRelativePurchaseRate(relativePurchaseRate);
        stockDecision.setRelativeSellOutRate(relativeSellOutRate);
        return stockDecision;
    }
    public static StockDecision of(BigDecimal relativePurchaseRate,BigDecimal relativeSellOutRate,BigDecimal basePrice){
        StockDecision stockDecision = new StockDecision();
        stockDecision.setPurchasePrice(BigDecimal.valueOf(1).subtract(relativePurchaseRate).multiply(basePrice));
        stockDecision.setSellOutPrice(BigDecimal.valueOf(1).add(relativeSellOutRate).multiply(basePrice));
        stockDecision.setRelativePurchaseRate(relativePurchaseRate.doubleValue());
        stockDecision.setRelativeSellOutRate(relativeSellOutRate.doubleValue());
        return stockDecision;
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
    public Double getRelativePurchaseRate() {
        return relativePurchaseRate;
    }

    public void setRelativePurchaseRate(Double relativePurchaseRate) {
        this.relativePurchaseRate = relativePurchaseRate;
    }

    public Double getRelativeSellOutRate() {
        return relativeSellOutRate;
    }

    public void setRelativeSellOutRate(Double relativeSellOutRate) {
        this.relativeSellOutRate = relativeSellOutRate;
    }
    public Long getTurnover() {
        return turnover;
    }

    public void setTurnover(Long turnover) {
        this.turnover = turnover;
    }
}
