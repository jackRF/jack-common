package org.jack.common.core;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.ObjectUtils;

public abstract class Trainable {
    protected String[] noTrain;
    public Trainable(String...noTrain){
        this.noTrain=noTrain;
    }
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
            Object value=wapper.getPropertyValue(name);
            if(!filter(name)){
                wapperDest.setPropertyValue(name,value);
                continue;
            }
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
        if(noTrain==null||noTrain.length==0){
            return true;
        }
        for(String nt:noTrain){
            if(name.equals(nt)){
                return false;
            }
        }
        return true;
    }
}
