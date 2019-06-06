package org.jack.common.logger;

import java.util.Stack;

public class StackLogger<T,S> extends Stack<T>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private S s;
	public S getS() {
		return s;
	}
	public void setS(S s) {
		this.s = s;
	}
	
}
