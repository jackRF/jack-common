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
	/**
	 * 比较两个字符串的相似度，并返回相似率。
	 * @param str1
	 * @param str2
	 * @return
	 */
    public static float Levenshtein(String str1, String str2){
        char[] char1 = str1.toCharArray();
        char[] char2 = str2.toCharArray();
        //计算两个字符串的长度。  
        int len1 = char1.length;
        int len2 = char2.length;
        //建二维数组，比字符长度大一个空间  
        int[][] dif = new int[len1 + 1][len2 + 1];
        //赋初值  
        for (int a = 0; a <= len1; a++){
            dif[a][0] = a;
        }
        for (int a = 0; a <= len2; a++){
            dif[0][a] = a;
        }
        //计算两个字符是否一样，计算左上的值  
        int temp;
        for (int i = 1; i <= len1; i++)
        {
            for (int j = 1; j <= len2; j++)
            {
                if (char1[i - 1] == char2[j - 1])
                {
                    temp = 0;
                }
                else
                {
                    temp = 1;
                }
                //取三个值中最小的  
                dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1, dif[i - 1][j] + 1);
            }
        }
        //计算相似度  
        float similarity = 1 - (float)dif[len1][len2] / Math.max(len1,len2);
        return similarity;
    }

    /**
     * 求最小值
     * @return
     */
    private static int min(int...nums){
        int min = Integer.MAX_VALUE;
        for(int item:nums){
            if (min > item){
                min = item;
            }
        }
        return min;
    }
}
