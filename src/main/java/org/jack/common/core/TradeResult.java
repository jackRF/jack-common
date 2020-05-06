package org.jack.common.core;

import java.util.Map;
/**
 * 交易结果
 * @param <D>
 */
public class TradeResult<D> extends Result<D> {
    /**
     * 签名
     */
    private String sign;
    public TradeResult(){
        super();
    }
    public TradeResult(D data){
        this();
        this.setData(data);
    }
    public TradeResult(String retCode,String retMsg){
        super(retCode, retMsg);
    }
    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
    public void applyForSign(Map<String, Object> map) {
        map.put("code", getCode());
        map.put("message", getMessage());
        map.put("data", TradeData.convertValue(super.getData()));
    }
    public static <D> TradeResult<D> success(D data){
        return new TradeResult<D>(data);
    }
    public static <D> TradeResult<D> fail(String message){
		return new TradeResult<D>(Result.CODE_FAIL,message);
	}
    public static <D> TradeResult<D> from(Result<D> result){
        TradeResult<D> tradeResult=new TradeResult<D>(result.getCode(),result.getMessage());
        tradeResult.setData(result.getData());
        return tradeResult;
    }
}