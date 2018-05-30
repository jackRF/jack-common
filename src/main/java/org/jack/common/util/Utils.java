package org.jack.common.util;

public class Utils {
	public static String propertyToColumn(String property,StringBuilder sb){
		sb.setLength(0);
		int i=0;
		boolean startUpperCase=false;
		while(i<property.length()){
			char c=property.charAt(i++);
			if(c>='A' && c<='Z'){
				if(i>1&&!startUpperCase){
					sb.append('_');
				}
				startUpperCase=true;
			}else{
				if(startUpperCase){
					startUpperCase=false;
				}
				if(c>='a'&& c<='z'){
					c=(char) (c-('a'-'A'));
				}
			}
			sb.append(c);
		}
		return sb.toString();
	}
	public static String columnToProperty(String alias,StringBuilder sb){
		sb.setLength(0);
		boolean underlineBefore=false;
		for(int i=0;i<alias.length();i++){
			char c=alias.charAt(i);
			if(c=='_'){
				underlineBefore=true;
				continue;
			}
			if(underlineBefore){
				if(c>='a'&& c<='z'){
					c=(char) (c-('a'-'A'));
				}
				underlineBefore=false;
			}else if(c>='A' && c<='Z'){
				c=(char) (c+('a'-'A'));
			}
			sb.append(c);
		}
		return sb.toString();
	}
}
