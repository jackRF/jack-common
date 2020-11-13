package org.jack.common.domain;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jack.common.core.Pair;
import org.jack.common.core.Trainable;

public class SmartStockStrategy implements StockStrategy {
    public static int RATE_TYPE_AVERAGE = 1;
    public static int RATE_TYPE_MAXORMIN = 2;
    public static int RATE_TYPE_NORMAL = 3;
    public static int RATE_TYPE_MAXORMIN_AVERAGE = 4;
    public static final SmartStockStrategy DEFAULT;
    public static final SmartStockStrategy RATEAVERAGE_STOCKSTRATEGY;
    static {
        SmartStockStrategy.RateWeight weight = new SmartStockStrategy.RateWeight();
        weight.setRateCount(3);
        weight.setCat(3d);
        weight.setwPurchase(BigDecimal.valueOf(0.8));
        weight.setbPurchase(BigDecimal.ZERO);
        weight.setLimitPurchase(BigDecimal.valueOf(0.015));
        weight.setwSellOut(BigDecimal.valueOf(0.8));
        weight.setbSellOut(BigDecimal.ZERO);
        weight.setLimitSellOut(BigDecimal.valueOf(0.015));
        DEFAULT = new SmartStockStrategy(SmartStockStrategy.RATE_TYPE_MAXORMIN, SmartStockStrategy.RATE_TYPE_AVERAGE,
                weight);
        weight.setRateCount(3);
        weight.setCat(3d);
        weight.setwPurchase(BigDecimal.valueOf(1.0));
        weight.setbPurchase(BigDecimal.valueOf(0.005));
        weight.setLimitPurchase(BigDecimal.ZERO);
        weight.setwSellOut(BigDecimal.valueOf(1.0));
        weight.setbSellOut(BigDecimal.valueOf(0.005));
        weight.setLimitSellOut(BigDecimal.ZERO);
        RATEAVERAGE_STOCKSTRATEGY = new SmartStockStrategy(SmartStockStrategy.RATE_TYPE_AVERAGE,
                SmartStockStrategy.RATE_TYPE_NORMAL, weight);
    }
    private int rateType;
    private int rateType2;
    private RateWeight weight;
    private RateStrategy rateStrategy;

    public SmartStockStrategy(int rateType, int rateType2, RateWeight weight) {
        this.rateType = rateType;
        this.rateType2 = rateType2;
        this.weight = weight;
        this.rateStrategy = new RateStrategy() {
            @Override
            public double useCat() {
                return weight.getCat();
            }

            @Override
            public int useRateCount() {
                return weight.getRateCount();
            }

            @Override
            public BigDecimal usePurchaseRate(BigDecimal max, BigDecimal min, StockTrade first, StockTrade last,
                    BigDecimal rateAverage) {
                BigDecimal rate = useRate(rateType, rateAverage, max, min, false, first, last);
                return rate.multiply(weight.getwPurchase()).add(weight.getbPurchase()).max(weight.limitPurchase);
            }

            @Override
            public BigDecimal useSellOutRate(BigDecimal max, BigDecimal min, StockTrade first, StockTrade last,
                    BigDecimal rateAverage) {
                BigDecimal rate = useRate(rateType, rateAverage, max, min, true, first, last);
                return rate.multiply(weight.getwSellOut()).add(weight.getbSellOut()).max(weight.limitSellOut);
            }

            @Override
            public BigDecimal usePurchaseRate2(BigDecimal max, BigDecimal min, StockTrade first, StockTrade last,
                    BigDecimal rateAverage) {
                BigDecimal rate2 = useRate(rateType2, rateAverage, max, min, false, first, last);
                // rate2=rate2.multiply(weight.getwPurchase()).add(weight.getbPurchase()).max(weight.limitPurchase);
                return usePurchaseRate(max, min, first, last, rateAverage).add(rate2);
            }

            @Override
            public BigDecimal useSellOutRate2(BigDecimal max, BigDecimal min, StockTrade first, StockTrade last,
                    BigDecimal rateAverage) {
                BigDecimal rate2 = useRate(rateType2, rateAverage, max, min, true, first, last);
                // rate2=rate2.multiply(weight.getwSellOut()).add(weight.getbSellOut()).max(weight.limitSellOut);
                return useSellOutRate(max, min, first, last, rateAverage).add(rate2);
            }

            private BigDecimal useRate(int rateType, BigDecimal rateAverage, BigDecimal max, BigDecimal min,
                    boolean sellOut, StockTrade first, StockTrade last) {
                BigDecimal rate = BigDecimal.ZERO;
                if (rateType == RATE_TYPE_AVERAGE) {
                    rate = rateAverage;
                } else if (rateType == RATE_TYPE_MAXORMIN) {
                    rate = RateStrategy.super.useMaxOrMinRate(sellOut ? max : min, last);
                } else if (rateType == RATE_TYPE_MAXORMIN_AVERAGE) {
                    rate = RateStrategy.super.useMaxOrMinRateAverage(max, min, last);
                } else {
                    rate = RateStrategy.super.useRate(first, last);
                }
                return rate;
            }
        };
    }

