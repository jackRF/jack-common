package org.jack.common.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.jack.common.BaseTest;
import org.jack.common.core.Pair;
import org.jack.common.domain.Stock;
import org.jack.common.domain.StockAccount;
import org.jack.common.domain.StockDecision;
import org.jack.common.domain.StockTrade;
import org.junit.Test;
import org.springframework.util.StringUtils;

public class StockUtils extends BaseTest {
    @Test
    public void testStock() {
        List<Pair<String[], Long>> stocks = new ArrayList<>();
        stocks.add(new Pair<>(new String[] { "君正集团", "sh601216" }, 400l));
        stocks.add(new Pair<>(new String[] { "中国银河", "sh601881" }, 200l));
        stocks.add(new Pair<>(new String[] { "百傲化学", "sh603360" }, 100l));
        stocks.add(new Pair<>(new String[] { "世运电路", "sh603920" }, 100l));
        stocks.add(new Pair<>(new String[] { "格力电器", "sz000651" }, 300l));
        stocks.add(new Pair<>(new String[] { "华东医药", "sz000963" }, 600l));
        stocks.add(new Pair<>(new String[] { "红旗连锁", "sz002697" }, 300l));
        List<Pair<String[], List<StockDecision>>> stockStrategys = stockStrategy(stocks);
        int hi = 0;
        StringBuilder text = new StringBuilder();
        String[] head = { "股票名称", "股票代码", "\t买入1", "卖出1", "收益率1", "交易量1", "买入2", "卖出2", "收益率2", "交易量2" };
        for (String h : head) {
            if (hi++ == 0) {
                text.append(h);
            } else {
                text.append("\t").append(h);
            }
        }
        text.append("\n");
        for (Pair<String[], List<StockDecision>> pair : stockStrategys) {
            String[] v1 = pair.getV1();
            text.append(v1[0]).append("\t").append(v1[1]);
            List<StockDecision> v2 = pair.getV2();
            for (StockDecision sd : v2) {
                text.append("\t\t").append(sd.getPurchasePrice().setScale(2, RoundingMode.FLOOR));
                text.append("\t").append(sd.getSellOutPrice().setScale(2, RoundingMode.CEILING));
                text.append("\t").append(
                        BigDecimal.valueOf(sd.getRelativeYield() * 100).setScale(2, RoundingMode.HALF_UP) + "%");
                text.append("\t").append(sd.getTurnover());
            }
            text.append("\n");
        }
        log(text);
        // log(ValueUtils.toJSONString(stockStrategys));
    }
    @Test
    public void mockStockTrade(){
        StockAccount stockAccount=new StockAccount(BigDecimal.valueOf(50000));
        // stocks.add(new Pair<>(new String[] { "君正集团", "sh601216" }, 400l));
        // stocks.add(new Pair<>(new String[] { "中国银河", "sh601881" }, 200l));
        // stocks.add(new Pair<>(new String[] { "百傲化学", "sh603360" }, 100l));
        // stocks.add(new Pair<>(new String[] { "世运电路", "sh603920" }, 100l));
        // stocks.add(new Pair<>(new String[] { "格力电器", "sz000651" }, 300l));
        // stocks.add(new Pair<>(new String[] { "华东医药", "sz000963" }, 600l));
        // stocks.add(new Pair<>(new String[] { "红旗连锁", "sz002697" }, 300l));
        // Stock stock=new Stock("君正集团", "sh601216");
        // Stock stock=new Stock("中国银河", "sh601881");
        // Stock stock=new Stock("百傲化学", "sh603360");
        // Stock stock=new Stock("世运电路", "sh603920");
        // Stock stock=new Stock("格力电器", "sz000651");
        Stock stock=new Stock("华东医药", "sz000963");
        // Stock stock=new Stock("红旗连锁", "sz002697");
        
        try {
            List<StockTrade> stockTradeList = fetchStockTrade(stock.getCode(), "201005");
            Collections.sort(stockTradeList);
            List<StockDecision> sdList=null;
            LinkedList<StockTrade> list=new LinkedList<>();
            for(StockTrade stockTrade:stockTradeList){
                if(sdList!=null){
                    for(StockDecision stockDecision:sdList){
                        stockDecision.apply(stock, stockTrade, stockAccount);
                    }
                    log(ValueUtils.toJSONString2(stockAccount));
                }
                list.add(stockTrade);
                if(list.size()==4){
                    Long turnover=stockAccount.getHoldShares().get(stock);
                    sdList=stockStrategy(list,turnover==null?0l:turnover);
                    list.removeFirst();
                }
            }
            Map<Stock,StockTrade> stockMarket=new HashMap<>();
            stockMarket.put(stock, stockTradeList.get(stockTradeList.size()-1));
            log(stockTradeList.get(0).getTime()+"-"+stockTradeList.get(stockTradeList.size()-1).getTime()+"总资产:"+stockAccount.useMarketValue(stockMarket));
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Pair<String[], List<StockDecision>>> stockStrategy(List<Pair<String[], Long>> stocks) {
        List<Pair<String[], List<StockDecision>>> stockStrategys = new ArrayList<>();
        for (Pair<String[], Long> stock : stocks) {
            Pair<String[], List<StockDecision>> stockStrategy = new Pair<>();
            stockStrategy.setV1(stock.getV1());
            stockStrategy.setV2(stockStrategy(stock.getV1()[1], stock.getV2()));
            stockStrategys.add(stockStrategy);
        }
        return stockStrategys;
    }

    private List<StockDecision> stockStrategy(String stockCode, Long turnover) {
        try {
            List<StockTrade> stockTradeList=fetchStockTrade(stockCode, "201005");
            // if(stockCode.equals("sz002697")){
            // log(ValueUtils.toJSONString(stockTradeList));
            // }
            List<StockDecision> strategys = stockStrategy(stockTradeList, turnover);
            return strategys;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<StockTrade> fetchStockTrade(String stockCode, String deadline)
            throws ClientProtocolException, IOException {
        String data = HttpUtils.get("http://data.gtimg.cn/flashdata/hushen/latest/weekly/" + stockCode + ".js");
        // log(data);
        List<StockTrade> stockTradeList = new ArrayList<>();
        String[] lines = data.split("\n");
        for (int i = 2; i < lines.length - 2; i++) {
            StockTrade stockTrade = parseLine(lines[i]);
            if (!StringUtils.hasText(deadline)||deadline.compareTo(stockTrade.getTime()) > 0) {
                stockTradeList.add(stockTrade);
            }
        }
        return stockTradeList;
    }

    private List<StockDecision> stockStrategy(List<StockTrade> data, Long turnover) {
        Collections.sort(data);
        Collections.reverse(data);
        int useRate = 3;
        int rc = 0;
        BigDecimal rateAbs = BigDecimal.ZERO;
        BigDecimal rate = null;
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
                // BigDecimal lastOpenPrice=last.getOpenPrice();
                BigDecimal lastClosePrice=last.getClosePrice();
                BigDecimal closePrice = stockTrade.getClosePrice();
                last = stockTrade;
                BigDecimal currentRate = lastClosePrice.subtract(closePrice).divide(closePrice, 4,
                        RoundingMode.HALF_UP);
                rateAbs = rateAbs.add(currentRate.abs());
                rc++;
                if (rc == useRate) {
                    rate = first.getClosePrice().subtract(closePrice).divide(closePrice, 4, RoundingMode.HALF_UP);
                    break;
                }
            }
        }
        BigDecimal firstOpenPrice=last.getOpenPrice();
        BigDecimal maxRate = max.subtract(firstOpenPrice).divide(firstOpenPrice, 4,RoundingMode.HALF_UP);
        BigDecimal minRate = min.subtract(firstOpenPrice).divide(firstOpenPrice, 4,RoundingMode.HALF_UP);
        // BigDecimal fRate=maxRate.abs().add(minRate.abs()).multiply(BigDecimal.valueOf(0.5));
        // rateAbs=rateAbs.add(fRate);
        rateAbs = rateAbs.divide(BigDecimal.valueOf(rc), 4, RoundingMode.HALF_UP).add(BigDecimal.valueOf(0.005));
        BigDecimal purchaseRate=minRate.abs().multiply(BigDecimal.valueOf(0.35));
        BigDecimal sellOutRate=maxRate.abs().multiply(BigDecimal.valueOf(0.85));
        // log(BigDecimal.valueOf(100).multiply(rateAbs)+"%");
        // log(BigDecimal.valueOf(100).multiply(rate)+"%");
        List<StockDecision> list = new ArrayList<>();
        StockDecision stockDecision = new StockDecision();
        stockDecision.setPurchasePrice(BigDecimal.valueOf(1).subtract(purchaseRate).multiply(first.getClosePrice()));
        stockDecision.setSellOutPrice(BigDecimal.valueOf(1).add(sellOutRate).multiply(first.getClosePrice()));
        stockDecision.setRelativeYield(rateAbs.doubleValue());
        stockDecision.setTurnover(useTurnover(turnover, 3));
        list.add(stockDecision);
        StockDecision stockDecision2 = new StockDecision();
        stockDecision2.setPurchasePrice(
                BigDecimal.valueOf(1).subtract(rateAbs.add(rate.abs())).multiply(first.getClosePrice()));
        stockDecision2
                .setSellOutPrice(BigDecimal.valueOf(1).add(rateAbs.add(rate.abs())).multiply(first.getClosePrice()));
        stockDecision2.setRelativeYield(rateAbs.add(rate.abs()).doubleValue());
        stockDecision2.setTurnover(useTurnover(turnover - stockDecision.getTurnover(), 3));
        list.add(stockDecision2);
        return list;
    }

    private long useTurnover(long turnover, int cat) {
        if (turnover <= 0) {
            return 0;
        }
        int use = BigDecimal.valueOf(turnover).divide(BigDecimal.valueOf(cat * 100), 0, RoundingMode.HALF_UP).intValue()
                * 100;
        if (use == 0) {
            return 100;
        }
        return use;
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