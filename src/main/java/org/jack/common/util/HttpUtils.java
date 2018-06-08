package org.jack.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * http 工具类
 * @author YM10177
 *
 */
public class HttpUtils {
	private static class HttpClientHolder{
		private static final HttpClient HTTP_CLIENT=HttpClientBuilder.create().build();
	}
	/**
	 * data转为http查询字符串
	 * @param data
	 * @return
	 */
	public static String dataToParams(Map<String,?> data){
		StringBuilder sb=new StringBuilder();
		int i=0;
		for(Map.Entry<String,?> entry:data.entrySet()){
			Object value=entry.getValue();
			if(value==null){
				continue;
			}
			if(i>0){
				sb.append("&");
			}
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(value);
			i++;
		}
		return sb.toString();
	}
	/**
	 * data转为form表单
	 * @param data
	 * @return
	 */
	public static List<NameValuePair> dataToNameValuePairs(Map<String,?> data){
		List<NameValuePair> params=new ArrayList<NameValuePair>();
		for(Map.Entry<String,?> entry:data.entrySet()){
			Object value=entry.getValue();
			if(value==null){
				continue;
			}
			NameValuePair pair=new BasicNameValuePair(entry.getKey(),value.toString());
			params.add(pair);
		}
		return params;
	}
	/**
	 * post 请求
	 * @param url
	 * @param data
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws UnsupportedOperationException 
	 */
	public static String post(String url,Map<String,?> data) throws ClientProtocolException, IOException{
		HttpPost httpPost=new HttpPost(url);
		HttpEntity entity=new UrlEncodedFormEntity(dataToNameValuePairs(data),"UTF-8");
		httpPost.setEntity(entity);
		return doRequest(httpPost);
	}
	public static String post(String url,String json) throws ClientProtocolException, IOException{
		HttpPost httpPost=new HttpPost(url);
		StringEntity entity = new StringEntity(json,"UTF-8");   
		entity.setContentEncoding("UTF-8");
		entity.setContentType("application/json");
		httpPost.setEntity(entity);
		return doRequest(httpPost);
	}
	/**
	 * get 请求
	 * @param url
	 * @param data
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws UnsupportedOperationException 
	 */
	public static String get(String url) throws ClientProtocolException, IOException{
		return doRequest(new HttpGet(url));
	}
	/**
	 * get 请求
	 * @param url
	 * @param data
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws UnsupportedOperationException 
	 */
	public static String get(String url,Map<String,?> data) throws ClientProtocolException, IOException{
		return doRequest(new HttpGet(url+dataToParams(data)));
	}
	/**
	 * 执行请求
	 * @param request
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String doRequest(HttpUriRequest request) throws ClientProtocolException, IOException{
		HttpEntity httpEntity=null;
		try{
			HttpResponse httpResponse=HttpClientHolder.HTTP_CLIENT.execute(request);
			httpEntity=httpResponse.getEntity();
			return EntityUtils.toString(httpEntity);
		}finally{
			EntityUtils.consume(httpEntity);
		}
	}
}
