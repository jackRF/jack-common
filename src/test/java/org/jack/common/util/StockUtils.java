package org.jack.common.util;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.client.ClientProtocolException;
import org.jack.common.BaseTest;
import org.jack.common.core.Pair;
import org.jack.common.domain.SmartStockStrategy;
import org.jack.common.domain.Stock;
import org.jack.common.domain.StockAccount;
import org.jack.common.domain.StockDecision;
import org.jack.common.domain.StockTrade;
import org.junit.Test;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class StockUtils extends BaseTest {
    @Test
    public void testStock() {
        Map<Stock, Long> holdShares = new HashMap<>();
        Stock stock = null;
        List<Stock> stocks = new ArrayList<>();
        stock = new Stock("君正集团", "sh601216");
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
        holdShares.put(stock, 0l);
        stock = new Stock("格力电器", "sz000651");
        stocks.add(stock);
        holdShares.put(stock, 300l);
        stock = new Stock("华东医药", "sz000963");
        stocks.add(stock);
        holdShares.put(stock, 400l);
        stock = new Stock("红旗连锁", "sz002697");
        stocks.add(stock);
        holdShares.put(stock, 200l);
        boolean train = true;
        int rateType1 = SmartStockStrategy.RATE_TYPE_AVERAGE;
        int rateType2 = SmartStockStrategy.RATE_TYPE_NORMAL;
        Date date = DateUtils.weekDay(new Date(), 1);
        String deadline = DateUtils.formatDate(date, "yyMMdd");
        if (train) {
            for (Stock stockItem : stocks) {
                List<StockTrade> stockTradeList = fetchStockTrade(stockItem.getCode(), null, deadline).getV1();
                Pair<SmartStockStrategy.RateWeight, Pair<StockTrade[], BigDecimal>> wPair = SmartStockStrategy.RateWeight
                        .trainStockTrade(stockItem, BigDecimal.valueOf(50000), rateType1, rateType2, stockTradeList,
                                "rateCount");
                swMap.put(stockItem, wPair.getV1());
                log(stockItem, wPair);
            }
        }
        List<Pair<Stock, List<StockDecision>>> stockDecisionPairList = new ArrayList<>();
        for (Stock stockItem : stocks) {
            Pair<List<StockTrade>,StockTrade> stockTradePair = fetchStockTrade(stockItem.getCode(), null, deadline);
            List<StockTrade> stockTradeList=stockTradePair.getV1();
            StockTrade newLast=stockTradePair.getV2();
            SmartStockStrategy smartStockStrategy = useStockStrategy(stockItem, rateType1, rateType2);
            if (!train) {
                StockAccount stockAccount = new StockAccount(BigDecimal.valueOf(50000));
                Pair<SmartStockStrategy.RateWeight, Pair<StockTrade[], BigDecimal>> wPair = StockTrade
                        .mockStockTrade(stockAccount, stockItem, smartStockStrategy, stockTradeList);
                log(stockItem, wPair);
            }
            StockAccount stockAccount = new StockAccount(BigDecimal.valueOf(12000), holdShares);
            Long turnover = stockAccount.getHoldShares().get(stockItem);
            List<StockDecision> stockDecisionList = smartStockStrategy.apply(stockTradeList, turnover,
                    stockAccount.getFund());
            if(newLast!=null){
                stockDecisionList=stockDecisionList.stream().map(sd->{return sd.copyApply(newLast);}).collect(Collectors.toList());
            }
            Pair<Stock, List<StockDecision>> stockDecisionPair = new Pair<>();
            stockDecisionPairList.add(stockDecisionPair);
            stockDecisionPair.setV1(stockItem);
            stockDecisionPair.setV2(stockDecisionList);
        }
        log("deadline:" + deadline);
        int hi = 0;
        StringBuilder text = new StringBuilder();
        String[] head = { "股票名称", "股票代码", "\t买入1", "买入数量1", "买入收益率1", "卖出1", "卖出数量1", "卖出收益率1", "买入2", "买入数量2",
                "买入收益率2", "卖出2", "卖出数量2", "卖出收益率2" };
        for (String h : head) {
            if (hi++ == 0) {
                text.append(h);
            } else {
                text.append("\t").append(h);
            }
        }
        text.append("\n");
        for (Pair<Stock, List<StockDecision>> pair : stockDecisionPairList) {
            Stock stockItem = pair.getV1();
            text.append(stockItem.getName()).append("\t").append(stockItem.getCode());
            List<StockDecision> v2 = pair.getV2();
            for (StockDecision sd : v2) {
                text.append("\t\t").append(sd.getPurchasePrice().setScale(2, RoundingMode.FLOOR));
                text.append("\t").append(sd.getPurchaseTurnover());
                text.append("\t\t\t").append(
                        BigDecimal.valueOf(sd.getRelativePurchaseRate() * 100).setScale(2, RoundingMode.HALF_UP) + "%");
                text.append("\t\t").append(sd.getSellOutPrice().setScale(2, RoundingMode.CEILING));
                text.append("\t").append(sd.getSellOutTurnover());
                text.append("\t\t\t").append(
                        BigDecimal.valueOf(sd.getRelativeSellOutRate() * 100).setScale(2, RoundingMode.HALF_UP) + "%");
            }
            text.append("\n");
        }
        log(text);
    }

    @Test
    public void testStockTrade() {
        StockAccount stockAccount = new StockAccount(BigDecimal.valueOf(50000));
        // Stock stock = new Stock("君正集团", "sh601216");
        // Stock stock=new Stock("中国银河", "sh601881");
        // Stock stock=new Stock("百傲化学", "sh603360");
        Stock stock=new Stock("世运电路", "sh603920");
        // Stock stock=new Stock("格力电器", "sz000651");
        // Stock stock = new Stock("华东医药", "sz000963");
        // Stock stock=new Stock("红旗连锁", "sz002697");
        Date date = DateUtils.weekDay(new Date(), 1);
        String endTime = DateUtils.formatDate(date, "yyMMdd");
        List<StockTrade> stockTradeList;
        stockTradeList = fetchStockTrade(stock.getCode(), null, endTime).getV1();
        Pair<SmartStockStrategy.RateWeight, Pair<StockTrade[], BigDecimal>> wPair = null;
        int rateType1 = SmartStockStrategy.RATE_TYPE_AVERAGE;
        int rateType2 = SmartStockStrategy.RATE_TYPE_NORMAL;
        boolean train = false;
        if (train) {
            wPair = SmartStockStrategy.RateWeight.trainStockTrade(stock, BigDecimal.valueOf(50000), rateType1,
                    rateType2, stockTradeList, "rateCount");
            swMap.put(stock, wPair.getV1());
        } else {
            SmartStockStrategy stockStrategy = useStockStrategy(stock, rateType1, rateType2);
            wPair = StockTrade.mockStockTrade(stockAccount, stock, stockStrategy, stockTradeList);
        }
        log(stock, wPair);
    }

    @Test
    public void testStockMarket() {
        // Stock stock = new Stock("君正集团", "sh601216");
        // Stock stock=new Stock("中国银河", "sh601881");
        // Stock stock=new Stock("百傲化学", "sh603360");
        Stock stock=new Stock("世运电路", "sh603920");
        // Stock stock = new Stock("格力电器", "sz000651");
        // Stock stock = new Stock("华东医药", "sz000963");

        // Stock stock = new Stock("贵州茅台", "sh600519");
        try {
            List<Pair<String, List<Pair<String, String>>>> stockMarketList = fetchStockMarket(stock.getCode());
            log(stockMarketList);
        } catch (Exception e) {
        }
        List<Stock> stockList = new ArrayList<>();
        // selectStock("sh6", 0, 2000, stockList);
        // selectStock("sz0", 0, 3018, stockList);
        log(ValueUtils.toJSONString(stockList));
    }

    private void selectStock(String shOrSz, int start, int end, List<Stock> stockList) {
        for (int i = start; i < end; i++) {
            String stockCode = shOrSz + ValueUtils.leftPad(i + "", "0", 5);
            Stock stock = new Stock("", stockCode);
            try {
                List<Pair<String, List<Pair<String, String>>>> stockMarketList = fetchStockMarket(stock.getCode());
                if (CollectionUtils.isEmpty(stockMarketList)) {
                    continue;
                }
                if (Stock.filter(stock, stockMarketList)) {
                    stockList.add(stock);
                }
            } catch (Exception e) {
                log(stockCode);

            }
        }
    }

    private Map<Stock, SmartStockStrategy.RateWeight> swMap = new HashMap<>();

    private SmartStockStrategy useStockStrategy(Stock stock, int rateType1, int rateType2) {
        SmartStockStrategy.RateWeight weight = new SmartStockStrategy.RateWeight();
        weight.setRateCount(3);
        weight.setCat(3d);
        weight.setwPurchase(BigDecimal.valueOf(0.11));
        weight.setbPurchase(BigDecimal.valueOf(0.017));
        weight.setwSellOut(BigDecimal.valueOf(2.36));
        weight.setbSellOut(BigDecimal.valueOf(0.005));
        Stock stockTemp = new Stock("格力电器", "sz000651");
        if (!swMap.containsKey(stockTemp)) {
            swMap.put(stockTemp, weight);
        }
        weight = new SmartStockStrategy.RateWeight();
        weight.setRateCount(3);
        weight.setCat(3d);
        weight.setwPurchase(BigDecimal.valueOf(0.7));
        weight.setbPurchase(BigDecimal.valueOf(0.010));
        weight.setwSellOut(BigDecimal.valueOf(0.8));
        weight.setbSellOut(BigDecimal.valueOf(0.010));
        stockTemp = new Stock("华东医药", "sz000963");
        if (!swMap.containsKey(stockTemp)) {
            swMap.put(stockTemp, weight);
        }
        if (swMap.containsKey(stock)) {
            return new SmartStockStrategy(rateType1, rateType2, swMap.get(stock));
        }
        return SmartStockStrategy.RATEAVERAGE_STOCKSTRATEGY;
    }

    private void log(List<Pair<String, List<Pair<String, String>>>> stockMarketList) {
        StringBuilder info = new StringBuilder();
        for (Pair<String, List<Pair<String, String>>> stockMarket : stockMarketList) {
            info.append(stockMarket.getV1()).append("\n");
            List<Pair<String, String>> entryList = stockMarket.getV2();
            for (Pair<String, String> entry : entryList) {
                info.append(entry.getV1() + ":" + entry.getV2()).append("\n");
            }
            info.append("\n");
        }
        log(info);
    }

    private void log(Stock stock, Pair<SmartStockStrategy.RateWeight, Pair<StockTrade[], BigDecimal>> wPair) {
        SmartStockStrategy.RateWeight weight = wPair.getV1();
        Pair<StockTrade[], BigDecimal> stPair = wPair.getV2();
        StockTrade[] sts = stPair.getV1();
        log("stock:" + ValueUtils.toJSONString(stock) + sts[0].getTime() + "-" + sts[1].getTime() + "总资产:"
                + stPair.getV2() + ", weight:" + ValueUtils.toJSONString(weight));
    }

    private List<Pair<String, List<Pair<String, String>>>> fetchStockMarket(String stockCode)
            throws ClientProtocolException, IOException {
        List<Pair<String, String>> apiList = new ArrayList<>();
        apiList.add(new Pair<>("最新行情", "http://qt.gtimg.cn/q="));
        apiList.add(new Pair<>("实时资金流向", "http://qt.gtimg.cn/q=ff_"));
        apiList.add(new Pair<>("盘口分析", "http://qt.gtimg.cn/q=s_pk"));
        apiList.add(new Pair<>("简要信息", "http://qt.gtimg.cn/q=s_"));
        List<Pair<String, List<Pair<String, String>>>> stockMarketList = new ArrayList<>();
        for (Pair<String, String> pair : apiList) {
            String data = HttpUtils.get(pair.getV2() + stockCode);
            data = trimData(data);
            String[] values = data.split("~");
            String text = IOUtils.readText(new File("./src/main/resources/stock", pair.getV1() + ".txt"));
            String[] lines = text.split("\n");
            Pair<String, List<Pair<String, String>>> stockMarket = new Pair<>();
            stockMarket.setV1(pair.getV1());
            List<Pair<String, String>> entryList = new ArrayList<>();
            stockMarket.setV2(entryList);
            stockMarketList.add(stockMarket);
            for (String line : lines) {
                if (!StringUtils.hasText(line)) {
                    continue;
                }
                String entry[] = line.split("\\s*:\\s*");
                if (entry.length < 2 || !StringUtils.hasText(entry[1]) || entry[1].contains("未知")) {
                    continue;
                }
                String index = entry[0].trim();
                int ih = index.indexOf("-");
                if (ih > 0) {
                    int[] range = { Integer.valueOf(index.substring(0, ih)), Integer.valueOf(index.substring(ih + 1)) };
                    String[] rangeName = "二 三 四 五".split(" ");
                    int in = 0;
                    if (entry[1].contains("买二") && entry[1].contains("买五")) {
                        for (int i = range[0]; i <= range[1]; i += 2) {
                            entryList.add(new Pair<>("买" + rangeName[in], values[i]));
                            entryList.add(new Pair<>("买" + rangeName[in++] + "量（手）", values[i + 1]));
                        }
                    } else if (entry[1].contains("卖二") && entry[1].contains("卖五")) {
                        for (int i = range[0]; i <= range[1]; i += 2) {
                            entryList.add(new Pair<>("卖" + rangeName[in], values[i]));
                            entryList.add(new Pair<>("卖" + rangeName[in++] + "量", values[i + 1]));
                        }
                    }
                } else {
                    entryList.add(new Pair<>(entry[1], values[Integer.valueOf(index)]));
                }
            }
        }
        return stockMarketList;
    }

    private String trimData(String data) {
        int i = data.indexOf("\"");
        if (i >= 0) {
            data = data.substring(i + 1, data.indexOf("\"", i + 1));
        }
        return data;
    }

    private Map<String, String> stockCache = new HashMap<>();

    private Pair<List<StockTrade>,StockTrade> fetchStockTrade(String stockCode, String startTime, String endTime) {
        String data = null;
        if (stockCache.containsKey(stockCode)) {
            data = stockCache.get(stockCode);
        } else {
            try {
                data = HttpUtils.get("http://data.gtimg.cn/flashdata/hushen/latest/weekly/" + stockCode + ".js");
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            stockCache.put(stockCode, data);
        }
        StockTrade stockTradeLast=null;
        List<StockTrade> stockTradeList = new ArrayList<>();
        String[] lines = data.split("\n");
        for (int i = 2; i < lines.length - 2; i++) {
            StockTrade stockTrade = parseLine(lines[i]);
            if ((!StringUtils.hasText(startTime) || startTime.compareTo(stockTrade.getTime()) <= 0)
                    && (!StringUtils.hasText(endTime) || endTime.compareTo(stockTrade.getTime()) > 0)) {
                stockTradeList.add(stockTrade);
            }
            if(StringUtils.hasText(endTime)&&endTime.compareTo(stockTrade.getTime())<=0){
                stockTradeLast=stockTrade;
            }
        }
        return new Pair<>(stockTradeList,stockTradeLast);
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