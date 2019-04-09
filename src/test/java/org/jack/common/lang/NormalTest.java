package org.jack.common.lang;

import org.jack.common.BaseTest;
import org.junit.Test;

public class NormalTest extends BaseTest{
	@Test
	public void testa() {
		log(Float.NaN);
		log(Float.floatToIntBits(Float.NaN));
		log(Float.floatToRawIntBits(Float.NaN));
		log(Float.floatToIntBits(Float.NEGATIVE_INFINITY));
		log(Float.floatToRawIntBits(Float.NEGATIVE_INFINITY));
		log(Float.floatToIntBits(Float.POSITIVE_INFINITY));
		log(Float.floatToRawIntBits(Float.POSITIVE_INFINITY));
	}
}
