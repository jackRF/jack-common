package org.jack.common.lang;

import java.util.Arrays;

import org.jack.common.BaseTest;
import org.junit.Test;

public class ArrayTest extends BaseTest{
	@Test
	public void testb() {
		Object[] a1={1,null,2};
		Integer[] a2={1,null,2};
		log(Arrays.equals(a1,a2));

	}
	@Test
	public void testa() {
		Class<?> c1=Integer[].class;
		Class<?> c2=Object[].class;
		log(c2.isAssignableFrom(c1));
		log(c2.isAssignableFrom(Integer[][].class));
		log(c2.isAssignableFrom(int[].class));
		log(c2.isAssignableFrom(int[][].class));
		log(c2.isAssignableFrom(Void[].class));
		log(c2.isAssignableFrom(Void[][].class));
	}
}
