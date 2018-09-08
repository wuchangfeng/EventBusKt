package com.itscoder.allenwu.eventbuskt

class TestEvent(text: String): IEvent(){
    var mText: String = text
    override fun toString(): String {
        return mText
    }
}
