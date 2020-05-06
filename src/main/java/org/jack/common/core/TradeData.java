package org.jack.common.core;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.BeanWrapperImpl;

public abstract class TradeData {
    
    protected void applyForSign(Map<String,Object> map){
        BeanWrapperImpl wrapper=new BeanWrapperImpl(this);
        PropertyDescriptor[] pds=wrapper.getPropertyDescriptors();
        if(pds==null||pds.length==0){
            return;
        }
        for(PropertyDescriptor pd:pds){
            if(!filter(pd)){
                continue;
            }
            String propertyName=pd.getName();
           if(!wrapper.isReadableProperty(propertyName)){
                continue; 
           }
           Object value=wrapper.getPropertyValue(propertyName);
           map.put(propertyName, TradeData.convertValue(value));
        }
    }
    protected boolean filter(PropertyDescriptor pd){
        Method  method =pd.getReadMethod();
        if(method==null||Object.class.equals(method.getDeclaringClass())){
            return false;
        }
        return true;
    }
    @SuppressWarnings("unchecked")
    public static Object convertValue(Object value){
        if(value==null){
            return null;
        }
        if(value instanceof TradeData){
            return convertValue((TradeData)value);
        }else if(value instanceof Collection<?>){
            return convertValue((Collection<?>)value);
        }else if(value instanceof Object[]){
            return convertValue((Object[])value);
        }else if(value instanceof Map){
            return convertValue((Map<String,Object>)value);
        }else{
            return value;
        }
    }
    private static Object convertValue(Map<String,Object> map){
        Map<String,Object> orderMap=new TreeMap<String,Object>();
        for(Map.Entry<String,Object> entry:map.entrySet()){
            orderMap.put(entry.getKey(), convertValue(entry.getValue()));
        }
        return orderMap;
    }
    private static Object convertValue(Collection<?> values){
        List<Object> list=new ArrayList<Object>();
        for(Object item:values){
            list.add(convertValue(item));
        }
        return list;
    }
    private static Object convertValue(Object[] values){
        for(int i=0,ln=values.length;i<ln;i++){
            values[i]=convertValue(values[i]);
        }
        return values;
    }
    private static Object convertValue(TradeData value){
        Map<String,Object> orderMap=new TreeMap<String,Object>();
        TradeData tradeData=(TradeData)value;
        tradeData.applyForSign(orderMap);
        return orderMap;
    }
}