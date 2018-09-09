package com.itscoder.allenwu.eventbuskt

import com.itscoder.allenwu.eventbuskt.EventBus.DEFAULT_TAG

/**
 * 事件接受方法的注解类
 */
@Target(AnnotationTarget.FUNCTION) // 在方法上使用
@Retention(AnnotationRetention.RUNTIME) // 运行时注解，因为要使用反射
annotation class Subscriber(val tag: String = DEFAULT_TAG, val mode: ThreadMode = ThreadMode.POSTING)
