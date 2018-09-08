package com.itscoder.allenwu.eventbuskt

class PendingPost(var event: IEvent?, var subscription: Subscription?, var next: PendingPost? = null) {

    companion object {
        private val pool = arrayListOf<PendingPost>()

        @JvmStatic
        fun obtainPendingPost(subscription: Subscription, event: IEvent): PendingPost {
            if (pool.size > 0) {
                val pendingPost = pool.removeAt(pool.size - 1)
                pendingPost.next = null
                pendingPost.subscription = subscription
                pendingPost.event = event
                return pendingPost
            }
            return PendingPost(event, subscription)
        }

        @JvmStatic
        fun releasePendingPost(pendingPost: PendingPost) {
            pendingPost.event = null
            pendingPost.subscription = null
            pendingPost.next = null
            synchronized(pool) {
                if (pool.size < 1000) {
                    pool.add(pendingPost)
                }
            }
        }
    }
}
