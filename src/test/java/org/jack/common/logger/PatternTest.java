package org.jack.common.logger;

import java.util.Random;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatternTest {
	private final Logger logger=LoggerFactory.getLogger(getClass());
	@Test
	public void testa() throws InterruptedException {
		logger.info("sfsfsfs");
		Thread.sleep(new Random().nextInt(1000));
		logger.info("aaa");
		Thread.sleep(new Random().nextInt(1000));
		logger.debug("sdfsfsf");

	}
}
