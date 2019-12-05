package com.schooltraining.storesdistribution.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)//注意这里不能用这个 @Retention(RetentionPolicy.SOURCE)，反射得不到！！！
public @interface LoginRequired {

    boolean loginSuccess() default true;

}