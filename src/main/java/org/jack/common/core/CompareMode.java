package org.jack.common.core;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.jack.common.core.Logic.LogicNode;
import org.jack.common.util.ValueUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.StringUtils;

public enum CompareMode {
    EQ {
        public <T, W extends AbstractWrapper<T, String, W>> void apply(W wrapper, String column, Object... val) {
            wrapper.eq(column, val[0]);
        }
    },
    NE {
        public <T, W extends AbstractWrapper<T, String, W>> void apply(W wrapper, String column, Object... val) {
            wrapper.ne(column, val[0]);
        }
    },
    GT {
        public <T, W extends AbstractWrapper<T, String, W>> void apply(W wrapper, String column, Object... val) {
            wrapper.gt(column, val[0]);
        }
    },
    GE {
        public <T, W extends AbstractWrapper<T, String, W>> void apply(W wrapper, String column, Object... val) {
            wrapper.ge(column, val[0]);
        }
    },
    LT {
        public <T, W extends AbstractWrapper<T, String, W>> void apply(W wrapper, String column, Object... val) {
            wrapper.lt(column, val[0]);
        }
    },
    LE {
        public <T, W extends AbstractWrapper<T, String, W>> void apply(W wrapper, String column, Object... val) {
            wrapper.le(column, val[0]);
        }
    },
    BETWEEN {
        public <T, W extends AbstractWrapper<T, String, W>> void apply(W wrapper, String column, Object... val) {
            wrapper.between(column, val[0], val[1]);
        }
    },
    NOTBETWEEN {
        public <T, W extends AbstractWrapper<T, String, W>> void apply(W wrapper, String column, Object... val) {
            wrapper.notBetween(column, val[0], val[1]);
        }
    },
    ISNULL {
        public <T, W extends AbstractWrapper<T, String, W>> void apply(W wrapper, String column, Object... val) {
            wrapper.isNull(column);
        }
    },
    ISNOTNULL {
        public <T, W extends AbstractWrapper<T, String, W>> void apply(W wrapper, String column, Object... val) {
            wrapper.isNotNull(column);
        }
    },
    IN {
        public <T, W extends AbstractWrapper<T, String, W>> void apply(W wrapper, String column, Object... val) {
            if (val.length == 1) {
                if (val[0] instanceof Collection) {
                    wrapper.in(column, (Collection<?>) val[0]);
                    return;
                }
            }
            wrapper.in(column, val);
        }
    },
    NOTIN {
        public <T, W extends AbstractWrapper<T, String, W>> void apply(W wrapper, String column, Object... val) {
            if (val.length == 1) {
                if (val[0] instanceof Collection) {
                    wrapper.notIn(column, (Collection<?>) val[0]);
                    return;
                }
            }
            wrapper.notIn(column, val);
        }
    },
    LIKE {
        public <T, W extends AbstractWrapper<T, String, W>> void apply(W wrapper, String column, Object... val) {
            if (StringUtils.hasText(val[0].toString())) {
                wrapper.like(column, val[0]);
            }
        }
    },
    LIKE_LEFT {
        public <T, W extends AbstractWrapper<T, String, W>> void apply(W wrapper, String column, Object... val) {
            if (StringUtils.hasText(val[0].toString())) {
                wrapper.likeLeft(column, val[0]);
            }
        }
    },
    LIKE_RIGHT {
        public <T, W extends AbstractWrapper<T, String, W>> void apply(W wrapper, String column, Object... val) {
            if (StringUtils.hasText(val[0].toString())) {
                wrapper.likeRight(column, val[0]);
            }
        }
    },
    NOT_LIKE {
        public <T, W extends AbstractWrapper<T, String, W>> void apply(W wrapper, String column, Object... val) {
            if (StringUtils.hasText(val[0].toString())) {
                wrapper.notLike(column, val[0]);
            }
        }
    };

