package org.jack.common.core;


import org.springframework.data.jpa.domain.Specification;

public class Specifications{
    public static <T> Specification<T> like(String name, String value) {
        return (root, query, cb) ->
                cb.like(root.get(name), value);
    }
    
    public static <T> Specification<T> notLike(String name, String value) {
        return (root, query, cb) ->
                cb.like(root.get(name), value).not();
    }

    public static <T> Specification<T> eq(String name, Object value) {
        return (root, query, cb) ->
                cb.equal(root.get(name), value);
    }

    public static <T> Specification<T> notEq(String name, Object value) {
        return (root, query, cb) ->
                cb.notEqual(root.get(name), value);
    }

    public static <T,Y extends Comparable<Y>> Specification<T> gt(String name, Y value) {
        return (root, query, cb) ->
                cb.greaterThan(root.get(name), value);
    }

    public static <T,Y extends Comparable<Y>> Specification<T> gtEq(String name, Y value) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get(name), value);
    }

    public static <T,Y extends Comparable<Y>> Specification<T> lt(String name, Y value) {
        return (root, query, cb) ->
                cb.lessThan(root.get(name), value);
    }

    public static <T,Y extends Comparable<Y>> Specification<T> ltEq(String name, Y value) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get(name), value);
    }

    public static <T> Specification<T> isNull(String name) {
        return (root, query, cb) ->
                cb.isNull(root.get(name));
    }

    public static <T> Specification<T> notNull(String name) {
        return (root, query, cb) ->
                cb.isNotNull(root.get(name));
    }

    public static <T> Specification<T> in(String name, Object...value) {
        return (root, query, cb) ->
                root.get(name).in(value);
    }

    public static <T> Specification<T> notIn(String name, Object...value) {
        return (root, query, cb) ->
                root.get(name).in(value).not();
    }
}