package org.jack.common;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jack.common.util.HttpUtils;
import org.jack.common.util.IOUtils;
import org.jack.common.validation.Validator;
import org.junit.Test;



public class MyTest extends BaseTest {
	@Test
	public void test2(){
		log(Validator.validateBankCardNo("6259650871772098"));
	}
	@Test
	public void test1(){
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