    public <T, W extends AbstractWrapper<T, String, W>> void apply(W wrapper, String column, Object... val) {
        throw new AbstractMethodError();
    }
    public static interface DataStrategy<D> {
        String apply(List<D> dataList);
    }
    public static <D, T, W extends AbstractWrapper<T, String, W>> void compile(W w, LogicNode node,
            DataStrategy<D> dataStrategy) {
          final int nodeType=node.getNodeType();
          final Object data=node.getData();
        if (nodeType == LogicNode.NODE_TYPE_DATA) {
            w.exists(dataStrategy.apply((List<D>) data));
        } else if (nodeType == LogicNode.NODE_TYPE_NOT) {
            QueryWrapper<String> not = new QueryWrapper<>();
            compile(not, (LogicNode) data, dataStrategy);
            w.apply("not(" + not.getSqlSegment() + ")");
        } else {
            List<LogicNode> list = (List<LogicNode>) data;
            if (nodeType == LogicNode.NODE_TYPE_OR) {
                w.and(new Function<W, W>() {
                    @Override
                    public W apply(W t) {
                        for (LogicNode compareNode : list) {
                            compile(t.or(), compareNode, dataStrategy);
                        }
                        return t;
                    }
                });
            } else {
                for (LogicNode logicNodeItem : list) {
                    compile(w, logicNodeItem, dataStrategy);
                }
            }
        }
    }
    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public static @interface Mode {
        CompareMode value() default CompareMode.EQ;
    }
    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public static @interface TargetCalss{
        Class<?> value();
    }

    public static class Condition {
        protected <T, W extends AbstractWrapper<T, String, W>> boolean customerCompare(W wrapper, String propertyName,
                Object propertyValue, String alias) {
            return false;
        }

        protected CompareMode useCompareMode(String propertyName, Object propertyValue, Field propertyField) {
            Class<?> propertyType=propertyField.getType();
            if (Boolean.class.equals(propertyType)) {
                if (Boolean.TRUE.equals(propertyValue)) {
                    return CompareMode.ISNOTNULL;
                } else if (Boolean.FALSE.equals(propertyValue)) {
                    return CompareMode.ISNULL;
                }
                return null;
            }
            if (propertyValue == null || propertyName.endsWith("CompareMode")
                    || (String.class.equals(propertyType) && !StringUtils.hasText((String) propertyValue))) {
                return null;
            }
            if (propertyName.endsWith("Begin")) {
                return CompareMode.GE;
            } else if (propertyName.endsWith("End")) {
                return CompareMode.LT;
            }
            Mode mode=propertyField.getAnnotation(Mode.class);
            if(mode!=null){
                return mode.value();
            }
            return CompareMode.EQ;
        }

        protected Class<?> useTargetCalzz(String propertyName, Field propertyField) {
            TargetCalss targetCalss=propertyField.getAnnotation(TargetCalss.class);
            if(targetCalss!=null){
                return targetCalss.value();
            }
            return propertyField.getDeclaringClass();
        }

        public <T, W extends AbstractWrapper<T, String, W>> void apply(W wrapper, Map<Class<?>, String> aliasMap) {
            BeanWrapperImpl beanWrapper = new BeanWrapperImpl(this);
            PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                Method method = pd.getReadMethod();
                if (method == null || Object.class.equals(method.getDeclaringClass())) {
                    continue;
                }
                String propertyName = pd.getName();
                Object propertyValue = beanWrapper.getPropertyValue(propertyName);
                Field propertyField=null;
                try {
                    propertyField = method.getDeclaringClass().getDeclaredField(propertyName);
                    if(propertyField==null){
                        continue;
                    }
                }catch (Exception e1) {
                    continue;
                }
                String alias ="";
                if(aliasMap!=null){
                    Class<?> clazz=useTargetCalzz(propertyName,propertyField);
                    if(aliasMap.containsKey(clazz)){
                        alias=aliasMap.get(clazz)+".";
                    }
                }
                if (!customerCompare(wrapper, propertyName, propertyValue, alias)) {
                    CompareMode compareMode=null;
                    if(beanWrapper.isReadableProperty(propertyName+"CompareMode")){
                        compareMode = (CompareMode)beanWrapper.getPropertyValue(propertyName+"CompareMode");
                    }else{
                        compareMode = useCompareMode(propertyName, propertyValue,propertyField);
                    }
                    if(compareMode!=null){
                        if(propertyValue==null&&!(CompareMode.ISNULL.equals(compareMode)||CompareMode.ISNOTNULL.equals(compareMode))){
                            continue;
                        }
                        String columnName = null;
                        TableField tableField=null;
                        try {
                            tableField =propertyField.getAnnotation(TableField.class);
                        }catch (Exception e) {
                        }
                        if(tableField!=null){
                            columnName=tableField.value();
                        }else{
                            if(propertyName.endsWith("Begin")){
                                columnName=ValueUtils.propertyToColumn(propertyName.substring(0, propertyName.length()-"Begin".length()));
                            }else if(propertyName.endsWith("End")){
                                columnName=ValueUtils.propertyToColumn(propertyName.substring(0, propertyName.length()-"End".length()));
                            }else{
                                columnName=ValueUtils.propertyToColumn(propertyName);
                            }
                        }
                        compareMode.apply(wrapper, alias+columnName, propertyValue);
                    }
                }
            }
        }
    }
}