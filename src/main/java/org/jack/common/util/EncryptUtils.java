package org.jack.common.util;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class EncryptUtils {
    private static final Logger logger=LoggerFactory.getLogger(EncryptUtils.class);
    /**
     * 验证签名
     * 
     * @param publicKey
     * @param text
     * @param sign
     * @return
     */
    public static boolean verify(String publicKey, String text, String sign) {
        try {
            return RSAUtils.verify(publicKey, text.getBytes("utf-8"), sign);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("publicKey:"+publicKey);
        logger.info("text:"+text);
        logger.info("sign:"+sign);
        return false;
    }
    /**
     * 签名
     * @param privateKey 私钥
     * @param content 内容
     * @return
     * @throws Exception
     */
    public static String sign(String privateKey, String content)throws Exception {
        return RSAUtils.sign(privateKey, content.getBytes("utf-8"));
    }
    /**
     * 签名
     * @param privateKey 私钥
     * @param orderMap 排序的Map
     * @return
     * @throws Exception
     */
    public static String sign(String privateKey, Map<String, Object> orderMap)throws Exception {
        return sign(privateKey, buildForSign(orderMap));
    }

    @SuppressWarnings("unchecked")
    public static String buildForSign(Map<String,Object> orderMap){
        int i=0;
        StringBuilder sb=new StringBuilder();
        for(Map.Entry<String,Object> entry:orderMap.entrySet()){
            String key=entry.getKey();
            Object value=entry.getValue();
            if(value==null||!StringUtils.hasText(key)){
                continue;
            }
            if(++i>1){
                sb.append("&");
            }
            sb.append(key).append("=");
            if(value instanceof Collection<?>){
                buildJSON((Collection<?>)value, sb);
            }else if(value instanceof Object[]){
                buildJSON((Object[])value,sb);
            }else if(value instanceof Map){
                buildJSON((Map<String,Object>) value, sb);
            }else{
                sb.append(value);
            }
        }
        return sb.toString();
    }
    private static void buildJSON(Map<String,Object> orderMap,StringBuilder sb){
        int i=0;
        sb.append("{");
        for(Map.Entry<String,Object> entry:orderMap.entrySet()){
            String key=entry.getKey();
            Object value=entry.getValue();
            if(value==null||!StringUtils.hasText(key)){
                continue;
            }
            if(++i>1){
                sb.append(",");
            }
            sb.append("\"").append(key).append("\"").append(":");
            buildJSON(value,sb);
        }
        sb.append("}");
    }
    private static void buildJSON(Collection<?> values,StringBuilder sb){
        sb.append("[");
        int i=0;
        for(Object item:values){
            if(++i>1){
                sb.append(",");
            }
            buildJSON(item,sb);
        }
        sb.append("]");
    }
    private static void buildJSON(Object[] values,StringBuilder sb){
        sb.append("[");
        int i=0;
        for(Object item:values){
            if(++i>1){
                sb.append(",");
            }
            buildJSON(item,sb);
        }
        sb.append("]");
    }
    @SuppressWarnings("unchecked")
    private static void buildJSON(Object value,StringBuilder sb){
        if(value==null){
            sb.append("null");
        }else if(value instanceof Number){
            sb.append(value);
        }else if(value instanceof Date){
            sb.append("\"").append(DateUtils.formatDate((Date)value, DateUtils.DATE_FORMAT_DATETIME)).append("\"");
        }else if(value instanceof Object[]){
            buildJSON((Object[])value,sb);
        }else if(value instanceof Collection<?>){
            buildJSON((Collection<?>)value, sb);
        }else if(value instanceof Map){
            buildJSON((Map<String,Object>)value, sb);
        }else{
            sb.append("\"").append(value).append("\"");
        }
    }
    /**
     * RSA私钥解密
     * 
     * @param privateKey
     * @param encryptText
     * @return
     */
    public static String privateKeyDecryptRSA(String privateKey, String encryptText) throws Exception{
        byte[] plainData = RSAUtils.decrypt(RSAUtils.loadPrivateKeyByStr(privateKey), encryptText);
        return new String(plainData, "utf-8");
    }
    /**
     * RSA公钥解密
     * 
     * @param publicKey
     * @param encryptText
     * @return
     */
    public static String publicKeyDecryptRSA(String publicKey, String encryptText) throws Exception{
        byte[] plainData = RSAUtils.decrypt(RSAUtils.loadPublicKeyByStr(publicKey), encryptText);
        return new String(plainData, "utf-8");
    }

    /**
     * AES解密
     * @param encryptKey
     * @param encryptText
     * @return
     */
    public static String decryptAES(String encryptKey, String encryptText) throws Exception{
        byte[] plainData=AESUtils.decrypt(encryptKey, encryptText);
        return new String(plainData, "utf-8");
    }
}