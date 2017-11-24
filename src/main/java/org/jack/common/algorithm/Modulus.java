package org.jack.common.algorithm;

public class Modulus {
	public static int luhn(String cardNumber){
		int ln=cardNumber.length();
		int sum=0;
		for(int i=ln-1,j=0;i>=0;i--,j++){
			int k=cardNumber.charAt(i)-'0';
			if(j%2==0){
				k=k*2;
				if(k>9){
					k=k-9;
				}
			}
			sum+=k;
		}
		return sum%10;
	}
}
