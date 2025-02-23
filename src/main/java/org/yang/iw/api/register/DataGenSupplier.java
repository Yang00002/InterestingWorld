package org.yang.iw.api.register;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * 该类可以提供数据生成信息。
 * <p>
 * 	- 这意味着它需要提供至少一个入口来让数据生成加载它
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface DataGenSupplier
{}
