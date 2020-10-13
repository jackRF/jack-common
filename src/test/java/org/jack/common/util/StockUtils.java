package org.jack.common.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.ClientProtocolException;
import org.jack.common.BaseTest;
import org.jack.common.core.Pair;
import org.jack.common.domain.SmartStockStrategy;
import org.jack.common.domain.Stock;
import org.jack.common.domain.StockAccount;
import org.jack.common.domain.StockDecision;
import org.jack.common.domain.StockStrategy;
import org.jack.common.domain.StockTrade;
import org.junit.Test;
import org.springframework.util.StringUtils;

public class StockUtils extends BaseTest {
    @Test
    public void testStock() {
        StockAccount stockAccount = new StockAccount(BigDecimal.valueOf(50000));
        Map<Stock, Long> holdShares = stockAccount.getHoldShares();
        List<Stock> stocks = new ArrayList<>();
        Stock stock = new Stock("君正集团", "sh601216");
        stocks.add(stock);
        holdShares.put(stock, 400l);
        stock = new Stock("中国银河", "sh601881");
        stocks.add(stock);
        holdShares.put(stock, 200l);
        stock = new Stock("百傲化学", "sh603360");
        stocks.add(stock);
        holdShares.put(stock, 100l);
        stock = new Stock("世运电路", "sh603920");
        stocks.add(stock);
        holdShares.put(stock, 100l);
        stock = new Stock("格力电器", "sz000651");
        stocks.add(stock);
        holdShares.put(stock, 300l);
        stock = new Stock("华东医药", "sz000963");
        stocks.add(stock);
        holdShares.put(stock, 600l);
        stock = new Stock("红旗连锁", "sz002697");
        stocks.add(stock);
        holdShares.put(stock, 300l);

        // trainStockTrade(stocks, BigDecimal.valueOf(50000));
        
        List<Pair<Stock, List<StockDecision>>> stockStrategys = stockStrategy(stocks, stockAccount);
        int hi = 0;
        StringBuilder text = new StringBuilder();
        String[] head = { "股票名称", "股票代码", "\t买入1", "买入收益率1", "卖出1", "卖出收益率1", "交易量1", "买入2", "买入收益率2", "卖出2", "卖出收益率2",
                "交易量2" };
        for (String h : head) {
            if (hi++ == 0) {
                text.append(h);
            } else {
                text.append("\t").append(h);
            }
        }
        text.append("\n");
        for (Pair<Stock, List<StockDecision>> pair : stockStrategys) {
            Stock stockItem = pair.getV1();
            text.append(stockItem.getName()).append("\t").append(stockItem.getCode());
            List<StockDecision> v2 = pair.getV2();
            for (StockDecision sd : v2) {
                text.append("\t\t").append(sd.getPurchasePrice().setScale(2, RoundingMode.FLOOR));
                text.append("\t").append(
                        BigDecimal.valueOf(sd.getRelativePurchaseRate() * 100).setScale(2, RoundingMode.HALF_UP) + "%");
                text.append("\t\t").append(sd.getSellOutPrice().setScale(2, RoundingMode.CEILING));
                text.append("\t").append(
                        BigDecimal.valueOf(sd.getRelativeSellOutRate() * 100).setScale(2, RoundingMode.HALF_UP) + "%");
                text.append("\t\t").append(sd.getTurnover());
            }
            text.append("\n");
        }
        log(text);
    }
    @Test
    public void testMockStockTrade(){
        StockAccount stockAccount = new StockAccount(BigDecimal.valueOf(50000));
        // Stock stock=new Stock("君正集团", "sh601216");
        // Stock stock=new Stock("中国银河", "sh601881");
        // Stock stock=new Stock("百傲化学", "sh603360");
        // Stock stock=new Stock("世运电路", "sh603920");
        // Stock stock=new Stock("格力电器", "sz000651");
        // Stock stock = new Stock("华东医药", "sz000963");
        Stock stock=new Stock("红旗连锁", "sz002697");
        execMockStockTrade(stockAccount, stock, useStockStrategy(stock));
    }
    @Test
    public void testTrainStockTrade(){
        // Stock stock=new Stock("君正集团", "sh601216");
        // Stock stock=new Stock("中国银河", "sh601881");
        // Stock stock=new Stock("百傲化学", "sh603360");
        // Stock stock=new Stock("世运电路", "sh603920");
        // Stock stock=new Stock("格力电器", "sz000651");
        Stock stock = new Stock("华东医药", "sz000963");
        trainStockTrade(stock, BigDecimal.valueOf(50000));
    }
    private Map<Stock,SmartStockStrategy.RateWeight> swMap=new HashMap<>();
    public void trainStockTrade(List<Stock> stocks,BigDecimal fund){
        for(Stock stock:stocks){
            swMap.put(stock, trainStockTrade(stock, fund));
        }
    }
    public SmartStockStrategy.RateWeight trainStockTrade(Stock stock,BigDecimal fund) {
        StockAccount stockAccount = new StockAccount(fund);
        SmartStockStrategy.RateWeight weightDest = new SmartStockStrategy.RateWeight();
        weightDest.setRateCount(3);
        weightDest.setCat(null);
        weightDest.setwPurchase(null);
        weightDest.setwSellOut(null);
        weightDest.setbPurchase(null);
        weightDest.setbSellOut(null);
        weightDest.setLimitPurchase(null);
        weightDest.setLimitSellOut(null);
        SmartStockStrategy.RateWeight weight = new SmartStockStrategy.RateWeight();
        weight.setCat(1d);
        weight.setRateCount(3);
        weight.setwPurchase(BigDecimal.valueOf(0.01));
        weight.setbPurchase(BigDecimal.valueOf(0.000));
        weight.setwSellOut(BigDecimal.valueOf(0.01));
        weight.setbSellOut(BigDecimal.valueOf(0.000));
        StockStrategy stockStrategy = new SmartStockStrategy(SmartStockStrategy.RATE_TYPE_AVERAGE,
                SmartStockStrategy.RATE_TYPE_NORMAL, weight);
        // stockStrategy=StockStrategy.RATEAVERAGE_STOCKSTRATEGY;
        BigDecimal maxMarketValue = null;
        for (;;) {
            stockAccount = new StockAccount(fund);
            BigDecimal marketValue= execMockStockTrade(stockAccount, stock, stockStrategy);
            boolean better=false;
            if (maxMarketValue == null || maxMarketValue.compareTo(marketValue) < 0) {
                maxMarketValue = marketValue;
                better=true;
            }
            if(!weight.train(weightDest,better)){
                break;
            }
        }
        log("weight:" + ValueUtils.toJSONString(weightDest)+", maxMarketValue:" + maxMarketValue);
        return weightDest;
    }
    private StockStrategy useStockStrategy(Stock stock) {
        if(swMap.containsKey(stock)){
            StockStrategy stockStrategy = new SmartStockStrategy(SmartStockStrategy.RATE_TYPE_AVERAGE,
                SmartStockStrategy.RATE_TYPE_NORMAL, swMap.get(stock));
            return stockStrategy;
        }
        Stock stockTemp = new Stock("格力电器", "sz000651");
        if (stock.equals(stockTemp)) {
            SmartStockStrategy.RateWeight weight = new SmartStockStrategy.RateWeight();
            weight.setRateCount(3);
            weight.setwPurchase(BigDecimal.valueOf(0.11));
            weight.setbPurchase(BigDecimal.valueOf(0.017));
            weight.setwSellOut(BigDecimal.valueOf(2.36));
            weight.setbSellOut(BigDecimal.valueOf(0.005));
            StockStrategy stockStrategy = new SmartStockStrategy(SmartStockStrategy.RATE_TYPE_AVERAGE,
                    SmartStockStrategy.RATE_TYPE_NORMAL, weight);
            return stockStrategy;
        }
        stockTemp = new Stock("华东医药", "sz000963");
        if (stock.equals(stockTemp)) {
            SmartStockStrategy.RateWeight weight = new SmartStockStrategy.RateWeight();
            weight.setRateCount(3);
            weight.setwPurchase(BigDecimal.valueOf(0.7));
            weight.setbPurchase(BigDecimal.valueOf(0.010));
            weight.setwSellOut(BigDecimal.valueOf(0.8));
            weight.setbSellOut(BigDecimal.valueOf(0.010));
            StockStrategy stockStrategy = new SmartStockStrategy(SmartStockStrategy.RATE_TYPE_AVERAGE,
                    SmartStockStrategy.RATE_TYPE_NORMAL, weight);
            return stockStrategy;
        }

        return StockStrategy.DEFAULT;
    }
    public BigDecimal execMockStockTrade(StockAccount stockAccount, Stock stock, StockStrategy stockStrategy) {
        Map<Stock, StockTrade[]> stockMarket = mockStockTrade(stockAccount, stock, stockStrategy);
        Map<Stock, StockTrade> stockMarketNew = new HashMap<>();
        for (Map.Entry<Stock, StockTrade[]> entry : stockMarket.entrySet()) {
            StockTrade[] stockTrades = entry.getValue();
            stockMarketNew.put(entry.getKey(), stockTrades[stockTrades.length - 1]);
        }
        StockTrade[] stockTrades = stockMarket.get(stock);
        BigDecimal marketValue = stockAccount.useMarketValue(stockMarketNew);
        log(stockTrades[0].getTime() + "-" + stockTrades[stockTrades.length - 1].getTime() + "总资产:" + marketValue);
        return marketValue;
    }
    public Map<Stock, StockTrade[]> mockStockTrade(StockAccount stockAccount, Stock stock,
            StockStrategy stockStrategy) {
        Date date = DateUtils.weekDay(new Date(), 1);
        String deadline = DateUtils.formatDate(date, "yyMMdd");
        int rateCount = stockStrategy.getRateStrategy().useRateCount();
        try {
            List<StockTrade> stockTradeList = fetchStockTrade(stock.getCode(), null, deadline);
            Collections.sort(stockTradeList);
            List<StockDecision> sdList = null;
            LinkedList<StockTrade> list = new LinkedList<>();
            for (StockTrade stockTrade : stockTradeList) {
                if (sdList != null) {
                    for (StockDecision stockDecision : sdList) {
                        stockDecision.apply(stock, stockTrade, stockAccount);
                    }
                    // log(ValueUtils.toJSONString2(stockAccount));
                }
                list.add(stockTrade);
                if (list.size() == rateCount + 1) {
                    Long turnover = stockAccount.getHoldShares().get(stock);
                    sdList = stockStrategy.apply(list, turnover == null ? 0l : turnover, stockAccount.getFund());
                    list.removeFirst();
                }
            }
            Map<Stock, StockTrade[]> stockMarket = new HashMap<>();
            stockMarket.put(stock,
                    new StockTrade[] { stockTradeList.get(0), stockTradeList.get(stockTradeList.size() - 1) });
            return stockMarket;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Pair<Stock, List<StockDecision>>> stockStrategy(List<Stock> stocks, StockAccount stockAccount) {
        Date date = DateUtils.weekDay(new Date(), 1);
        String deadline = DateUtils.formatDate(date, "yyMMdd");
        log("deadline:"+deadline);
        List<Pair<Stock, List<StockDecision>>> stockStrategys = new ArrayList<>();
        for (Stock stock : stocks) {
            Pair<Stock, List<StockDecision>> stockStrategy = new Pair<>();
            stockStrategys.add(stockStrategy);
            stockStrategy.setV1(stock);
            try {
                List<StockTrade> stockTradeList = fetchStockTrade(stock.getCode(), null, deadline);
                Long turnover = stockAccount.getHoldShares().get(stock);
                List<StockDecision> strategys = useStockStrategy(stock).apply(stockTradeList, turnover,
                        BigDecimal.ZERO);
                stockStrategy.setV2(strategys);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stockStrategys;
    }
    private Map<String,String> stockCache=new HashMap<>();
    private List<StockTrade> fetchStockTrade(String stockCode, String startTime, String endTime)
            throws ClientProtocolException, IOException {
        String data=null;
        if(stockCache.containsKey(stockCode)){
            data=stockCache.get(stockCode);
        }else{
            data = HttpUtils.get("http://data.gtimg.cn/flashdata/hushen/latest/weekly/" + stockCode + ".js");
            stockCache.put(stockCode, data);
        }
        List<StockTrade> stockTradeList = new ArrayList<>();
        String[] lines = data.split("\n");
        for (int i = 2; i < lines.length - 2; i++) {
            StockTrade stockTrade = parseLine(lines[i]);
            if ((!StringUtils.hasText(startTime) || startTime.compareTo(stockTrade.getTime()) <= 0)
                    && (!StringUtils.hasText(endTime) || endTime.compareTo(stockTrade.getTime()) > 0)) {
                stockTradeList.add(stockTrade);
            }
        }
        return stockTradeList;
    }

    private StockTrade parseLine(String line) {
        line = line.substring(0, line.length() - 3);
        String[] values = line.split("\\s+");
        StockTrade stockTrade = new StockTrade();
        stockTrade.setTime(values[0]);
        stockTrade.setOpenPrice(new BigDecimal(values[1]));
        stockTrade.setClosePrice(new BigDecimal(values[2]));
        stockTrade.setMaxPrice(new BigDecimal(values[3]));
        stockTrade.setMinPrice(new BigDecimal(values[4]));
        stockTrade.setTurnover(Long.valueOf(values[5]));
        return stockTrade;
    }
}