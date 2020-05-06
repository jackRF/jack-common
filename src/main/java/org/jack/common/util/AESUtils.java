package org.jack.common.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {
    /**
     * AES加密
     * @param encryptKey 密钥
     * @param content 内容
     * @return
     * @throws Exception
     */
    public static String encrypt(String encryptKey, byte[] content) throws Exception{
        //加密 1.构造密钥 2.创建和初始化密码器 3.内容加密 4.返回字符串
        SecretKey key = new SecretKeySpec(encryptKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] byte_AES = cipher.doFinal(content);
        return Base64Utils.encodeBase64String(byte_AES);
    }

    /**
     * AES解密
     * @param encryptKey 密钥
     * @param encryptText 密文
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(String encryptKey, String encryptText)throws Exception{
        //解密 1.构造密钥 2.创建和初始化密码器 3.将加密后的字符串反纺成byte[]数组 4.将加密内容解密
        SecretKey key = new SecretKeySpec(encryptKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] byte_content = Base64Utils.decodeBase64(encryptText);
        return cipher.doFinal(byte_content);
	}
}