package org.jack.common.clazz;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import org.jack.common.BaseTest;
import org.jack.common.bo.Product2BO;
import org.jack.common.bo.ProductBO;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

public class ClazzTest extends BaseTest{
	protected static void m1(String s){
		
	}
	@Test
	public void testF() {
		ProductBO product2=new ProductBO();
		
		Product2BO product=new Product2BO();
		product.setName("sd");
		product.setPrice(null);
		BeanUtils.copyProperties(product, product2);
		log(product2.getPrice());
	}
	@Test
	public void testE() {
		log(int.class.isAssignableFrom(int.class));
		log(ClassUtils.isAssignable(int.class, Integer.class));
		log(ClassUtils.isAssignable(Integer.class,int.class));
	}
	@Test
	public void testD() {
		try {
			Method method=ClazzTest.class.getMethod("log",double[].class);
			log(method);
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testC() {
		try {
			Method method=CA.class.getMethod("m1", String.class);
			log(method);
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testB() {
		log(Introspector.decapitalize("cSgsf"));
		log(CA.CB.CC.class.getName());
		try {
			Class<?> clazz=ClassUtils.forName("org.jack.common.clazz.CA$CB$CC",null);
			log(clazz);
		} catch (ClassNotFoundException | LinkageError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testa() {
		log(void.class.isPrimitive());
		log(Void.class.getName());
		log(Void[].class.getName());
		log(void.class.getName());
		log(int.class.getName());
		log(int[].class.getName());
		log(Integer.class.getName());
		log(Integer[].class.getName());
		log(String[].class.getName());
		log(String[][].class.getName());

	}
}
