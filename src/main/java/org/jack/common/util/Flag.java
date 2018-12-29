package org.jack.common.util;

import java.util.ArrayList;
import java.util.List;
/**
 * 标记
 * @author ym10177
 *
 */
public class Flag {
	/**
	 * 空标记
	 */
	public static final Flag EMPTY=new Flag(0);
	/**
	 * 进件短信标记
	 */
	public static final Flag APPLY_SMS=new Flag(1);
	/**
	 * High-Quality标记
	 */
	public static final Flag HIGH_QUALITY=new Flag(2);
	public static List<Flag> unitFlags(){
		List<Flag> list=new ArrayList<Flag>();
		list.add(APPLY_SMS);
		list.add(HIGH_QUALITY);
		return list;
	}
	/**
	 * 标记类型多个可以用|合并
	 */
	private final long type;
	
	private Flag(long type){
		this.type=type;
	}
	/**
	 * 是否有标记
	 * @param checkFlags
	 * @return
	 */
	public boolean hasFlag(Flag...checkFlags){
		return hasFlag(merge(checkFlags));
	}
	/**
	 * 是否有标记
	 * @param checkFlag
	 * @return
	 */
	public boolean hasFlag(Flag checkFlag){
		long checkType=checkFlag.type;
		return (type&checkType)==checkType;
	}
	/**
	 * 添加标记
	 * @param flags
	 * @param flag
	 * @return
	 */
	public Flag mark(Flag flag,Flag...markFlags){
		return mark(flag,merge(markFlags));
	}
	/**
	 * 添加标记
	 * @param flag
	 * @param markFlag
	 * @return
	 */
	public synchronized Flag mark(Flag flag,Flag markFlag) {
		return typeOf(flag.type|markFlag.type);
	}
	/**
	 * 取消标记
	 * @param flags
	 * @param flag
	 * @return
	 */
	public Flag unMark(Flag flag,Flag...unFlags){
		return unMark(flag,merge(unFlags));
	}
	/**
	 * 取消标记
	 * @param flag
	 * @param unflag
	 * @return
	 */
	public synchronized Flag unMark(Flag flag,Flag unflag){
		return typeOf(flag.type&(~unflag.type));
	}
	public synchronized long getType() {
		return type;
	}
	private static Flag merge(Flag...flags) {
		long type=0;
		for(Flag flag:flags){
			type|=flag.type;
		}
		return typeOf(type);
	}
	public static Flag typeOf(long type) {
		if(type<0) {
			throw new IllegalStateException("无效的状态 type:"+type);
		}
		if(type==0){
			return EMPTY;
		}
		for(Flag unitFlag:unitFlags()) {
			if(type==unitFlag.type){
				return unitFlag;
			}
		}
		return new Flag(type);
	}
}
