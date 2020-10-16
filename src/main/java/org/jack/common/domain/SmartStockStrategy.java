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
    public static final SmartStockStrategy DEFAULT;
    public static final SmartStockStrategy RATEAVERAGE_STOCKSTRATEGY;
    static {
        SmartStockStrategy.RateWeight weight = new SmartStockStrategy.RateWeight();
        weight.setRateCount(3);
        weight.setCat(3d);
        weight.setwPurchase(BigDecimal.valueOf(0.8));
        weight.setLimitPurchase(BigDecimal.valueOf(0.015));
        weight.setwSellOut(BigDecimal.valueOf(0.8));
        weight.setLimitSellOut(BigDecimal.valueOf(0.015));
        DEFAULT = new SmartStockStrategy(SmartStockStrategy.RATE_TYPE_MAXORMIN, SmartStockStrategy.RATE_TYPE_AVERAGE,
                weight);
        weight.setRateCount(3);
        weight.setCat(3d);
        weight.setwPurchase(BigDecimal.valueOf(1.0));
        weight.setbPurchase(BigDecimal.valueOf(0.005));
        weight.setwSellOut(BigDecimal.valueOf(1.0));
        weight.setbSellOut(BigDecimal.valueOf(0.005));
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
                BigDecimal rate = useRate(rateType, rateAverage, min, first, last);
                return rate.multiply(weight.getwPurchase()).add(weight.getbPurchase()).max(weight.limitPurchase);
            }

            @Override
            public BigDecimal useSellOutRate(BigDecimal max, BigDecimal min, StockTrade first, StockTrade last,
                    BigDecimal rateAverage) {
                BigDecimal rate = useRate(rateType, rateAverage, max, first, last);
                return rate.multiply(weight.getwSellOut()).add(weight.getbSellOut()).max(weight.limitSellOut);
            }

            @Override
            public BigDecimal usePurchaseRate2(BigDecimal max, BigDecimal min, StockTrade first, StockTrade last,
                    BigDecimal rateAverage) {
                BigDecimal rate2=useRate(rateType2, rateAverage, min, first, last);
                // rate2=rate2.multiply(weight.getwPurchase()).add(weight.getbPurchase()).max(weight.limitPurchase);
                return usePurchaseRate(max, min, first, last, rateAverage).add(rate2);
            }

            @Override
            public BigDecimal useSellOutRate2(BigDecimal max, BigDecimal min, StockTrade first, StockTrade last,
                    BigDecimal rateAverage) {
                BigDecimal rate2=useRate(rateType2, rateAverage, max, first, last);
                // rate2=rate2.multiply(weight.getwSellOut()).add(weight.getbSellOut()).max(weight.limitSellOut);
                return useSellOutRate(max, min, first, last, rateAverage).add(rate2);
            }

            private BigDecimal useRate(int rateType, BigDecimal rateAverage, BigDecimal maxOrMin, StockTrade first,
                    StockTrade last) {
                BigDecimal rate = BigDecimal.ZERO;
                if (rateType == RATE_TYPE_AVERAGE) {
                    rate = rateAverage;
                } else if (rateType == RATE_TYPE_MAXORMIN) {
                    rate = RateStrategy.super.useMaxOrMinRate(maxOrMin, last);
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
        private Double cat = 3d;
        private Integer rateCount = 3;
        private BigDecimal wPurchase = BigDecimal.ONE;
        private BigDecimal bPurchase = BigDecimal.ZERO;
        private BigDecimal limitPurchase = BigDecimal.ZERO;
        private BigDecimal wSellOut = BigDecimal.ONE;
        private BigDecimal bSellOut = BigDecimal.ZERO;
        private BigDecimal limitSellOut = BigDecimal.ZERO;

        public RateWeight(String... noTrain) {
            super(noTrain);
        }

        public static Pair<SmartStockStrategy.RateWeight, Pair<StockTrade[], BigDecimal>> trainStockTrade(Stock stock,
                BigDecimal fund, List<StockTrade> stockTradeList, String... noTrain) {
            return trainStockTrade(stock, fund, SmartStockStrategy.RATE_TYPE_AVERAGE,
                    SmartStockStrategy.RATE_TYPE_NORMAL,stockTradeList , noTrain);
        }
        public static Pair<SmartStockStrategy.RateWeight, Pair<StockTrade[], BigDecimal>> trainStockTrade(Stock stock,
                BigDecimal fund, int rateType1, int rateType2, List<StockTrade> stockTradeList, String... noTrain) {
            SmartStockStrategy.RateWeight weightDest = new SmartStockStrategy.RateWeight();
            weightDest.setRateCount(null);
            weightDest.setCat(null);
            weightDest.setwPurchase(null);
            weightDest.setwSellOut(null);
            weightDest.setbPurchase(null);
            weightDest.setbSellOut(null);
            weightDest.setLimitPurchase(null);
            weightDest.setLimitSellOut(null);
            SmartStockStrategy.RateWeight weight = new SmartStockStrategy.RateWeight(noTrain);
            weight.setRateCount(3);
            weight.setCat(2d);
            weight.setwPurchase(BigDecimal.valueOf(1.5));
            weight.setbPurchase(BigDecimal.valueOf(0.005));
            weight.setLimitPurchase(BigDecimal.valueOf(0.015));
            weight.setwSellOut(BigDecimal.valueOf(1.5));
            weight.setbSellOut(BigDecimal.valueOf(0.005));
            weight.setLimitSellOut(BigDecimal.valueOf(0.015));
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
                int v = sway((int) value, 1, 3);
                if (v >= 1 && v <= 5) {
                    return v;
                }
            } else if (name.equals("cat")) {
                double v = sway((double) value, 0.1, 2);
                if (v >= 1 && v <= 3) {
                    return v;
                }
            } else {
                BigDecimal v = (BigDecimal) value;
                if (name.startsWith("w")) {
                    v = sway(v, BigDecimal.valueOf(0.01), BigDecimal.valueOf(1.5));
                    if (v.compareTo(BigDecimal.valueOf(0.5)) >= 0 && v.compareTo(BigDecimal.valueOf(2.5)) <= 0) {
                        return v;
                    }
                } else if (name.startsWith("b")) {
                    v = sway(v, BigDecimal.valueOf(0.001), BigDecimal.valueOf(0.005));
                    if (v.compareTo(BigDecimal.ZERO) >= 0 && v.compareTo(BigDecimal.valueOf(0.01)) <= 0) {
                        return v;
                    }
                } else {
                    v = sway(v, BigDecimal.valueOf(0.001), BigDecimal.valueOf(0.015));
                    if (v.compareTo(BigDecimal.ZERO) >= 0 && v.compareTo(BigDecimal.valueOf(0.03)) <= 0) {
                        return v;
                    }
                }
            }
            return null;
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

        protected BigDecimal sway(BigDecimal v, BigDecimal b, BigDecimal center) {
            if (v.compareTo(center) > 0) {
                v = center.subtract(v.subtract(center)).subtract(b);
            } else {
                v = center.subtract(v.subtract(center)).add(b);
            }
            return v;
        }

        protected int sway(int v, int b, int center) {
            if (v > center) {
                v = center - (v - center) - b;
            } else {
                v = center - (v - center) + b;
            }
            return v;
        }

        protected double sway(double v, double b, double center) {
            if (v > center) {
                v = center - (v - center) - b;
            } else {
                v = center - (v - center) + b;
            }
            return v;
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
            if (name.startsWith("w")) {
                return 1;
            }
            if (name.startsWith("2")) {
                return 2;
            }
            if (name.startsWith("limit")) {
                return 3;
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
