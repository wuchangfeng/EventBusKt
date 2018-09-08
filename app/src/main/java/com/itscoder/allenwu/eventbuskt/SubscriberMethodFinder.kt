package com.itscoder.allenwu.eventbuskt

import java.lang.ref.WeakReference
import java.lang.reflect.Modifier
import java.util.concurrent.CopyOnWriteArrayList

internal class SubscriberMethodFinder(private val subscriberMap: MutableMap<EventType, CopyOnWriteArrayList<Subscription>>){

    companion object {
        private const val BRIDGE = 0x40
        private const val SYNTHETIC = 0x1000
        private const val MODIFIERS_IGNORE = Modifier.ABSTRACT or Modifier.STATIC or BRIDGE or SYNTHETIC
    }

    @Synchronized
    fun findSubscribeMethods(subscriber: Any) {
        var clazz: Class<*>? = subscriber.javaClass
        while (clazz != null && !isSystemClass(clazz.name)) {
            var methods = try {
                // 返回所有的方法，包括public/private/protected/default
                clazz.declaredMethods
            } catch (e: Exception) {
                // public方法
                clazz.methods
            }
            for (method in methods) {
                val modifiers = method.modifiers
                // 过滤方法的修饰符
                if (Modifier.PUBLIC and modifiers != 0 && modifiers and MODIFIERS_IGNORE == 0) {
                    // 获取到注解
                    val annotation = method.getAnnotation(Subscriber::class.java)
                    // 如果注解不为空
                    if (annotation != null) {
                        val parameterTypes = method.parameterTypes
                        // 方法只接收一个参数
                        parameterTypes?.let {
                            if (parameterTypes.size == 1) {
                                var type = parameterTypes[0]
                                // 判断是否实现 IEvent 接口
                                if (isAssignableFrom(IEvent::class.java, type)) {
                                    val eventType = EventType(type as Class<IEvent>, annotation.tag)
                                    val subscription = Subscription(WeakReference(subscriber), method, annotation.mode)
                                    // 保存订阅信息
                                    subscribe(eventType, subscription)
                                }
                            }
                        }
                    }
                }
            }
            clazz = clazz.superclass
        }
    }

    /**
     * 根据事件Type来确定一对多的关系
     */
    @Synchronized
    private fun subscribe(type: EventType, subscription: Subscription) {
        var subscriptionLists: CopyOnWriteArrayList<Subscription>? = getMatchEventType(type)
        if (subscriptionLists == null) {
            subscriptionLists = CopyOnWriteArrayList()
        }
        // 这就是为什么我们要重写equals和hashCode方法的原因
        if (subscriptionLists.contains(subscription)) {
            return
        }

        subscriptionLists.add(subscription)
        // 将事件类型key和订阅者信息存储到map中
        subscriberMap.put(type, subscriptionLists)
    }

    /**
     * 判断是否有已经存在的EventType
     */
    internal fun getMatchEventType(type: EventType): CopyOnWriteArrayList<Subscription>? {
        val keys = subscriberMap.keys
        return keys.firstOrNull { it == type }?.let { subscriberMap[it] }
    }

    @Synchronized
    fun removeSubscriberMethod(subscriber: Any) {
        // 注意删除的时候要使用迭代器
        var iterator = subscriberMap.values.iterator()
        while (iterator.hasNext()) {
            val subscriptions: MutableList<Subscription>? = iterator.next()
            subscriptions?.let { it: MutableList<Subscription>? ->
                val subIterator = it!!.iterator()
                while (subIterator.hasNext()) {
                    val subscription = subIterator.next()
                    // 获取引用
                    val cacheObject = subscription.subscriber.get()
                    cacheObject?.let {
                        if (isSameObject(cacheObject, subscriber)) {
                            subscriptions.remove(subscription)
                        }
                    }
                }
            }

            // 如果针对某个Event的订阅者数量为空了,那么需要从map中清除
            if (subscriptions == null || subscriptions.isEmpty()) {
                iterator.remove()
            }
        }
    }

    // 判断是否是同一个对象
    private fun isSameObject(subOne: Any, subTwo: Any) = subOne === subTwo

    // 判断类是否为系统类
    private fun isSystemClass(clazzName: String): Boolean {
        if (clazzName.startsWith("java.") || clazzName.startsWith("javax.") || clazzName.startsWith("android.")) {
            return true
        }
        return false
    }

    // 判断某个类是否实现了IEvent接口
    private fun isAssignableFrom(a: Class<*>, b: Class<*>): Boolean = a.isAssignableFrom(b)
}