package org.jack.common.domain;

import java.math.BigDecimal;

public class StockDecision {
    /**
     * 买入价
     */
    private BigDecimal purchasePrice;
    /**
     * 卖出价
     */
    private BigDecimal sellOutPrice;
    /**
     * 数量
     */
    private Long turnover; 
    /**
     * 相对收益率
     */
    private Double relativeYield;

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getSellOutPrice() {
        return sellOutPrice;
    }

    public void setSellOutPrice(BigDecimal sellOutPrice) {
        this.sellOutPrice = sellOutPrice;
    }

    public Long getTurnover() {
        return turnover;
    }

    public void setTurnover(Long turnover) {
        this.turnover = turnover;
    }

    public Double getRelativeYield() {
        return relativeYield;
    }

    public void setRelativeYield(Double relativeYield) {
        this.relativeYield = relativeYield;
    }
}
