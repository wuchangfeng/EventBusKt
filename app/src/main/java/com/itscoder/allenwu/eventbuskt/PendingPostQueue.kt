package com.itscoder.allenwu.eventbuskt

class PendingPostQueue{
    private var head: PendingPost? = null
    private var tail: PendingPost? = null

    @Synchronized
    fun enqueue(post: PendingPost) = when {
        tail != null -> {
            tail!!.next = post
            tail = post
        }
        head == null -> {
            head = post
            tail = post
        }
        else -> throw IllegalStateException("Head present, but no tail")
    }

    @Synchronized
    fun poll(): PendingPost? {
        val post = head
        if (head != null) {
            head = head!!.next
            if (head == null) {
                tail = null
            }
        }
        return post
    }

    @Synchronized
    @Throws(InterruptedException::class)
    fun poll(maxMillisToWait: Int): PendingPost? {
        if (head == null) {
            Thread.sleep(maxMillisToWait.toLong())
        }
        return poll()
    }
}