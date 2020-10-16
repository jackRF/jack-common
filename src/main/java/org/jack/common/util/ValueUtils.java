package org.jack.common.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import org.jack.common.core.Pair;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class ValueUtils {
	public static <T> T parseJSON2(String json,Class<T> clazz){
        return JSON.parseObject(json, clazz);
    }
    public static String toJSONString2(Object bean){
        return JSON.toJSONString(bean);
    }
	public static String toJSONString(Object bean) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(LocalDateTime.class,new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_DATETIME)));
		simpleModule.addDeserializer(LocalDateTime.class,new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_DATETIME)));
		mapper.registerModule(simpleModule);
		try {
			return mapper.writeValueAsString(bean);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T parseJSON(String json, Class<T> clazz) {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(LocalDateTime.class,new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_DATETIME)));
		simpleModule.addDeserializer(LocalDateTime.class,new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_DATETIME)));
		mapper.registerModule(simpleModule);
		try {
			return mapper.readValue(json, clazz);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String rightPad(String text,String pad,int ln){
		String use=StringUtils.hasText(text)?text:"";
		for(;;){
			if(use.length()>=ln){
				break;
			}
			use=use+pad;
		}
		return use;
	}
	public static String leftPad(String text,String pad,int ln){
		String use=StringUtils.hasText(text)?text:"";
		for(;;){
			if(use.length()>=ln){
				break;
			}
			use=pad+use;
		}
		return use;
	}
	/**
     * 属性转列名
     * @param property
     * @return
     */
    public static String propertyToColumn(String property){
        return propertyToColumn(property,new StringBuilder());
    }
    /**
     * 列名转属性名
     * @param alias
     * @return
     */
    public static String columnToProperty(String alias){
        return propertyToColumn(alias,new StringBuilder());
    }
    /**
     * 属性转列名
     * @param property
     * @param sb
     * @return
     */
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
    /**
     * 列名转属性名
     * @param alias
     * @param sb
     * @return
     */
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
    public static void fillMap(Object bean,Map<String,Object> map){
        BeanWrapperImpl wrapper=new BeanWrapperImpl(bean);
        PropertyDescriptor[] pds=wrapper.getPropertyDescriptors();
        if(pds==null||pds.length==0){
            return;
        }
        for(PropertyDescriptor pd:pds){
            Method  method =pd.getReadMethod();
            String propertyName=pd.getName();
            if(method==null||Object.class.equals(method.getDeclaringClass())
            ||!wrapper.isReadableProperty(propertyName)){
                continue;
            }
           Object value=wrapper.getPropertyValue(propertyName);
           map.put(propertyName, value);
        }
    }
    public static Pair<Map<String,Object>,Map<String,Object>> diff(Map<String,Object> oldValueMap,Map<String,Object> newValueMap){
        Map<String,Object> oldDiffMap=new HashMap<String,Object>();
        Map<String,Object> newDiffMap=new HashMap<String,Object>();
        for(Map.Entry<String,Object> entry:newValueMap.entrySet()){
            String key=entry.getKey();
            if(!ObjectUtils.nullSafeEquals(entry.getValue(), oldValueMap.get(key))){
                oldDiffMap.put(key, oldValueMap.get(key));
                newDiffMap.put(key, entry.getValue());
            }
        }
        Pair<Map<String,Object>,Map<String,Object>> pair= new Pair<Map<String,Object>,Map<String,Object>>();
        pair.setV1(oldDiffMap);
        pair.setV2(newDiffMap);
        return pair;
	}
	public static <T> boolean contains(T dest,T...array){
		if(array==null||array.length==0){
			return false;
		}
		for(T item:array){
			if(ObjectUtils.nullSafeEquals(item, dest)){
				return true;
			}
		}
		return false;
	}
	public static <T> T defaultValue(T t, T def) {
		return t == null ? def : t;
	}

	public static String percent(double decimal, int fraction) {
		// 获取格式化对象
		NumberFormat nt = NumberFormat.getPercentInstance();
		// 设置百分数精确度2即保留两位小数
		nt.setMinimumFractionDigits(fraction);
		return nt.format(decimal);
	}

	public static BigDecimal decimalAdd(BigDecimal dec1, BigDecimal dec2) {
		if (dec1 == null || dec2 == null) {
			return dec1 == null ? dec2 : dec1;
		}
		return dec1.add(dec2);
	}

	public static int numberAdd(Integer... nums) {
		int result = 0;
		for (Integer num : nums) {
			if (num != null) {
				result += num.intValue();
			}
		}
		return result;

	}

	public static BigDecimal max(BigDecimal dec1, BigDecimal dec2) {
		if (dec1 == null || dec2 == null) {
			return dec1 == null ? dec2 : dec1;
		}
		return dec1.compareTo(dec2) >= 0 ? dec1 : dec2;
	}

	public static BigDecimal min(BigDecimal dec1, BigDecimal dec2) {
		if (dec1 == null || dec2 == null) {
			return dec1 == null ? dec2 : dec1;
		}
		return dec1.compareTo(dec2) <= 0 ? dec1 : dec2;
	}

	public static <K, V, R> Map<K, R> listToMap(List<V> list,
			ItemStrategy<K, V, R> itemStrategy) {
		Map<K, R> map = new HashMap<K, R>();
		for (V v : list) {
			K k = itemStrategy.getKey(v);
			R r;
			if (!map.containsKey(k)) {
				r = itemStrategy.whenNewKey(v);
				map.put(k, r);
			} else {
				r = map.get(k);
			}
			itemStrategy.onValue(r, v);
		}
		return map;
	}

	public static <K, T> Map<K, List<T>> listToMap(List<T> list,
			final KeyStrategy<K, T> keyStrategy) {
		return ValueUtils.listToMap(list,
				new ValueUtils.ItemStrategy<K, T, List<T>>() {

					@Override
					public K getKey(T v) {
						return keyStrategy.getKey(v);
					}

					@Override
					public List<T> whenNewKey(T v) {
						return new ArrayList<T>();
					}

					@Override
					public void onValue(List<T> r, T v) {
						r.add(v);
					}
				});
	}

	public static interface KeyStrategy<K, V> {
		K getKey(V v);
	}

	public static interface ItemStrategy<K, V, R> extends KeyStrategy<K, V> {
		R whenNewKey(V v);

		void onValue(R r, V v);
	}
}
