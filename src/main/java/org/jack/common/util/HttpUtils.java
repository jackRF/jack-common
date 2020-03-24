package org.jack.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
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
		private static final HttpClient HTTP_CLIENT;
		static{
			HttpClientBuilder httpClientBuilder=HttpClientBuilder.create();
			//连接池中最大连接数
			httpClientBuilder.setMaxConnTotal(200);
			/** 
			 * 分配给同一个route(路由)最大的并发连接数。 
			 * route：运行环境机器 到 目标机器的一条线路。 
			 * 举例来说，我们使用HttpClient的实现来分别请求 www.baidu.com 的资源和 www.bing.com 的资源那么他就会产生两个route。 
			 */ 
			httpClientBuilder.setMaxConnPerRoute(100);
			RequestConfig requestConfig = RequestConfig.custom()
					 //从连接池中获取连接的超时时间
                    .setConnectionRequestTimeout(1000)  
                    //与服务器连接超时时间：httpclient会创建一个异步线程用以创建socket连接，此处设置该socket的连接超时时间  
					.setConnectTimeout(5000)  
					//socket读数据超时时间：从服务器获取响应数据的超时时间
                    .setSocketTimeout(5000)                 
                    .build();
			httpClientBuilder.setDefaultRequestConfig(requestConfig);
			HTTP_CLIENT=httpClientBuilder.build();
		}
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
	
	public static class MultipartInfo{
		private String fileKey;
		private String fileName;
		private InputStream inputStream;
		public String getFileKey() {
			return fileKey;
		}
		public void setFileKey(String fileKey) {
			this.fileKey = fileKey;
		}
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public InputStream getInputStream() {
			return inputStream;
		}
		public void setInputStream(InputStream inputStream) {
			this.inputStream = inputStream;
		}
	}
	public String upload(String url,List<MultipartInfo> multipartInfos,Map<String,Object> paramMap) throws ClientProtocolException, IOException{
		HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for(MultipartInfo multipartInfo:multipartInfos){
        	builder.addBinaryBody(multipartInfo.getFileKey(), multipartInfo.getInputStream(), ContentType.MULTIPART_FORM_DATA, multipartInfo.getFileName());
        }
        ContentType contentType=ContentType.create("text/plain", Consts.UTF_8);
        for(Map.Entry<String,Object> entry:paramMap.entrySet()){
        	if(entry.getValue()==null){
        		continue;
        	}
        	builder.addTextBody(entry.getKey(), entry.getValue().toString(),contentType);
        }
        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);
        return consumeRequest(httpPost);
	}
	public String upload(String url,MultipartInfo multipartInfo,Map<String,Object> paramMap) throws ClientProtocolException, IOException{
		return upload(url, Collections.singletonList(multipartInfo), paramMap);
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
		return consumeRequest(new HttpGet(url));
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
		return consumeRequest(buildHttpGet(url, data));
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
		return consumeRequest(buildHttpPostForm(url, data));
	}
	public static String post(String url,String json) throws ClientProtocolException, IOException{
		return consumeRequest(buildHttpPostJson(url, json));
	}
	public static HttpGet buildHttpGet(String url,Map<String,?> data) {
		return new HttpGet(url+dataToParams(data));
	}
	public static HttpPost buildHttpPostForm(String url,Map<String,?> data) throws UnsupportedEncodingException {
		HttpPost httpPost=new HttpPost(url);
		HttpEntity entity=new UrlEncodedFormEntity(dataToNameValuePairs(data),"UTF-8");
		httpPost.setEntity(entity);
		return httpPost;
	}
	public static HttpPost buildHttpPostJson(String url,String json){
		HttpPost httpPost=new HttpPost(url);
		StringEntity entity = new StringEntity(json,"UTF-8");   
		entity.setContentEncoding("UTF-8");
		entity.setContentType("application/json");
		httpPost.setEntity(entity);
		return httpPost;
	}
	public static HttpResponse request(String url) throws ClientProtocolException, IOException {
		return doRequest(new HttpGet(url));
	}
	/**
	 * 执行请求
	 * @param request
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	private static String consumeRequest(HttpUriRequest request) throws ClientProtocolException, IOException{
		HttpEntity httpEntity=null;
		try{
			HttpResponse httpResponse=doRequest(request);
			httpEntity=httpResponse.getEntity();
			return EntityUtils.toString(httpEntity);
		}finally{
			EntityUtils.consume(httpEntity);
		}
	}
	public static HttpResponse doRequest(HttpUriRequest request) throws ClientProtocolException, IOException {
		return HttpClientHolder.HTTP_CLIENT.execute(request);
	}
}
