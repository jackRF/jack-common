package org.jack.common;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public abstract class BaseTest {
	protected void testCompare(Object bean1,Object bean2){
		Map<String,Class<?>> map1=propertiesMap(bean1);
		Map<String,Class<?>> map2=propertiesMap(bean2);
		CompareInfo<String> compareInfo=new CompareInfo<String>();
		for(Map.Entry<String,Class<?>> entry:map1.entrySet()){
			String property=entry.getKey();
			if(map2.containsKey(property)){
				compareInfo.addSameProperty(property);
				if(!entry.getValue().equals(map2.get(property))){
					compareInfo.addClassConflictProperty(property);
				}
			}else{
				compareInfo.addDiff1Property(property);
			}
		}
		for(Map.Entry<String,Class<?>> entry:map2.entrySet()){
			String property=entry.getKey();
			if(!map1.containsKey(property)){
				compareInfo.addDiff2Property(property);
			}
		}
		log("same properties:"+compareInfo.getSameProperty());
		log("class conflict properties:"+compareInfo.getClassConflictProperty());
		log("bean1-bean2 properties:"+compareInfo.getDiff1Property());
		log("bean2-bean1 properties:"+compareInfo.getDiff2Property());
	}
	
	private static class CompareInfo<T>{
		private Map<String,Set<T>> scopeMap=new HashMap<String,Set<T>>();
		private Set<T> getSameProperty() {
			return scopeMap.get("same");
		}
		private Set<T> getClassConflictProperty(){
			return scopeMap.get("classConflict");
		}
		private Set<T> getDiff1Property(){
			return scopeMap.get("diff1");
		}
		private Set<T> getDiff2Property(){
			return scopeMap.get("diff2");
		}
		private void addSameProperty(T property) {
			add("same",property);
		}
		private void addClassConflictProperty(T property){
			add("classConflict",property);
		}
		private void addDiff1Property(T property){
			add("diff1",property);
		}
		private void addDiff2Property(T property){
			add("diff2",property);
		}
		private void add(String scope,T item){
			Set<T> set=null;
			if(scopeMap.containsKey(scope)){
				set=scopeMap.get(scope);
			}else{
				set=new HashSet<T>();
				scopeMap.put(scope, set);
			}
			set.add(item);
		}
	}
	private Map<String,Class<?>> propertiesMap(Object bean1){
		Map<String,Class<?>> map=new HashMap<String,Class<?>>();
		BeanWrapper beanWrapper=new BeanWrapperImpl(bean1);
		PropertyDescriptor[] pds=beanWrapper.getPropertyDescriptors();
		for(PropertyDescriptor pd:pds){
			Method readMethod=pd.getReadMethod();
			if(readMethod==null||Object.class.equals(readMethod.getDeclaringClass())){
				continue; 
			}
			map.put(pd.getName(), pd.getPropertyType());
		}
		return map;
	}
	protected static void log(double...msgs) {
		for(double msg:msgs){
			log(msg);
		}
	}
	protected static void log(float...msgs) {
		for(float msg:msgs){
			log(msg);
		}
	}
	protected static void log(long...msgs) {
		for(long msg:msgs){
			log(msg);
		}
	}
	protected static void log(int...msgs) {
		for(int msg:msgs){
			log(msg);
		}
	}
	protected static void log(char...msgs) {
		for(char msg:msgs){
			log(msg);
		}
	}
	protected static void log(short...msgs) {
		for(short msg:msgs){
			log(msg);
		}
	}
	protected static void log(byte...msgs) {
		for(byte msg:msgs){
			log(msg);
		}
	}
	protected void log(Object...msgs) {
		for(Object msg:msgs){
			log(msg);
		}
	}
	protected static void log(Object msg) {
		System.out.println(msg);
	}
}
