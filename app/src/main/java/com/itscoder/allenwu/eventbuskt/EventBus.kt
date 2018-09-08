package com.itscoder.allenwu.eventbuskt

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object EventBus {
    private val executorService: ExecutorService = Executors.newCachedThreadPool()
    private const val DEFAULT_TAG = ""
    // 根据事件类型，找到所有对应的注解方法
    private val subscriberMap = mutableMapOf<EventType,CopyOnWriteArrayList<Subscription>>()
    private val methodFinder = SubscriberMethodFinder(subscriberMap)

    /**
     * 注册观察者，类似查找出当前 Activity 中被@Subscriber关键字来标记方法
     */
    fun register(obj: Any) = executorService.execute(){
        methodFinder.findSubscribeMethods(obj)
    }

    /**
     * 分发事件
     */
    @JvmOverloads
    fun post(event: IEvent,tag: String = DEFAULT_TAG){
        val eventType = EventType(event.javaClass,tag)
        // 找出所有该事件的订阅者
        val list = methodFinder.getMatchEventType(eventType)
        // 分发
        list?.let{
            EventDispatcher.dispatchEvent(event, it)
        }
    }

    /**
     * 注销观察者
     */
    fun unregister(obj: Any) = executorService.execute {
        methodFinder.removeSubscriberMethod(obj)
    }

    fun getExecutorService() = executorService
}