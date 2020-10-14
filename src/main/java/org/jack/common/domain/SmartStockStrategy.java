package org.jack.common.domain;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;

import org.jack.common.core.Trainable;

public class SmartStockStrategy implements StockStrategy {
    public static int RATE_TYPE_AVERAGE=1;
    public static int RATE_TYPE_MAXORMIN=2;
    public static int RATE_TYPE_NORMAL=3;
    private RateStrategy rateStrategy;
    public SmartStockStrategy(int rateType,int rateType2,RateWeight weight){
        this.rateStrategy=new RateStrategy(){
            @Override
            public double useCat() {
                return weight.getCat();
            }
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
    public static class RateWeight  extends Trainable {
        private Double cat=3d;
        private Integer rateCount=3;
        private BigDecimal wPurchase=BigDecimal.ONE;
        private BigDecimal bPurchase=BigDecimal.ZERO;
        private BigDecimal limitPurchase=BigDecimal.ZERO;
        private BigDecimal wSellOut=BigDecimal.ONE;
        private BigDecimal bSellOut=BigDecimal.ZERO;
        private BigDecimal limitSellOut=BigDecimal.ZERO;
        public RateWeight(String ...noTrain){
            super(noTrain);
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

        @Override
        protected Object useNewValue(String name, Object value) {
            if(name.equals("rateCount")){
                int v=sway((int) value,1,3);
                if(v>=1&&v<=5){
                    return v;
                }
            }else if(name.equals("cat")){
                double v=sway((double) value,0.1,2);
                if(v>=1&&v<=3){
                    return v;
                }
            }else{
                BigDecimal v=(BigDecimal)value;
                if(name.startsWith("w")){
                    v=sway(v,BigDecimal.valueOf(0.01),BigDecimal.valueOf(1.5));
                    if(v.compareTo(BigDecimal.valueOf(0.5))>=0&&v.compareTo(BigDecimal.valueOf(2.5))<=0){
                        return v;
                    }
                }else if(name.startsWith("b")){
                    v=sway(v,BigDecimal.valueOf(0.001),BigDecimal.valueOf(0.005));
                    if(v.compareTo(BigDecimal.ZERO)>=0&&v.compareTo(BigDecimal.valueOf(0.01))<=0){
                        return v;
                    }
                }else{
                    v=sway(v,BigDecimal.valueOf(0.001),BigDecimal.valueOf(0.015));
                    if(v.compareTo(BigDecimal.ZERO)>=0&&v.compareTo(BigDecimal.valueOf(0.03))<=0){
                        return v;
                    }
                }
            }
            return null;
        }
        protected BigDecimal sway(BigDecimal v,BigDecimal b, BigDecimal center){
            if(v.compareTo(center)>0){
                v=center.subtract(v.subtract(center)).subtract(b);
            }else{
                v=center.subtract(v.subtract(center)).add(b);
            }
            return v;
        }
        protected int sway(int v,int b, int center){
            if(v>center){
                v=center-(v-center)-b;
            }else{
                v=center-(v-center)+b;
            }
            return v;
        }
        protected double sway(double v,double b, double center){
            if(v>center){
                v=center-(v-center)-b;
            }else{
                v=center-(v-center)+b;
            }
            return v;
        }
        @Override
        protected void sort(PropertyDescriptor[] pds) {

            Arrays.sort(pds,new Comparator<PropertyDescriptor>(){

                @Override
                public int compare(PropertyDescriptor o1, PropertyDescriptor o2) {
                    String n1=o1.getName();
                    String n2=o2.getName();
                    int or1=order(n1);
                    int or2=order(n2);
                    if(or1==or2){
                        return n1.compareTo(n2);
                    }
                    return or1-or2;
                }
            });
        }
        private int order(String name){
            if(name.startsWith("w")){
                return 1;
            }
            if(name.startsWith("2")){
                return 2;
            }
            if(name.startsWith("limit")){
                return 3;
            }
            if(name.equals("cat")){
                return 4;
            }
            if(name.equals("rateCount")){
                return 6;
            }
            return 7;
        }
    }
}
