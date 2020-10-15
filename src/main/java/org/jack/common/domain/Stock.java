package org.jack.common.domain;

import java.math.BigDecimal;
import java.util.List;

import org.jack.common.core.Pair;

public class Stock {
    /**
     * 股票名称
     */
    private String name;
    /**
     * 股票代码
     */
    private String code;

    public static boolean filter(Stock stock, List<Pair<String, List<Pair<String, String>>>> stockMarketList) {
        Pair<String, List<Pair<String, String>>> stockMarket0 = stockMarketList.get(0);
        for (Pair<String, String> entry : stockMarket0.getV2()) {
            String value = entry.getV2();
            if ("股票名字".equals(entry.getV1())) {
                stock.setName(value);
                if (value.contains("*ST")) {
                    return false;
                }
            }
            if ("总市值".equals(entry.getV1())) {
                if (BigDecimal.valueOf(Double.valueOf(value)).compareTo(BigDecimal.valueOf(500)) < 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public Stock(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Stock other = (Stock) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        return true;
    }

}
