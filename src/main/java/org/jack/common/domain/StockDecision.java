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
     * 买入数量
     */
    private Long purchaseTurnover=0l;
    /**
     * 卖出数量
     */
    private Long sellOutTurnover=0l;
    public void apply(Stock stock,StockTrade stockTrade,StockAccount stockAccount){
        BigDecimal fund=stockAccount.getFund();
        BigDecimal maxPrice=stockTrade.getMaxPrice();
        BigDecimal minPrice=stockTrade.getMinPrice();
        Long holdTurnover=stockAccount.getHoldShares().get(stock);
        if(holdTurnover==null){
            holdTurnover=0l;
        }
        if(maxPrice.compareTo(sellOutPrice)>=0){
            if(holdTurnover>=sellOutTurnover&&sellOutTurnover>0){
                BigDecimal trade=sellOutPrice.max(stockTrade.getOpenPrice()).multiply(BigDecimal.valueOf(sellOutTurnover));
                fund=fund.add(trade).subtract(trade.multiply(BigDecimal.valueOf(0.004)));
                holdTurnover-=sellOutTurnover;
            }
        }
        if(minPrice.compareTo(purchasePrice)<=0){
            if(purchaseTurnover>0){
                BigDecimal useFund=purchasePrice.min(stockTrade.getOpenPrice()).multiply(BigDecimal.valueOf(purchaseTurnover));
                if(fund.compareTo(useFund)>=0){
                    fund=fund.subtract(useFund).subtract(useFund.multiply(BigDecimal.valueOf(0.003)));
                    holdTurnover+=purchaseTurnover;
                }
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

    public Long getPurchaseTurnover() {
        return purchaseTurnover;
    }

    public void setPurchaseTurnover(Long purchaseTurnover) {
        this.purchaseTurnover = purchaseTurnover;
    }

    public Long getSellOutTurnover() {
        return sellOutTurnover;
    }

    public void setSellOutTurnover(Long sellOutTurnover) {
        this.sellOutTurnover = sellOutTurnover;
    }
   
}
