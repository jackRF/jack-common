package org.jack.common;

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