    public int getRateType() {
        return rateType;
    }

    public void setRateType(int rateType) {
        this.rateType = rateType;
    }

    public int getRateType2() {
        return rateType2;
    }

    public void setRateType2(int rateType2) {
        this.rateType2 = rateType2;
    }

    public RateWeight getWeight() {
        return weight;
    }

    public void setWeight(RateWeight weight) {
        this.weight = weight;
    }

    @Override
    public RateStrategy getRateStrategy() {
        return rateStrategy;
    }

    public static class RateWeight extends Trainable {
        private Double cat;
        private Integer rateCount;
        private BigDecimal wPurchase;
        private BigDecimal bPurchase;
        private BigDecimal limitPurchase;
        private BigDecimal wSellOut;
        private BigDecimal bSellOut;
        private BigDecimal limitSellOut;

        public RateWeight(String... noTrain) {
            super(noTrain);
        }

        public static RateWeight of(double w, double b, double limit, String... noTrain) {
            SmartStockStrategy.RateWeight weight = new SmartStockStrategy.RateWeight(noTrain);
            weight.setRateCount(3);
            weight.setCat(3d);
            weight.setwPurchase(BigDecimal.valueOf(w));
            weight.setbPurchase(BigDecimal.valueOf(b));
            weight.setLimitPurchase(BigDecimal.valueOf(limit));
            weight.setwSellOut(BigDecimal.valueOf(w));
            weight.setbSellOut(BigDecimal.valueOf(b));
            weight.setLimitSellOut(BigDecimal.valueOf(limit));
            return weight;
        }

        public static Pair<SmartStockStrategy.RateWeight, Pair<StockTrade[], BigDecimal>> trainStockTrade(Stock stock,
                BigDecimal fund, List<StockTrade> stockTradeList, String... noTrain) {
            return trainStockTrade(stock, fund, SmartStockStrategy.RATE_TYPE_AVERAGE,
                    SmartStockStrategy.RATE_TYPE_NORMAL, stockTradeList, noTrain);
        }

        public static Pair<SmartStockStrategy.RateWeight, Pair<StockTrade[], BigDecimal>> trainStockTrade(Stock stock,
                BigDecimal fund, int rateType1, int rateType2, List<StockTrade> stockTradeList, String... noTrain) {
            SmartStockStrategy.RateWeight weightDest = new SmartStockStrategy.RateWeight();
            SmartStockStrategy.RateWeight weight = RateWeight.of(1.5, 0.005, 0.02, noTrain);
            StockStrategy stockStrategy = new SmartStockStrategy(rateType1, rateType2, weight);
            BigDecimal maxMarketValue = null;
            Pair<RateWeight, Pair<StockTrade[], BigDecimal>> wPair = null;
            for (;;) {
                StockAccount stockAccount = new StockAccount(fund);
                wPair = StockTrade.mockStockTrade(stockAccount, stock, stockStrategy, stockTradeList);
                Map<Stock, StockTrade> stockMarket = new HashMap<>();
                stockMarket.put(stock, wPair.getV2().getV1()[1]);
                BigDecimal marketValue = stockAccount.useMarketValue(stockMarket);
                boolean better = false;
                if (maxMarketValue == null || maxMarketValue.compareTo(marketValue) < 0) {
                    maxMarketValue = marketValue;
                    better = true;
                }
                if (!weight.train(weightDest, better)) {
                    break;
                }
            }
            Pair<SmartStockStrategy.RateWeight, Pair<StockTrade[], BigDecimal>> maxWPair = new Pair<SmartStockStrategy.RateWeight, Pair<StockTrade[], BigDecimal>>();
            maxWPair.setV1(weightDest);
            maxWPair.setV2(new Pair<>(wPair.getV2().getV1(), maxMarketValue));
            return maxWPair;
        }

