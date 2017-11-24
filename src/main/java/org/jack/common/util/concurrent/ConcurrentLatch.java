package org.jack.common.util.concurrent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 并发的latch,一个key 只能一个线程执行
 * @author YM10177
 *
 * @param <K>
 */
public class ConcurrentLatch<K>{
	private final Map<K,Latch> latchMap=new ConcurrentHashMap<K, Latch>();
	private final Object mutex;
	public ConcurrentLatch(){
		mutex=null;
	}
	public ConcurrentLatch(Object mutex){
		this.mutex=mutex;
	}
	public Object getMutex(){
		return mutex!=null?mutex:latchMap;
	}
	public Object require(K key){
		synchronized (getMutex()) {
			if(!latchMap.containsKey(key)){
				latchMap.put(key, new Latch());
			}
			Latch latch=latchMap.get(key);
			latch.require();
			return latch;
		}
	}
	public void release(K key){
		synchronized (getMutex()) {
			Latch latch=latchMap.get(key);
			if(latch.release()==0){
				latchMap.remove(key); 
			}
		}
	}
	private static class Latch{
		private int count;
		private int  require(){
			count++;
			return count;
		}
		private int release(){
			count--;
			return count;
		}
		
	}
}
