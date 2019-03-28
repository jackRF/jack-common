package org.jack.common.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;

import org.jack.common.BaseTest;
import org.junit.Test;
import org.springframework.core.ResolvableType;

public class ReflectTest extends BaseTest{
	@Test
	public void test7() {
		Field[] fs=E2.class.asSubclass(E1.class).getDeclaredFields();
		log(fs);
		fs=E1.class.getDeclaredFields();
		log(fs);
	}
	@Test
	public void test6() {
		Method[] methods=RA2.class.getDeclaredMethods();
		Method[] methods2=RA2.class.getDeclaredMethods();
		log(methods[0]==methods2[0]);
		log(methods[0].equals(methods2[0]));
	}
	@Test
	public void test5(){
		try {
			ResolvableType rt=ResolvableType.forField(Publisher.class.getDeclaredField("observer"), CreditPublisher.class);
			log((Object)rt.resolve());
		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void test4(){
		Constructor[] cs=RA2.class.getConstructors();
		for(Constructor cor:cs){
			log(cor.isSynthetic());
		}
		
		Method[] methods=RA2.class.getDeclaredMethods();
		for(Method method:methods){
			Class<?>[] clazzs=method.getParameterTypes();
			log(method.getName()+"|"+method.isBridge()+"|"+method.isSynthetic()+"|ParameterTypes");
			for(Class<?> clazz:clazzs){
				log((Object)clazz);
			}
		}
		
	}
	@Test
	public void test3() {
		log(RD.class.getTypeName());
		log(RD.class.getTypeParameters());
		
		Type type=RE.class.getGenericSuperclass();
		if(type instanceof ParameterizedType){
			ParameterizedType typep=(ParameterizedType)type;
//			log(typep.getActualTypeArguments());
		}
//		log(type);
		
		try {
			Method method=RAW.class.getDeclaredMethod("m1", List.class);
			Type[] types=method.getGenericParameterTypes();
//			log(types);
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void log(Type...types){
		for(Type typei:types){
			log(typei);
		}
	}
	private void log(Type typei){
		log((Object)typei.getClass());
		if(typei instanceof ParameterizedType){
			ParameterizedType typep=(ParameterizedType)typei;
			log("OwnerType:"+typep.getOwnerType()+"|TypeName:"+typep.getTypeName()+"|ActualTypeArguments:");
			log(typep.getActualTypeArguments());
		}else if(typei instanceof WildcardType){
			WildcardType typew=(WildcardType)typei;
			log("LowerBounds:"+typew.getLowerBounds().length);
			log("UpperBounds:"+typew.getUpperBounds().length);
		}else if(typei instanceof GenericArrayType){
			GenericArrayType typea=(GenericArrayType)typei;
			log("TypeName:"+typea.getTypeName());
			log("GenericComponentType:");
			log(typea.getGenericComponentType());
		}else if(typei instanceof TypeVariable){
			TypeVariable typev=(TypeVariable)typei;
			log("Name:"+typev.getName()+"|TypeName:"+typev.getTypeName());
			Type[] types=typev.getBounds();
			if(types.length>0){
				log("Bounds:");
				log(types);
			}
			
		}
	}
	@Test
	public void test2() {
		log(RE.class.asSubclass(RD.class));		
	}
	@Test
	public void test1() {
		log(RB.class);
		isBridge(RB.class.getDeclaredMethods());
		log(RC.class);
		isBridge(RC.class.getDeclaredMethods());
	}
	private void isBridge(Method...methods) {
		for(Method method:methods){
			log(method.getName()+" isBridge:"+method.isBridge()+" "+method.getParameterTypes()[0]);
		}
	}
}