        @Override
        protected Object useNewValue(String name, Object value) {
            if (name.equals("rateCount")) {
                return sway((int) value,1,2, 3);
            } else if (name.equals("cat")) {
                return sway((double) value, 0.1,1.01, 3);
            } else {
                BigDecimal v = (BigDecimal) value;
                if (name.startsWith("w")) {
                    return sway(v, BigDecimal.valueOf(0.1),BigDecimal.valueOf(0.51), BigDecimal.valueOf(1.5));
                } else if (name.startsWith("b")) {
                    return sway(v, BigDecimal.valueOf(0.001),BigDecimal.valueOf(0.0051), BigDecimal.valueOf(0.005));
                } else {
                    return sway(v, BigDecimal.valueOf(0.001),BigDecimal.valueOf(0.0051), BigDecimal.valueOf(0.02));
                }
            }
        }

        public Double getCat() {
            return cat;
        }

        public void setCat(Double cat) {
            this.cat = cat;
        }

        public Integer getRateCount() {
            return rateCount;
        }

        public void setRateCount(Integer rateCount) {
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

        protected BigDecimal sway(BigDecimal v, BigDecimal f, BigDecimal maxf, BigDecimal m) {
            BigDecimal diff = (m.subtract(v));
            BigDecimal nv=m.add(diff);
            if(diff.compareTo(BigDecimal.ZERO)<=0){
                nv=nv.subtract(f);
            }
           if(nv.compareTo(m.subtract(maxf))>=0&&nv.compareTo(m.add(maxf))<=0){
                return nv;
           }
            return null;
        }

        protected Integer sway(int v, int f, int maxf, int m) {
            int diff = (m - v);
            Integer nv=m+diff+(diff>0?0:-f);
            if (nv >= (m - maxf) && nv <= (m + maxf)) {
                return nv;
            }
            return null;
        }

        protected Double sway(double v, double f, double maxf, double m) {
            double diff = (m - v);
            Double nv=m+diff+(diff>0?0:-f);
            if (nv >= (m - maxf) && nv <= (m + maxf)) {
                return nv;
            }
            return null;
        }

        @Override
        protected void sort(PropertyDescriptor[] pds) {

            Arrays.sort(pds, new Comparator<PropertyDescriptor>() {

                @Override
                public int compare(PropertyDescriptor o1, PropertyDescriptor o2) {
                    String n1 = o1.getName();
                    String n2 = o2.getName();
                    int or1 = order(n1);
                    int or2 = order(n2);
                    if (or1 == or2) {
                        return n1.compareTo(n2);
                    }
                    return or1 - or2;
                }
            });
        }

        private int order(String name) {
            int w=0; 
            if(name.endsWith("Purchase")){
                w=-20;
            }else if(name.endsWith("SellOut")){
                w=-10;
            }else{
                return -1000;
            }
            if (name.startsWith("w")) {
                return w+1;
            }
            if (name.startsWith("b")) {
                return w+2;
            }
            if (name.startsWith("limit")) {
                return w+3;
            }
            if (name.equals("cat")) {
                return 4;
            }
            if (name.equals("rateCount")) {
                return 6;
            }
            return 7;
        }
    }
}
