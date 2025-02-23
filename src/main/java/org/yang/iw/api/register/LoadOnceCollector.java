package org.yang.iw.api.register;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * 该类是信息收集类，但有特殊的要求。
 * <p>
 * 	- 只收集信息，不进行注册
 * 	- 需要在初始化时加载，以处理 LoadOnceRegistryEntry
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface LoadOnceCollector
{}
