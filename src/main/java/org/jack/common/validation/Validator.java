package org.jack.common.validation;

import org.jack.common.algorithm.Modulus;
/**
 * 验证器
 * @author YM10177
 *
 */
public class Validator {
	/**
	 * 验证是否是纯数字
	 * @param text
	 * @return
	 */
	public static boolean validateDigit(String text){
		if(text==null){
			return false;
		}
		for(int i=0;i<text.length();i++){
			if(text.charAt(i)<'0'||text.charAt(i)>'9'){
				return false;
			}
		}
		return true;
	}
	/**
	 * 验证银行卡
	 * @param bankCardNo
	 * @return
	 */
	public static boolean validateBankCardNo(String bankCardNo){
		if(bankCardNo==null||bankCardNo.length()<8){
			return false;
		}
		if(!validateDigit(bankCardNo)){
			return false;
		}
		int lastIndex=bankCardNo.length()-1;
		int m10=Modulus.luhn(bankCardNo.substring(0,lastIndex));
		return (10-m10)%10==bankCardNo.charAt(lastIndex)-'0';
	}
}
