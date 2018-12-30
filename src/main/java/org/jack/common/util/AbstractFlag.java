package org.jack.common.util;
/**
 * 抽象标记
 * @author zhangwei
 *
 * @param <T>
 * @param <V>
 */
public abstract class AbstractFlag<T extends AbstractFlag<T,V>,V> {
	protected final V value;
	protected AbstractFlag(V value){
		this.value=value;
	}
	/**
	 * 是否有标记
	 * @param flags
	 * @return
	 */
	public boolean hasAnly(@SuppressWarnings("unchecked") T...flags){
		for(T flag:flags) {
			if(has(flag)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 是否有标记
	 * @param flags
	 * @return
	 */
	public boolean has(@SuppressWarnings("unchecked") T...flags){
		return has(merge(flags));
	}
	/**
	 * 是否有标记
	 * @param flag
	 * @return
	 */
	public abstract boolean has(T flag);
	/**
	 * 添加标记
	 * @param flags
	 * @return
	 */
	public T mark(@SuppressWarnings("unchecked") T...flags){
		return mark(merge(flags));
	}
	/**
	 * 添加标记
	 * @param flag
	 * @return
	 */
	public abstract T mark(T flag);
	/**
	 * 取消标记
	 * @param flags
	 * @return
	 */
	public T unMark(@SuppressWarnings("unchecked") T...flags){
		return unMark(merge(flags));
	}
	/**
	 * 取消标记
	 * @param flag
	 * @return
	 */
	public abstract T unMark(T flag);
	/**
	 * 合并标记
	 * @param flags
	 * @return
	 */
	protected abstract T merge(@SuppressWarnings("unchecked") T...flags);
	public V getValue() {
		return value;
	}
	protected abstract T valueOf0(V value);
	@Override
	public boolean equals(Object obj) {
		if(obj==null) {
			return false;
		}
		if(obj==this) {
			return true;
		}
		if(this.getClass().isInstance(obj)) {
			@SuppressWarnings("unchecked")
			T flag=(T)obj;
			return equals(value,flag.value);
		}
		return false;
	}
	@Override
	public int hashCode() {
		if(value==null) {
			return 0;
		}
		return value.hashCode();
	}
	protected static <V> boolean equals(V v1,V v2) {
		if(v1==v2) {
			return true;
		}else if(v1==null||v2==null) {
			return false;
		}
		return v1.equals(v2);
	}
}
