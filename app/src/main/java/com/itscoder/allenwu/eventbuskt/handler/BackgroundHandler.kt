package com.itscoder.allenwu.eventbuskt.handler

import com.itscoder.allenwu.eventbuskt.*

/**
 * 后台线程
 */
class BackgroundHandler : Runnable, EventHandler {

    private val queue: PendingPostQueue = PendingPostQueue()

    @Volatile
    private var executorRunning: Boolean = false

    override fun handleEvent(subscription: Subscription, event: IEvent) {
        val pendingPost = PendingPost.obtainPendingPost(subscription, event)
        synchronized(this) {
            queue.enqueue(pendingPost)
            if (!executorRunning) {
                executorRunning = true
                EventBus.getExecutorService().execute(this)
            }
        }
    }

    override fun run() = try {
        try {
            while (true) {
                var pendingPost = queue.poll(1000)
                if (pendingPost == null) {
                    synchronized(this) {
                        pendingPost = queue.poll()
                        if (pendingPost == null) {
                            executorRunning = false
                            return
                        }
                    }
                }
                pendingPost!!.subscription!!.invoke(pendingPost!!.event!!)
                PendingPost.releasePendingPost(pendingPost!!)
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    } finally {
        executorRunning = false
    }
}
