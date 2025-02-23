package org.yang.iw.api.register;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * 该类是依赖注册类。
 * <p>
 * 	- 需要在初始化时调用 initialize 来确保完成注册
 * <p>
 * 	- 该类加载前必须加载其依赖类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface DependRegister
{
	Class[] depends();
}