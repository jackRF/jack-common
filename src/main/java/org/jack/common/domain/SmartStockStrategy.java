package org.jack.common.domain;

import java.math.BigDecimal;

public class SmartStockStrategy implements StockStrategy {
    public static int RATE_TYPE_AVERAGE=1;
    public static int RATE_TYPE_MAXORMIN=2;
    public static int RATE_TYPE_NORMAL=3;
    private RateStrategy rateStrategy;
    public SmartStockStrategy(int rateType,int rateType2,RateWeight weight){
        this.rateStrategy=new RateStrategy(){
            @Override
            public int useRateCount() {
                return weight.rateCount;
            }
            @Override
            public BigDecimal usePurchaseRate(BigDecimal max, BigDecimal min, StockTrade first, StockTrade last,
                    BigDecimal rateAverage) {
                BigDecimal rate=useRate(rateType, rateAverage, min, first, last);
                return rate.multiply(weight.getwPurchase()).add(weight.getbPurchase()).max(weight.limitPurchase);
            }
            @Override
            public BigDecimal useSellOutRate(BigDecimal max, BigDecimal min, StockTrade first, StockTrade last,
                    BigDecimal rateAverage) {
                BigDecimal rate=useRate(rateType, rateAverage, max, first, last);
                return rate.multiply(weight.getwSellOut()).add(weight.getbSellOut()).max(weight.limitSellOut);
            }
            @Override
            public BigDecimal usePurchaseRate2(BigDecimal max, BigDecimal min, StockTrade first, StockTrade last,
                    BigDecimal rateAverage) {
                return usePurchaseRate(max, min, first, last, rateAverage).add(useRate(rateType2, rateAverage, min, first, last));
            }
            @Override
            public BigDecimal useSellOutRate2(BigDecimal max, BigDecimal min, StockTrade first, StockTrade last,
                    BigDecimal rateAverage) {
                return useSellOutRate(max, min, first, last, rateAverage).add(useRate(rateType2, rateAverage, max, first, last));
            }
            private BigDecimal useRate(int rateType,BigDecimal rateAverage,BigDecimal maxOrMin,StockTrade first, StockTrade last){
                BigDecimal rate=BigDecimal.ZERO;
                if(rateType==RATE_TYPE_AVERAGE){
                    rate=rateAverage;
                }else if(rateType==RATE_TYPE_MAXORMIN){
                    rate=RateStrategy.super.useMaxOrMinRate(maxOrMin, last);
                }else{
                    rate=RateStrategy.super.useRate(first, last);
                }
                return rate;
            }
        };
    }
    @Override
    public RateStrategy getRateStrategy() {
        return rateStrategy;
    }
    public static class RateWeight{
        private int rateCount=3;
        private BigDecimal wPurchase=BigDecimal.ONE;
        private BigDecimal bPurchase=BigDecimal.ZERO;
        private BigDecimal limitPurchase=BigDecimal.ZERO;
        private BigDecimal wSellOut=BigDecimal.ONE;
        private BigDecimal bSellOut=BigDecimal.ZERO;
        private BigDecimal limitSellOut=BigDecimal.ZERO;

        public int getRateCount() {
            return rateCount;
        }

        public void setRateCount(int rateCount) {
            this.rateCount = rateCount;
        }

        public BigDecimal getwPurchase() {
            return wPurchase;
        }

        public void setwPurchase(BigDecimal wPurchase) {
            this.wPurchase = wPurchase;
        }

        public BigDecimal getbPurchase() {
            return bPurchase;
        }

        public void setbPurchase(BigDecimal bPurchase) {
            this.bPurchase = bPurchase;
        }

        public BigDecimal getLimitPurchase() {
            return limitPurchase;
        }

        public void setLimitPurchase(BigDecimal limitPurchase) {
            this.limitPurchase = limitPurchase;
        }

        public BigDecimal getwSellOut() {
            return wSellOut;
        }

        public void setwSellOut(BigDecimal wSellOut) {
            this.wSellOut = wSellOut;
        }

        public BigDecimal getbSellOut() {
            return bSellOut;
        }

        public void setbSellOut(BigDecimal bSellOut) {
            this.bSellOut = bSellOut;
        }

        public BigDecimal getLimitSellOut() {
            return limitSellOut;
        }

        public void setLimitSellOut(BigDecimal limitSellOut) {
            this.limitSellOut = limitSellOut;
        }
    }
}
