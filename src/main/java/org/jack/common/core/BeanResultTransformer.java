package org.jack.common.core;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.transform.BasicTransformerAdapter;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class BeanResultTransformer extends BasicTransformerAdapter {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Class<?> resultClass;
    private Map<String,String> columnPropertyMap;
    public BeanResultTransformer(Class<?> resultClass) {
        this.resultClass=resultClass;
    }
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
    private void init(){
        Map<String, String> temp = new HashMap<String, String>();
        BeanWrapper beanWrapper=new BeanWrapperImpl(resultClass);
        PropertyDescriptor[] pds=beanWrapper.getPropertyDescriptors();
        for(PropertyDescriptor pd:pds){
            final String property=pd.getName();
            String column=property.toUpperCase();
            if(temp.containsKey(column)){
                throw new RuntimeException("Property to column 重复:"+column);
            }
            temp.put(column, property);
            String column2=propertyToColumn(property,new StringBuilder());
            if(column.equals(column2)){
                continue;
            }
            if(temp.containsKey(column2)){
                throw new RuntimeException("Property to column 重复:"+column2);
            }
            temp.put(column2, property);
        }
        this.columnPropertyMap=temp;
    }
    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        if(columnPropertyMap==null){
            init();
        }
        BeanWrapper beanWrapper=new BeanWrapperImpl(resultClass);
        int ln=aliases.length;
        for(int i=0;i<ln;i++){
            if(aliases[i]==null){
                continue;
            }
            String column=aliases[i].toUpperCase();
            if(!columnPropertyMap.containsKey(column)){
                if(column.length()>2&&column.charAt(1)=='_'){
                    String temp=column;
                    column=column.substring(2);
                    if(!columnPropertyMap.containsKey(column)){
                        column=temp.substring(0, 1)+column;
                    }
                }
            }
            String propertyName=columnPropertyMap.get(column);
            if(propertyName!=null&&beanWrapper.isWritableProperty(propertyName)){
                beanWrapper.setPropertyValue(propertyName, tuple[i]);
            }
        }
        return beanWrapper.getWrappedInstance();
    }
}