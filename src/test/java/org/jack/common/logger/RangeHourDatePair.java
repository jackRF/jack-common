package org.jack.common.logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jack.common.util.DateUtils;

public class RangeHourDatePair extends DatePair{
	private Date start;
	private Date end;
	public RangeHourDatePair() {
		this(new Date());
	}
	public RangeHourDatePair(Date start) {
		this.start=start;
	}
	public RangeHourDatePair(Date start,Date end) {
		this.start=start;
		this.end=end;
	}
	@Override
	public List<String> dateParts() {
		Date base=null;
		if(end==null){
			base=DateUtils.addHour(new Date(), 1);
		}else{
			base=DateUtils.addHour(end, 1);
		}
		List<String> list=new ArrayList<String>();
		Date use=start;
		while(use.compareTo(base)<0){
			list.add(DateUtils.formatDate(use, "yyyy-MM-dd-HH"));
			use=DateUtils.addHour(use, 1);
		}
		return list;
	}

}
