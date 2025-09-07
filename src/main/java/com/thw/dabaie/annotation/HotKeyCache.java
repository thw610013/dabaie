package com.thw.dabaie.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HotKeyCache {

    /**
     * 缓存 key 模板（支持 SpEL 表达式）
     * 示例: "question_#{#id}" 或 "bank_detail_#{#request.id}"
     */
    String key();

    /**
     * 缓存结果类型（可选）
     * 如果不写，默认用方法返回值类型
     */
    Class<?> type() default Object.class;
}
