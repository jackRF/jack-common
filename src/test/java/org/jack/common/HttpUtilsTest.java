package org.jack.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.http.client.ClientProtocolException;
import org.jack.common.util.HttpUtils;
import org.junit.Test;

public class HttpUtilsTest extends BaseTest{
	@Test
	public void testHttp() {
		testConcurrency(100, new Task<String>(){

			@Override
			public void toDo(String key) {
				try {
					Map<String,Object> paramMap=new HashMap<>();
					paramMap.put("key", key);
					String json=HttpUtils.get("http://localhost:8080/cfs-web-boss/api/rule/test.do?",paramMap);
					log(json);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	interface Task<K>{
		void toDo(K key);
	}
	/**
	 * 测试并发
	 */
	private void testConcurrency(int threadCount,Task<String> task) {
		CountDownLatch latch=new CountDownLatch(1);
		for(int i=0;i<threadCount;i++){
			final int ui=i;
			Thread thread=new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						latch.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					task.toDo(ui+"");
				}
			});
			thread.start();
		}
		latch.countDown();
	}
}
