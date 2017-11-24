package org.jack.common;

import org.jack.common.validation.Validator;
import org.junit.Test;

public class MyTest extends BaseTest {
	@Test
	public void test2(){
		log(Validator.validateBankCardNo("6259650871772098"));
	}
	@Test
	public void test1(){
		log("sfsf".charAt(-1));
	}
}
