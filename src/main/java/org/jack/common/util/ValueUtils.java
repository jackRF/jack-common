package org.jack.common.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValueUtils {
	public static <T> T defaultValue(T t, T def) {
		return t == null ? def : t;
	}

	public static String percent(double decimal, int fraction) {
		// 获取格式化对象
		NumberFormat nt = NumberFormat.getPercentInstance();
		// 设置百分数精确度2即保留两位小数
		nt.setMinimumFractionDigits(fraction);
		return nt.format(decimal);
	}

	public static BigDecimal decimalAdd(BigDecimal dec1, BigDecimal dec2) {
		if (dec1 == null || dec2 == null) {
			return dec1 == null ? dec2 : dec1;
		}
		return dec1.add(dec2);
	}

	public static int numberAdd(Integer... nums) {
		int result = 0;
		for (Integer num : nums) {
			if (num != null) {
				result += num.intValue();
			}
		}
		return result;

	}

	public static BigDecimal max(BigDecimal dec1, BigDecimal dec2) {
		if (dec1 == null || dec2 == null) {
			return dec1 == null ? dec2 : dec1;
		}
		return dec1.compareTo(dec2) >= 0 ? dec1 : dec2;
	}

	public static BigDecimal min(BigDecimal dec1, BigDecimal dec2) {
		if (dec1 == null || dec2 == null) {
			return dec1 == null ? dec2 : dec1;
		}
		return dec1.compareTo(dec2) <= 0 ? dec1 : dec2;
	}

	public static <K, V, R> Map<K, R> listToMap(List<V> list,
			ItemStrategy<K, V, R> itemStrategy) {
		Map<K, R> map = new HashMap<K, R>();
		for (V v : list) {
			K k = itemStrategy.getKey(v);
			R r;
			if (!map.containsKey(k)) {
				r = itemStrategy.whenNewKey(v);
				map.put(k, r);
			} else {
				r = map.get(k);
			}
			itemStrategy.onValue(r, v);
		}
		return map;
	}

	public static <K, T> Map<K, List<T>> listToMap(List<T> list,
			final KeyStrategy<K, T> keyStrategy) {
		return ValueUtils.listToMap(list,
				new ValueUtils.ItemStrategy<K, T, List<T>>() {

					@Override
					public K getKey(T v) {
						return keyStrategy.getKey(v);
					}

					@Override
					public List<T> whenNewKey(T v) {
						return new ArrayList<T>();
					}

					@Override
					public void onValue(List<T> r, T v) {
						r.add(v);
					}
				});
	}

	public static interface KeyStrategy<K, V> {
		K getKey(V v);
	}

	public static interface ItemStrategy<K, V, R> extends KeyStrategy<K, V> {
		R whenNewKey(V v);

		void onValue(R r, V v);
	}
}
