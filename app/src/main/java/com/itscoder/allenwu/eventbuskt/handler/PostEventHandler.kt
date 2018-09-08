package com.itscoder.allenwu.eventbuskt.handler

import com.itscoder.allenwu.eventbuskt.IEvent
import com.itscoder.allenwu.eventbuskt.Subscription

/**
 * 当前事件发出的线程
 */
class PostEventHandler: EventHandler {
    override fun handleEvent(subscription: Subscription, event: IEvent) {
        subscription.invoke(event)
    }
}