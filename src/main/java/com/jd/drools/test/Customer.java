package com.jd.drools.test;

public class Customer{
    /**
     * 渠道编号
     */
    private String channelId;
    /**
	 * 客户姓名
	 */
    private String custName;
    /**
     * 性别    custType为1必填 （仅自然人填写）
     */
    private String sex;
    /**
	 * "证件类型:0-身份证,1-中国护照,2-军官证,3-士兵证,4-回乡证<br/>
	 * 5-户口本,6-外籍护照,7-其他,8-文职,9-警官A-港澳通行证,B-居住证,C-社保卡"
	 */
	private String identityType;
	/**
	 * 证件号码
	 */
    private String identityNo;
    /**
	 * 绑定手机   	isOpenNettrade为1必填
	 */
    private String bindMobile;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public String getIdentityNo() {
        return identityNo;
    }

    public void setIdentityNo(String identityNo) {
        this.identityNo = identityNo;
    }

    public String getBindMobile() {
        return bindMobile;
    }

    public void setBindMobile(String bindMobile) {
        this.bindMobile = bindMobile;
    }
    
}