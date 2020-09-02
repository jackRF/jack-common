package org.jack.common.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Monitor {
    String value()default "";
    @Target({ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public static @interface Ignore{

    }
}
