package org.jack.common.clazz;

import java.util.List;

public class A2 extends A1{
	@Override
	protected void m1(int i) {
		super.m1(1);
	}
	@Override
	protected void m2(CharSequence i) {
		// TODO Auto-generated method stub
		super.m2("");
	}
	@Override
	protected void m3(Object[] os) {
		// TODO Auto-generated method stub
		super.m3(os);
	}
	@Override
	protected void m4(List<String> li) {
		// TODO Auto-generated method stub
		super.m4(li);
	}
	@Override
	protected String m5(String s) {
		// TODO Auto-generated method stub
		return "";
	}
//	@Override
//	protected Object m6(Object o) {// 编译不通过
//		// TODO Auto-generated method stub
//		return super.m6(o);
//	}
//	@Override
//	protected void m7(Object o) {// 编译不通过
//		// TODO Auto-generated method stub
//		super.m7(o);
//	}
	
}
