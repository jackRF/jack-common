package org.jack.common.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private static File stockDir=new File("./src/main/resources/stock");
    @Test
    public void testStock() {
        List<Stock> stocks = new ArrayList<>();
        Map<Stock, Long> holdShares = new HashMap<>();
        parseholdStockShares(new File(stockDir,"/持仓.txt"), holdShares, stocks);
        boolean train = true;
        int rateType1 = SmartStockStrategy.RATE_TYPE_AVERAGE;
        int rateType2 = SmartStockStrategy.RATE_TYPE_NORMAL;
        Date date = DateUtils.weekDay(new Date(), 1);
        String startTime=null;
        startTime=DateUtils.formatDate(DateUtils.addMonth(date, -12), "yyMMdd");
        String deadline = DateUtils.formatDate(date, "yyMMdd");
        StringBuilder text = new StringBuilder();
        if (train) {
            for (Stock stockItem : stocks) {
                List<StockTrade> stockTradeList = fetchStockTrade(stockItem.getCode(), startTime, deadline).getV1();
                Pair<SmartStockStrategy.RateWeight, Pair<StockTrade[], BigDecimal>> wPair = SmartStockStrategy.RateWeight
                        .trainStockTrade(stockItem, BigDecimal.valueOf(50000), rateType1, rateType2, stockTradeList,
                                "rateCount");
                swMap.put(stockItem, wPair.getV1());
                text.append(format(stockItem, wPair)).append("\n");
            }
        }
        List<Pair<Stock, List<StockDecision>>> stockDecisionPairList = new ArrayList<>();
        for (Stock stockItem : stocks) {
            Pair<List<StockTrade>, StockTrade> stockTradePair = fetchStockTrade(stockItem.getCode(), startTime, deadline);
            List<StockTrade> stockTradeList = stockTradePair.getV1();
            StockTrade newLast = stockTradePair.getV2();
            SmartStockStrategy smartStockStrategy = useStockStrategy(stockItem, rateType1, rateType2);
            if (!train) {
                StockAccount stockAccount = new StockAccount(BigDecimal.valueOf(50000));
                Pair<SmartStockStrategy.RateWeight, Pair<StockTrade[], BigDecimal>> wPair = StockTrade
                        .mockStockTrade(stockAccount, stockItem, smartStockStrategy, stockTradeList);
                text.append(format(stockItem, wPair)).append("\n");
            }
            StockAccount stockAccount = new StockAccount(BigDecimal.valueOf(20582.34), holdShares);
            Long turnover = stockAccount.getHoldShares().get(stockItem);
            List<StockDecision> stockDecisionList = smartStockStrategy.apply(stockTradeList, turnover,
                    stockAccount.getFund());
            if (newLast != null) {
                stockDecisionList = stockDecisionList.stream().map(sd -> {
                    return sd.copyApply(newLast);
                }).collect(Collectors.toList());
            }
            Pair<Stock, List<StockDecision>> stockDecisionPair = new Pair<>();
            stockDecisionPairList.add(stockDecisionPair);
            stockDecisionPair.setV1(stockItem);
            stockDecisionPair.setV2(stockDecisionList);
        }
        int hi = 0;
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
        File file = new File(stockDir,"/交易策略/" +(StringUtils.hasText(startTime)?startTime+"-"+deadline:deadline)+".txt");
        writeFile(text, file);
    }
    @Test
    public void testStockMarket() {
        List<Stock> stockList = new ArrayList<>();
        stockList.add(new Stock("君正集团", "sh601216"));
        stockList.add(new Stock("中国银河", "sh601881"));
        stockList.add(new Stock("百傲化学", "sh603360"));
        stockList.add(new Stock("世运电路", "sh603920"));
        stockList.add(new Stock("格力电器", "sz000651"));
        stockList.add(new Stock("华东医药", "sz000963"));
        stockList.add(new Stock("红旗连锁", "sz002697"));
        // stockList.add(new Stock("贵州茅台", "sh600519"));
        Map<String, Set<String>> useStockMarket = new HashMap<>();
        Set<String> use = Set.of("股票名字", "股票代码", "当前价格", "最高", "最低", "涨跌%", "换手率", "市盈率", "市净率", "流通市值", "总市值");
        useStockMarket.put("最新行情", use);
        Map<String, Map<String, List<String>>> diffMap = new HashMap<>();
        Map<String, Pair<String, List<Pair<String, List<String>>>>> classifyPairMap = new HashMap<>();
        List<Pair<String, List<Pair<String, List<String>>>>> diffList = new ArrayList<>();
        for (Stock stock : stockList) {
            try {
                List<Pair<String, List<Pair<String, String>>>> stockMarketList = fetchStockMarket(stock.getCode());
                processDiffStock(useStockMarket, diffList, diffMap, classifyPairMap, stockMarketList);
                // log(stockMarketList);
            } catch (Exception e) {
            }
        }
        logDiff(diffList);
        // selectStock("sh6", 0, 2000, stockList);
        // selectStock("sz0", 0, 3018, stockList);
        // log(ValueUtils.toJSONString(stockList));
        // List<Pair<String,List<Pair<String,String[]>>>>
        // classificationPairList=readClassification(new
        // File("./src/main/resources/stock/股票分类.txt"));
        // Map<String,List<Stock>> classificationMap=classifyStock(stockList,
        // classificationPairList);
        // String selectDir="./src/main/resources/stock/选股/";
        // writeClassifyStock(classificationMap, new File(selectDir,"市盈率20市值200.txt"));
    }
    @Test
    public void testStockTrade() {
        StockAccount stockAccount = new StockAccount(BigDecimal.valueOf(50000));
        Stock stock = new Stock("比亚迪", "sz002594");
        // Stock stock = new Stock("顺丰控股", "sz002352");
        // Stock stock = new Stock("贵州茅台", "sh600519");
        // Stock stock = new Stock("恒瑞医药", "sh600276");
        // Stock stock = new Stock("三七互娱", "sz002555");
        // Stock stock=new Stock("中天科技", "sh600522");
        // Stock stock = new Stock("君正集团", "sh601216");
        // Stock stock = new Stock("中国银河", "sh601881");
        // Stock stock=new Stock("百傲化学", "sh603360");
        // Stock stock = new Stock("世运电路", "sh603920");
        // Stock stock=new Stock("格力电器", "sz000651");
        // Stock stock = new Stock("华东医药", "sz000963");
        // Stock stock=new Stock("红旗连锁", "sz002697");
        
        Date date = DateUtils.weekDay(new Date(), 1);
        String startTime=null;
        startTime=DateUtils.formatDate(DateUtils.addMonth(date, -12), "yyMMdd");
        String endTime = DateUtils.formatDate(date, "yyMMdd");
        List<StockTrade> stockTradeList;
        stockTradeList = fetchStockTrade(stock.getCode(), startTime, endTime).getV1();
        Pair<SmartStockStrategy.RateWeight, Pair<StockTrade[], BigDecimal>> wPair = null;
        int rateType1 = SmartStockStrategy.RATE_TYPE_AVERAGE;
        int rateType2 = SmartStockStrategy.RATE_TYPE_NORMAL;
        boolean train = true;
        if (train) {
            wPair = SmartStockStrategy.RateWeight.trainStockTrade(stock, stockAccount.getFund(), rateType1, rateType2,
                    stockTradeList, "rateCount");
            swMap.put(stock, wPair.getV1());
        } else {
            SmartStockStrategy stockStrategy = useStockStrategy(stock, rateType1, rateType2);
            wPair = StockTrade.mockStockTrade(stockAccount, stock, stockStrategy, stockTradeList);
        }
        log(stock, wPair);
    }

    private void writeFile(Object message, File file) {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(message + "\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logDiff(List<Pair<String, List<Pair<String, List<String>>>>> diffList) {
        StringBuilder sb = new StringBuilder();
        for (Pair<String, List<Pair<String, List<String>>>> pair : diffList) {
            sb.append(pair.getV1()).append("\n");
            for (Pair<String, List<String>> entry : pair.getV2()) {
                sb.append(ValueUtils.rightPad(entry.getV1()+":", " ",
                        entry.getV1().length() + 13 - entry.getV1().getBytes().length));
                for (String stock : entry.getV2()) {
                    sb.append("\t").append(stock);
                }
                sb.append("\n");
            }
            sb.append("\n");
        }
        log(sb);
    }

    private void processDiffStock(Map<String, Set<String>> useStockMarket,
            List<Pair<String, List<Pair<String, List<String>>>>> diffList,
            Map<String, Map<String, List<String>>> diffMap,
            Map<String, Pair<String, List<Pair<String, List<String>>>>> classifyPairMap,
            List<Pair<String, List<Pair<String, String>>>> stockMarketList) {
        for (Pair<String, List<Pair<String, String>>> pair : stockMarketList) {
            String classify = pair.getV1();
            if (!useStockMarket.isEmpty() && !useStockMarket.containsKey(classify)) {
                continue;
            }
            Pair<String, List<Pair<String, List<String>>>> classifyPair = null;
            Map<String, List<String>> classifyMap = null;
            if (diffMap.containsKey(classify)) {
                classifyMap = diffMap.get(classify);
                classifyPair = classifyPairMap.get(classify);
            } else {
                classifyMap = new HashMap<String, List<String>>();
                diffMap.put(classify, classifyMap);
                classifyPair = new Pair<>(classify, new ArrayList<>());
                classifyPairMap.put(classify, classifyPair);
                diffList.add(classifyPair);
            }
            Set<String> keys = useStockMarket.get(classify);
            Set<String> used = new HashSet<>();
            for (Pair<String, String> kv : pair.getV2()) {
                if ((CollectionUtils.isEmpty(keys) || keys.contains(kv.getV1())) && used.add(kv.getV1())) {
                    List<String> sstocks = classifyMap.get(kv.getV1());
                    if (sstocks == null) {
                        sstocks = new ArrayList<>();
                        classifyMap.put(kv.getV1(), sstocks);
                        classifyPair.getV2().add(new Pair<>(kv.getV1(), sstocks));
                    }
                    sstocks.add(kv.getV2());
                }
            }
        }
    }

    private void writeClassifyStock(Map<String, List<Stock>> classificationMap, File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (Map.Entry<String, List<Stock>> entry : classificationMap.entrySet()) {
                writer.write(entry.getKey() + "\n");
                for (Stock stock : entry.getValue()) {
                    writer.write(stock.getName());
                    writer.write("\t");
                    writer.write(stock.getCode());
                    writer.write("\n");
                }
                writer.write("\n");
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, List<Stock>> classifyStock(List<Stock> stockList,
            List<Pair<String, List<Pair<String, String[]>>>> classificationPairList) {
        Map<String, List<Stock>> classificationMap = new HashMap<>();
        for (Stock stock : stockList) {
            String classification = useClassification(stock, classificationPairList);
            List<Stock> list = classificationMap.get(classification);
            if (list == null) {
                list = new ArrayList<>();
                classificationMap.put(classification, list);
            }
            list.add(stock);
        }
        return classificationMap;
    }

    private String useClassification(Stock stock,
            List<Pair<String, List<Pair<String, String[]>>>> classificationPairList) {
        String name = stock.getName();
        for (Pair<String, List<Pair<String, String[]>>> classificationPair : classificationPairList) {
            String classification = classificationPair.getV1();
            List<Pair<String, String[]>> conditionList = classificationPair.getV2();
            for (Pair<String, String[]> condition : conditionList) {
                String key = condition.getV1();
                String[] values = condition.getV2();
                if ("contains".equals(key)) {
                    for (String value : values) {
                        if (name.contains(value)) {
                            return classification;
                        }
                    }
                } else if ("special".equals(key)) {
                    if (ValueUtils.contains(name, values)) {
                        return classification;
                    }
                } else if ("endsWith".equals(key)) {
                    for (String value : values) {
                        if (name.endsWith(value)) {
                            return classification;
                        }
                    }
                }
            }
        }
        return "未知";
    }

    private List<Pair<String, List<Pair<String, String[]>>>> readClassification(File file) {
        List<Pair<String, List<Pair<String, String[]>>>> classificationPairList = new ArrayList<>();
        try {
            IOUtils.processText(file, new Task<String>() {
                private Pair<String, List<Pair<String, String[]>>> classificationPair;

                @Override
                public void toDo(String line) {
                    if (!StringUtils.hasText(line)) {
                        return;
                    }
                    line = line.trim();
                    if (!line.contains(":")) {
                        Pair<String, List<Pair<String, String[]>>> classificationPair = new Pair<>();
                        classificationPair.setV1(line);
                        classificationPair.setV2(new ArrayList<>());
                        classificationPairList.add(classificationPair);
                        this.classificationPair = classificationPair;
                    } else {
                        String[] condition = line.split(":");
                        classificationPair.getV2().add(new Pair<>(condition[0], condition[1].split("~")));
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classificationPairList;
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

    private String format(Stock stock, Pair<SmartStockStrategy.RateWeight, Pair<StockTrade[], BigDecimal>> wPair) {
        SmartStockStrategy.RateWeight weight = wPair.getV1();
        Pair<StockTrade[], BigDecimal> stPair = wPair.getV2();
        StockTrade[] sts = stPair.getV1();
        return "stock:" + ValueUtils.toJSONString(stock) + sts[0].getTime() + "-" + sts[1].getTime() + "总资产:"
                + stPair.getV2() + ", weight:" + ValueUtils.toJSONString(weight);
    }

    private void log(Stock stock, Pair<SmartStockStrategy.RateWeight, Pair<StockTrade[], BigDecimal>> wPair) {
        log(format(stock, wPair));
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

    private Pair<List<StockTrade>, StockTrade> fetchStockTrade(String stockCode, String startTime, String endTime) {
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
        StockTrade stockTradeLast = null;
        List<StockTrade> stockTradeList = new ArrayList<>();
        String[] lines = data.split("\n");
        for (int i = 2; i < lines.length - 1; i++) {
            StockTrade stockTrade = parseLine(lines[i]);
            if ((!StringUtils.hasText(startTime) || startTime.compareTo(stockTrade.getTime()) <= 0)
                    && (!StringUtils.hasText(endTime) || endTime.compareTo(stockTrade.getTime()) > 0)) {
                stockTradeList.add(stockTrade);
            }
            if (StringUtils.hasText(endTime) && endTime.compareTo(stockTrade.getTime()) <= 0) {
                stockTradeLast = stockTrade;
            }
        }
        return new Pair<>(stockTradeList, stockTradeLast);
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
    private static void parseholdStockShares(File file,Map<Stock, Long> holdShares,List<Stock> stocks){
        String text="";
        try {
            text = IOUtils.readText(file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String[] lines=text.split("\n");
        for(String line:lines){
            if(!StringUtils.hasText(line)){
                continue;
            }
            String[] values=line.split("\\s+");
            Stock stock=new Stock(values[0],values[1]);
            stocks.add(stock);
            Long turnover=0l;
            if(values.length>=3){
                turnover=Long.valueOf(values[2]);
            }
            holdShares.put(stock, turnover);
        }
    }
}