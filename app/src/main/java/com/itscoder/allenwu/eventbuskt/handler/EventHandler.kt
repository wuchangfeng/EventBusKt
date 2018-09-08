package com.itscoder.allenwu.eventbuskt.handler

import com.itscoder.allenwu.eventbuskt.IEvent
import com.itscoder.allenwu.eventbuskt.Subscription

interface EventHandler {
    fun handleEvent(subscription: Subscription, event: IEvent)
}