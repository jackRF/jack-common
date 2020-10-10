package org.jack.common;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jd.drools.test.RuleTest;

import org.apache.http.client.ClientProtocolException;
import org.jack.common.util.DateUtils;
import org.jack.common.util.HttpUtils;
import org.jack.common.util.LoanFlag;
import org.jack.common.validation.Validator;
import org.junit.Test;

import sun.misc.Unsafe;



public class MyTest extends BaseTest {
	@Test
	public void test10() {
		Date date=DateUtils.weekDay(new Date(), 1);
		log(DateUtils.formatDate(date, "yyMMdd"));
	}
	@Test
	public void test9() {
		RuleTest test=new RuleTest();
        test.testPointRule("e");
	}
	@Test
	public void test8() {
		int limit=500;
		StringBuilder sql=new StringBuilder();
		sql.append("select  b.LOAN_NO,b.STATUS,b.RTF_STATE,b.APPLY_TYPE,p.INIT_PRODUCT_CD, p.PRODUCT_CD");
		sql.append(",b.CREATED_TIME,b.MODIFIED_TIME ");
		sql.append(",a.CREATED_TIME as FIRST_COMMIT_DATE");
		sql.append(",e.FIRST_LEVLE_REASONS_CODE,e.TWO_LEVLE_REASONS_CODE");
		sql.append(",b.PERSON_ID,b.RTF_NODE_STATE,b.APP_INPUT_FLAG");
		sql.append(" from bms_loan_base b left join bms_loan_audit a on  b.LOAN_NO=a.LOAN_NO");
		sql.append(" left join bms_loan_ext e on  b.LOAN_NO=e.LOAN_NO");
		sql.append(" left join bms_loan_product p on  b.LOAN_NO=p.LOAN_NO");
		sql.append(" where b.ID_NO=:idNo and b.STATUS!='NORMAL' ORDER BY b.id desc");
		if(limit>0) {
			sql.append(" limit "+limit);
		}
		log(sql.toString());
	}
	@Test
	public void test7() {
		log(LoanFlag.unitFlags());
	}
	@Test
	public void test6() {
		try {
			for(int i=0;i<1000;i++){
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							log(HttpUtils.get("http://172.16.230.37:8080/creditzx-web/"));
						} catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}).start();
			}
			Thread.sleep(50000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@Test
	public void test5() {
		Field f;
		try {
			f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			Unsafe unsafe = (Unsafe) f.get(null);
			log(unsafe);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Test
	public void test4() {
		Object o=new Object();
		log(o.hashCode());
		log(System.identityHashCode(o));
	}
	@Test
	public void test3() {
		int k=2;
		 int iChild = (k << 1) + 1;
		 log(iChild);

	}
	@Test
	public void test2(){
		log(Validator.validateBankCardNo("6259650871772098"));
	}
	@Test
	public void test1(){
		log(System.nanoTime());
		log(System.nanoTime());
		log("sfsf".charAt(-1));
	}
	@Test
	public void testHttp() {
		Map<String,String> req = new HashMap<String,String>();
		req.put("sorgcode", "30310105201605003");
		req.put("name", "孙妍妍");
		req.put("idCard", "411422198810245725");
		String hash=null;
		try {
			hash = signatureForSH(req, "e3DydhHm6cHEP26");
			req.put("hash",hash);
			log(HttpUtils.post("https://test.suanhua.org/cpcs/api/v2"+"/channel/3001", req));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	interface MessageStrategy{
		StringBuilder messageStrategy(Map<String,String> paramMap,String[] sortParamNames);
	}
	
	public static String signatureForSH(Map<String,String> map,final String secret) throws Exception {
		StringBuilder content=buildDigestMessage(map, new MessageStrategy(){

			@Override
			public StringBuilder messageStrategy(Map<String, String> paramMap,
					String[] sortParamNames) {
				StringBuilder sb=new StringBuilder();
				for(String paramName:sortParamNames){
					sb.append(paramMap.get(paramName));
				}
				sb.append(secret);
				return sb;
			}
		});
		return md5(content.toString().getBytes("utf-8"));
	}
	private static StringBuilder buildDigestMessage(Map<String,String> map,MessageStrategy ms){
		List<String> list = new ArrayList<String>();
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		String[] arr = list.toArray(new String[list.size()]);
		// 将参数进行字典序排序
		Arrays.sort(arr);
		StringBuilder content =null;
		if(ms!=null){
			content=ms.messageStrategy(map, arr);
		}else{
			content = new StringBuilder();
			for (int i = 0; i < arr.length; i++) {
				if(null != map.get(arr[i])){
					content.append(arr[i] + "=" + map.get(arr[i])).append("&");
				}
			}
			content.deleteCharAt(content.length() - 1);
		}
		return content;
	}
	public static String md5(byte[] input) throws NoSuchAlgorithmException{
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		byte[] digest = messageDigest.digest(input);
		String sign = byts2hexstr(digest);
		return sign;
	}
	private static String byts2hexstr(byte[] arrayBytes) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (int i = 0; i < arrayBytes.length; i++) {
            tmp = Integer.toHexString(arrayBytes[i] & 0xff);
            sb.append(tmp.length() == 1 ? "0" + tmp : tmp);
        }
        return sb.toString();
    }
}
