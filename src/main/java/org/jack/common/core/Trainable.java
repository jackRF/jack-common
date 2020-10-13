package org.jack.common.core;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.ObjectUtils;

public abstract class Trainable {
    public <T extends Trainable> boolean train(T trainable,boolean better){
        BeanWrapperImpl wapper=new BeanWrapperImpl(this);
        BeanWrapperImpl wapperDest=new BeanWrapperImpl(trainable);
        PropertyDescriptor[] pds=wapper.getPropertyDescriptors();
        sort(pds);
        for(PropertyDescriptor pd:pds){
            Method method=pd.getReadMethod();
            if(method==null||Object.class.equals(method.getDeclaringClass())){
                continue;
            }
            String name=pd.getName();
            if(!filter(name)){
                continue;
            }
            Object value=wapper.getPropertyValue(name);
            Object valueDest=wapperDest.getPropertyValue(name);
            if(valueDest==null||!ObjectUtils.nullSafeEquals(value, valueDest)){
                if(better||valueDest==null){
                    wapperDest.setPropertyValue(name,value);
                }
                Object nValue=useNewValue(name,value);
                wapper.setPropertyValue(name,nValue==null?wapperDest.getPropertyValue(name):nValue);
                return true;
            }
        }
        return false;
    }
    protected abstract Object useNewValue(String name,Object value);
    protected void sort(PropertyDescriptor[] pds){
    }
    protected boolean filter(String name){
        return true;
    }
}
