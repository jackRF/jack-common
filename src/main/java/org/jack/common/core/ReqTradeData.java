package org.jack.common.core;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jack.common.util.EncryptUtils;
import org.springframework.util.StringUtils;

/**
 * 抽象的请求交易类
 */
public abstract class ReqTradeData extends TradeData {
    /**
     * 时间戳
     */
    private String timestamp;
    /**
     * 签名
     */
    private String sign;
    /**
     * 验证
     */
    public Result<Void> vaildate(){
        List<String> messages=new ArrayList<String>();
        if(!StringUtils.hasText(timestamp)){
            messages.add("时间戳不能为空！");
        }
        if(!StringUtils.hasText(sign)){
            messages.add("签名不能为空！");
        }
        vaildate(messages);
        if(!messages.isEmpty()){
            return Result.fail(messages.toString());
        }
        return Result.SUCCESS;
    }
    public Result<Void> vaildate(String publicKey){
        Result<Void> r=vaildate();
        if(!r.isSuccess()){
            return r;
        }
        Map<String,Object> orderMap=new TreeMap<String,Object>();
        orderMap.put("timestamp", timestamp);
        this.applyForSign(orderMap);
        if(!EncryptUtils.verify(publicKey, EncryptUtils.buildForSign(orderMap), sign)){
            return Result.fail("验证签名失败！");
        }
        return Result.SUCCESS;
    }
    @Override
    protected boolean filter(PropertyDescriptor pd){
        Method  method =pd.getReadMethod();
        if(method==null
        ||Object.class.equals(method.getDeclaringClass())
        ||ReqTradeData.class.equals(method.getDeclaringClass()) ){
            return false;
        }
        return true;
    }
    /**
     * 验证
     * @param messages
     * @return
     */
    protected abstract boolean vaildate(List<String> messages);
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "sign=" + sign + ", timestamp=" + timestamp;
    }
}