package org.jack.common.util;
/**
 * 抽象bit位标记
 * @author zhangwei
 *
 * @param <T>
 */
public abstract class AbstractBitFlag<T extends AbstractBitFlag<T>> extends  AbstractFlag<T,Long>{
	
	protected AbstractBitFlag(Long value) {
		super(value);
	}

	@Override
	public boolean has(T flag) {
		long cv=flag.value;
		return (value&cv)==cv;
	}

	@Override
	public T mark(T flag) {
		return valueOf0(value|flag.value);
	}

	@Override
	public T unMark(T flag) {
		return valueOf0(value&(~flag.value));
	}

	@Override
	protected T merge(@SuppressWarnings("unchecked") T... flags) {
		return valueOf0(mergeValue(flags));
	}
	protected static <T extends AbstractBitFlag<T>> long mergeValue(@SuppressWarnings("unchecked") T... flags) {
		long value=0;
		for(T flag:flags) {
			value|=flag.value;
		}
		return value;
	}
}
