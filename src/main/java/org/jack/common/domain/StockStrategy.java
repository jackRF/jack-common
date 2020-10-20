package org.jack.common.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface StockStrategy {
    public static final StockStrategy DEFAULT=SmartStockStrategy.DEFAULT;
    public static final StockStrategy RATEAVERAGE_STOCKSTRATEGY=SmartStockStrategy.RATEAVERAGE_STOCKSTRATEGY;
    
    RateStrategy getRateStrategy();
    default List<StockDecision> apply(List<StockTrade> data, Long turnover,BigDecimal fund){
        Collections.sort(data);
        Collections.reverse(data);
        RateStrategy rateStrategy=getRateStrategy();
        int rateCount = rateStrategy.useRateCount();
        int rc = 0;
        BigDecimal rateAverage = BigDecimal.ZERO;
        BigDecimal max=null;
        BigDecimal min=null;
        StockTrade first = null;
        StockTrade last = null;
        for (StockTrade stockTrade : data) {
            if(max==null||stockTrade.getMaxPrice().compareTo(max)>0){
                max=stockTrade.getMaxPrice();
            }
            if(min==null||stockTrade.getMinPrice().compareTo(min)<0){
                min=stockTrade.getMinPrice();
            }
            if (first == null) {
                first = stockTrade;
                last = stockTrade;
            } else {
                BigDecimal lastClosePrice=last.getClosePrice();
                BigDecimal closePrice = stockTrade.getClosePrice();
                last = stockTrade;
                BigDecimal currentRate = lastClosePrice.subtract(closePrice).divide(closePrice, 4,
                        RoundingMode.HALF_UP);
                rateAverage = rateAverage.add(currentRate.abs());
                rc++;
                if (rc == rateCount) {
                    break;
                }
            }
        }
        rateAverage = rateAverage.divide(BigDecimal.valueOf(rc), 4, RoundingMode.HALF_UP);
        
        List<StockDecision> sdList = new ArrayList<>();
        BigDecimal purchaseRate=rateStrategy.usePurchaseRate(max,min,first,last,rateAverage);
        BigDecimal sellOutRate=rateStrategy.useSellOutRate(max, min, first, last, rateAverage);
        StockDecision stockDecision =StockDecision.of(purchaseRate, sellOutRate, first.getClosePrice());
        sdList.add(stockDecision);
        BigDecimal purchaseRate2=rateStrategy.usePurchaseRate2(max,min,first,last,rateAverage);
        BigDecimal sellOutRate2=rateStrategy.useSellOutRate2(max, min, first, last, rateAverage);
        StockDecision stockDecision2 =StockDecision.of(purchaseRate2, sellOutRate2, first.getClosePrice());
        sdList.add(stockDecision2);
        if(turnover==null){
            turnover=0l;
        }
        applyTurnover(stockDecision,turnover,fund);
        BigDecimal newFund=fund.subtract(stockDecision.getPurchasePrice().multiply(BigDecimal.valueOf(stockDecision.getPurchaseTurnover())));
        Long newTurnover=turnover-stockDecision.getSellOutTurnover();
        applyTurnover(stockDecision2,newTurnover,newFund);
        return sdList;
    }
    default void applyTurnover(StockDecision stockDecision,Long turnover,BigDecimal fund){
        double cat=getRateStrategy().useCat();
        BigDecimal purchasePrice=stockDecision.getPurchasePrice();
        long purchaseTurnover=0;
        if(purchasePrice.compareTo(BigDecimal.ZERO)>0){
            BigDecimal temp=purchasePrice.multiply(BigDecimal.valueOf(100*cat));
            purchaseTurnover=fund.divide(temp,1,RoundingMode.FLOOR).intValue()*100;
        }else{
            // System.out.println("fund:"+fund+",cat:"+cat+",purchasePrice:"+purchasePrice);
        }
        long sellOutTurnover=0;
        if (turnover!=null&&turnover>0) {
            int use = BigDecimal.valueOf(turnover).divide(BigDecimal.valueOf(cat * 100), 0, RoundingMode.HALF_UP).intValue() * 100;
            if (use == 0) {
                sellOutTurnover=100;
            }else{
                sellOutTurnover=use;
            }
        }
        stockDecision.setPurchaseTurnover(purchaseTurnover);
        stockDecision.setSellOutTurnover(sellOutTurnover);
    }
    public static interface RateStrategy{
        double useCat();
        int useRateCount();
        BigDecimal usePurchaseRate(BigDecimal max,BigDecimal min,StockTrade first,StockTrade last,BigDecimal rateAverage);
        BigDecimal useSellOutRate(BigDecimal max,BigDecimal min,StockTrade first,StockTrade last,BigDecimal rateAverage);
        BigDecimal usePurchaseRate2(BigDecimal max,BigDecimal min,StockTrade first,StockTrade last,BigDecimal rateAverage);
        BigDecimal useSellOutRate2(BigDecimal max,BigDecimal min,StockTrade first,StockTrade last,BigDecimal rateAverage);
        default BigDecimal useRate(StockTrade first, StockTrade last){
            BigDecimal lastClosePrice=last.getClosePrice();
            return first.getClosePrice().subtract(lastClosePrice).divide(lastClosePrice, 4, RoundingMode.HALF_UP).abs();
        }
        default BigDecimal useMaxOrMinRate(BigDecimal maxOrMin,StockTrade last){
            BigDecimal firstOpenPrice=last.getOpenPrice();
            return maxOrMin.subtract(firstOpenPrice).divide(firstOpenPrice, 4,RoundingMode.HALF_UP).abs();
        }
        default BigDecimal useMaxOrMinRateAverage(BigDecimal max,BigDecimal min,StockTrade last){
            return useMaxOrMinRate(max,last).abs().add(useMaxOrMinRate(min,last).abs()).multiply(BigDecimal.valueOf(0.5));
        }
    }
}
