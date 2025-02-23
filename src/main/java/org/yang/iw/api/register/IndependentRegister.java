package org.yang.iw.api.register;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * 该类是独立注册类。
 * <p>
 * 	- 需要在初始化时调用 initialize 来确保完成注册
 * <p>
 * 	- 加载该类与其他类的顺序不会影响其内容
 * 	- 这通常是因为该类的加载不依赖于其它类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface IndependentRegister
{}