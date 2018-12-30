package org.jack.common;

import org.jack.common.util.LoanFlag;
import org.junit.Test;

public class FlagTest extends BaseTest{
	@Test
	public void testc() {
		LoanFlag flag=LoanFlag.valueOf(2l);
		log(flag.has(LoanFlag.APPLY_SMS,LoanFlag.HIGH_QUALITY));
		log(flag.hasAnly(LoanFlag.APPLY_SMS,LoanFlag.HIGH_QUALITY));
		flag=LoanFlag.valueOf(0l);
		log(flag.hasAnly(LoanFlag.EMPTY));
	}
	@Test
	public void testb() {
		LoanFlag[] fs=LoanFlag.unitFlags();
		fs[1]=LoanFlag.APPLY_SMS;
		LoanFlag[] fs1=LoanFlag.unitFlags();
		log(fs1[1].getValue());
	}
	@Test
	public void testa() {
		LoanFlag flag=LoanFlag.EMPTY;
		log(flag.has(LoanFlag.APPLY_SMS));
		log(flag.has(LoanFlag.APPLY_SMS,LoanFlag.HIGH_QUALITY));
		flag=flag.mark(LoanFlag.APPLY_SMS);
		log(flag.has(LoanFlag.APPLY_SMS));
		log(flag.has(LoanFlag.APPLY_SMS,LoanFlag.HIGH_QUALITY));
		flag=flag.mark(LoanFlag.HIGH_QUALITY);
		log(flag.has(LoanFlag.APPLY_SMS,LoanFlag.HIGH_QUALITY));
		log(flag.getValue());
	}
}
