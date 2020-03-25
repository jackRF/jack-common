package com.jd.drools.test;

import java.math.BigDecimal;
import java.util.Date;

public class Order{
    /**
	 * 交易渠道编号
	 */
    private String channelId;
    /**
	 * 金额
	 */
    private BigDecimal balance;
    /**
	 * 产品编号
	 */
    private String fundCode;
    /**
	 * 创立时间
	 */
    private Date createTime;
    /**
	 * 收益级别
	 */
	private String profitclass;
	/**
	 * 业务标识 01 购买 03赎回
	 */
    private String busiflag;
    /**
	 * 订单状态 1待上传附件,2待视频,3待付款,4取消,5订单超时,6已支付并发送TA,7待上传支付凭证,8已上传凭证并发送TA
	 */
    private String status;
    /**
	 * 线上付款：支付状态 0失败 1成功 2超时 3初始化4冲正成功5退款成功6部分成功
	 */
	private String payStatus;
	/**
	 * 支付类型：0线下，1线上, 2产品转换
	 */
	private String payType;
    /**
	 * 特殊产品标识 T+N：1
	 */
    private String reserve2;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getProfitclass() {
        return profitclass;
    }

    public void setProfitclass(String profitclass) {
        this.profitclass = profitclass;
    }

    public String getBusiflag() {
        return busiflag;
    }

    public void setBusiflag(String busiflag) {
        this.busiflag = busiflag;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getReserve2() {
        return reserve2;
    }

    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2;
    }
}