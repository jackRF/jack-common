package org.jack.common.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface StockStrategy {
    public static StockStrategy DEFAULT=new StockStrategy(){};
    public static StockStrategy RATEAVERAGE_STOCKSTRATEGY=new StockStrategy(){
        @Override
        public RateStrategy getRateStrategy() {
            return RateStrategy.RATEAVERAGE_RATESTRATEGY;
        }
    };
    default RateStrategy getRateStrategy(){
        return RateStrategy.DEFAULT;
    }
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
        stockDecision.setTurnover(useTurnover(turnover));
        stockDecision2.setTurnover(useTurnover(turnover - stockDecision.getTurnover()));
        
        return sdList;
    }
    default long useTurnover(Long turnover){
        return useTurnover(turnover,getRateStrategy().useCat());
    }
    default long useTurnover(Long turnover,double cat) {
        if (turnover==null||turnover <= 0) {
            return 0;
        }
        int use = BigDecimal.valueOf(turnover).divide(BigDecimal.valueOf(cat * 100), 0, RoundingMode.HALF_UP).intValue()
                * 100;
        if (use == 0) {
            return 100;
        }
        return use;
    }
    public static interface RateStrategy{
        public static RateStrategy DEFAULT=new RateStrategy(){};
        public static RateStrategy RATEAVERAGE_RATESTRATEGY=new RateStrategy(){
            @Override
            public BigDecimal usePurchaseRate(BigDecimal max, BigDecimal min, StockTrade first, StockTrade last,
                    BigDecimal rateAverage) {
                return rateAverage.multiply(BigDecimal.valueOf(1.0)).add(BigDecimal.valueOf(0.005)).max(BigDecimal.valueOf(0.00));
            }
            @Override
            public BigDecimal useSellOutRate(BigDecimal max, BigDecimal min, StockTrade first, StockTrade last,
                    BigDecimal rateAverage) {
                return rateAverage.multiply(BigDecimal.valueOf(1.0)).add(BigDecimal.valueOf(0.005)).max(BigDecimal.valueOf(0.00));
            }
            @Override
            public BigDecimal usePurchaseRate2(BigDecimal max, BigDecimal min, StockTrade first, StockTrade last,
                    BigDecimal rateAverage) {
                return usePurchaseRate(max, min, first, last, rateAverage).add(useRate(first,last));
            }
            @Override
            public BigDecimal useSellOutRate2(BigDecimal max, BigDecimal min, StockTrade first, StockTrade last,
                    BigDecimal rateAverage) {
                return useSellOutRate(max, min, first, last, rateAverage).add(useRate(first,last));
            }
        };
        default double useCat(){
            return 3.0;
        }
        default int useRateCount(){
            return 3;
        };
        default BigDecimal usePurchaseRate(BigDecimal max,BigDecimal min,StockTrade first,StockTrade last,BigDecimal rateAverage){
            return useMaxOrMinRate(min,last).multiply(BigDecimal.valueOf(0.8)).max(BigDecimal.valueOf(0.015));
        }
        default BigDecimal useSellOutRate(BigDecimal max,BigDecimal min,StockTrade first,StockTrade last,BigDecimal rateAverage){
            return useMaxOrMinRate(max,last).multiply(BigDecimal.valueOf(0.8)).max(BigDecimal.valueOf(0.015));
        }
        default BigDecimal usePurchaseRate2(BigDecimal max,BigDecimal min,StockTrade first,StockTrade last,BigDecimal rateAverage){
            return usePurchaseRate(max, min, first, last, rateAverage).add(rateAverage);
        }
        default BigDecimal useSellOutRate2(BigDecimal max,BigDecimal min,StockTrade first,StockTrade last,BigDecimal rateAverage){
            return useSellOutRate(max, min, first, last, rateAverage).add(rateAverage);
        }
        default BigDecimal useRate(StockTrade first, StockTrade last){
            BigDecimal lastClosePrice=last.getClosePrice();
            return first.getClosePrice().subtract(lastClosePrice).divide(lastClosePrice, 4, RoundingMode.HALF_UP).abs();
        }
        default BigDecimal useMaxOrMinRate(BigDecimal maxOrMin,StockTrade last){
            BigDecimal firstOpenPrice=last.getOpenPrice();
            return maxOrMin.subtract(firstOpenPrice).divide(firstOpenPrice, 4,RoundingMode.HALF_UP).abs();
        }
    }
}
