package com.itscoder.allenwu.eventbuskt

import java.lang.ref.WeakReference
import java.lang.reflect.Method

/**
 * 事件执行方法包装类
 */
class Subscription(val subscriber: WeakReference<Any>,
                   private val targetMethod: Method,
                   val threadMode: ThreadMode) {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other == null || (other::class !== this::class)) {
            return false
        }

        val subscription = other as Subscription
        val judgeSubscriber = this.subscriber.get() === subscription.subscriber.get()
        val judgeMethod = this.targetMethod.name == subscription.targetMethod.name
        return judgeSubscriber && judgeMethod
    }

    override fun hashCode(): Int {
        var hash = 7
        hash = hash * 31 + subscriber.hashCode()
        hash = hash * 31 + targetMethod.hashCode()
        hash = hash * 31 + threadMode.hashCode()
        return hash
    }

    /**
     * 根据传入的实例，反射调用实例方法。
     */
    internal fun invoke(event: IEvent) {
        targetMethod.invoke(subscriber.get(), event)
    }
}
