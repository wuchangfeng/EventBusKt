package com.itscoder.allenwu.eventbuskt.handler

import com.itscoder.allenwu.eventbuskt.*

/**
 * 开线程
 */
class AsyncEventHandler: EventHandler {
    private val queue: PendingPostQueue = PendingPostQueue()

    override fun handleEvent(subscription: Subscription, event: IEvent) {
        val pendingPost = PendingPost.obtainPendingPost(subscription, event)
        queue.enqueue(pendingPost)
        EventBus.getExecutorService().execute {
            val pendingPost = queue.poll() ?: throw IllegalStateException("No pending post available")
            pendingPost.subscription!!.invoke(pendingPost!!.event!!)
            PendingPost.releasePendingPost(pendingPost)
        }
    }
}