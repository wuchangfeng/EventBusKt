package com.itscoder.allenwu.eventbuskt

import android.os.Looper
import com.itscoder.allenwu.eventbuskt.handler.*
import java.util.concurrent.CopyOnWriteArrayList

object EventDispatcher {

    private val postHandler = PostEventHandler()
    private val mainHandler = MainEventHandler(Looper.getMainLooper())
    private val asyncHandler = AsyncEventHandler()
    private val bgHandler = BackgroundHandler()

    fun dispatchEvent(event: IEvent, list: CopyOnWriteArrayList<Subscription>) =
            list.forEach { it: Subscription ->
                it.let {
                    val subscriber = it.subscriber.get()
                    subscriber?.let { subscriber: Any ->
                        val eventHandler = getEventHandler(it.threadMode)
                        eventHandler.handleEvent(it, event)
                    }
                }
            }

    // 很据ThreadMode获取对应的事件处理器
    private fun getEventHandler(mode: ThreadMode): EventHandler = when (mode) {
        ThreadMode.POSTING -> postHandler
        ThreadMode.ASYNC -> asyncHandler
        ThreadMode.MAIN -> mainHandler
        ThreadMode.BACKGROUND -> bgHandler
    }
}
