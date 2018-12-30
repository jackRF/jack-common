package org.jack.common.util;
/**
 * 借款标记
 * @author zhangwei
 *
 */
public class LoanFlag extends AbstractBitFlag<LoanFlag>{
	public static final LoanFlag EMPTY=new LoanFlag(0l);
	public static final LoanFlag APPLY_SMS=new LoanFlag(1l);
	public static final LoanFlag HIGH_QUALITY=new LoanFlag(4l);
	private static final LoanFlag[] UNIT_FLAGS=new LoanFlag[] {
			APPLY_SMS,HIGH_QUALITY
	};
	private static final LoanFlag ALL=new LoanFlag(mergeValue(UNIT_FLAGS));
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
		LoanFlag[] unitFlags=UNIT_FLAGS;
		
		for(LoanFlag flag:unitFlags) {
			if(equals(flag.value,value)) {
				return flag;
			}
		}
		return null;
	}
	public static LoanFlag[] unitFlags() {
		return UNIT_FLAGS.clone();
	}
}
