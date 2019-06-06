package org.jack.common.logger.task;

public interface Filter<E>{
	boolean export();
	boolean filter(E e);
}
