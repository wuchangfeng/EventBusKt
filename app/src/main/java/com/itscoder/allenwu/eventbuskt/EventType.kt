package com.itscoder.allenwu.eventbuskt

/**
 * 确定唯一Key，记录对应的事件执行方法
 */
internal class EventType(private val eventClass: Class<*>, private val tag: String){
    override fun equals(other: Any?): Boolean {

        if (this === other){
            return true
        }
        // 判断是否为空，是否属于同一种类型
        if (other == null || (other.javaClass.name !== this.javaClass.name)) {
            return false
        }

        // 能执行到这里，说明 obj 和 this 同类且非 null
        val eventType = other as EventType
        val tagJudge = tag == eventType.tag
        val eventJudge = eventClass.name == eventType.eventClass.name
        // EventType是同一个类型
        return tagJudge && eventJudge
    }

    override fun hashCode(): Int {
        var hash = 7
        hash = hash * 31 + eventClass.hashCode()
        hash = hash * 31 + tag.hashCode()
        return hash
    }
}