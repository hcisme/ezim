package org.chc.ezim.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GlobalAccessInterceptor {
    boolean checkLoginAccess() default true;

    boolean checkAdminAccess() default false;
}
