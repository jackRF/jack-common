package org.jack.common.logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class DatePair {
	public static final DatePair EMPTY=new EmptyDatePair();
	public abstract List<String> dateParts();
	public static class EmptyDatePair extends DatePair{
		private final List<String> list;
		private EmptyDatePair() {
			List<String> temp=new ArrayList<String>();
			temp.add("");
			list=Collections.unmodifiableList(temp);
		}
		@Override
		public List<String> dateParts() {
			return list;
		}
	}
	
}
