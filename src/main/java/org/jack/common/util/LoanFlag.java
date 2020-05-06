package org.jack.common.util;

import org.jack.common.core.AbstractBitFlag;

/**
 * 借款标记
 * 
 * @author zhangwei
 *
 */
public class LoanFlag extends AbstractBitFlag<LoanFlag>{

	private static enum FlagHolder{
		EMPTY(new LoanFlag(0l)),
		APPLY_SMS(new LoanFlag(1l)),
		HIGH_QUALITY(new LoanFlag(4l));
		private LoanFlag flag;
		private FlagHolder(LoanFlag flag){
			this.flag=flag;
		}
	}
	public static final LoanFlag EMPTY=FlagHolder.EMPTY.flag;
	public static final LoanFlag APPLY_SMS=FlagHolder.APPLY_SMS.flag;
	public static final LoanFlag HIGH_QUALITY=FlagHolder.HIGH_QUALITY.flag;
	private static final LoanFlag ALL=new LoanFlag(mergeValue(unitFlags()));
	protected LoanFlag(Long value) {
		super(value);
	}
	@Override
	protected LoanFlag valueOf0(Long value) {
		LoanFlag flag= useSingle(value);
		return flag==null?new LoanFlag(value):flag;
	}
	
	public static LoanFlag valueOf(Long value) {
		if(value==null||value<0||value>ALL.value) {
			throw new IllegalStateException("无效的状态 value:"+value);
		}
		LoanFlag flag= useSingle(value);
		if(flag!=null) {
			return flag;
		}
		flag=new LoanFlag(value);
		if(!EMPTY.equals(flag.unMark(ALL))) {
			throw new IllegalStateException("无效的状态 value:"+value);
		}
		return flag;
	}
	private static LoanFlag useSingle(Long value) {
		if(equals(EMPTY.value,value)) {
			return EMPTY;
		}
		if(equals(ALL.value,value)) {
			return ALL;
		}
		for(FlagHolder flagHolder:FlagHolder.values()) {
			if(equals(flagHolder.flag.value,value)) {
				return flagHolder.flag;
			}
		}
		return null;
	}
	public static LoanFlag[] unitFlags() {
		int i=0;
		LoanFlag[] unitFlags=new LoanFlag[FlagHolder.values().length];
		for(FlagHolder flagHolder:FlagHolder.values()) {
			unitFlags[i++]=flagHolder.flag;
		}
		return unitFlags;
	}
}
