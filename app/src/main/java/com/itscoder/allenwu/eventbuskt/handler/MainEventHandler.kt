package com.itscoder.allenwu.eventbuskt.handler

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.itscoder.allenwu.eventbuskt.IEvent
import com.itscoder.allenwu.eventbuskt.PendingPost
import com.itscoder.allenwu.eventbuskt.PendingPostQueue
import com.itscoder.allenwu.eventbuskt.Subscription

class MainEventHandler(looper: Looper) : Handler(looper), EventHandler {
    private val queue: PendingPostQueue = PendingPostQueue()
    private var handlerActive = false
    override fun handleMessage(msg: Message?) {
        while (true) {
            var pendingPost = queue.poll()
            if (pendingPost == null) {
                synchronized(this) {
                    pendingPost = queue.poll()
                    if (pendingPost == null) {
                        handlerActive = false
                        return
                    }
                }
            }
            pendingPost!!.subscription!!.invoke(pendingPost!!.event!!)
            PendingPost.releasePendingPost(pendingPost!!)
        }
    }

    override fun handleEvent(subscription: Subscription, event: IEvent) {
        val post = PendingPost.obtainPendingPost(subscription, event)
        synchronized(this) {
            queue.enqueue(post)
            if (!handlerActive) {
                handlerActive = true
                sendMessage(Message.obtain())
            }

        }
    }
}
