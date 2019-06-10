package com.github.zhou6ang.mvc.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.github.zhou6ang.mvc.util.HttpRequestMethod;

@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface BitRequestPath {
    String value();
    String method() default HttpRequestMethod.METHOD_GET;
    String[] reqHeader() default {};
    String[] resHeader() default {};
}
