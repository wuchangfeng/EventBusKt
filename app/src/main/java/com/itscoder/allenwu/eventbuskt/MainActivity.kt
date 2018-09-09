package com.itscoder.allenwu.eventbuskt

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.register(this)

        findViewById<Button>(R.id.btn_1).setOnClickListener(View.OnClickListener {
            Thread{
                EventBus.post(TestEvent("Hello,POST"))
            }.start()
        })

        findViewById<Button>(R.id.btn_2).setOnClickListener(View.OnClickListener {
            EventBus.post(TestEvent("Hello,ASYNC"))
        })

        findViewById<Button>(R.id.btn_3).setOnClickListener(View.OnClickListener {
            EventBus.post(TestEvent("Hello,MAIN"))
        })

        findViewById<Button>(R.id.btn_4).setOnClickListener(View.OnClickListener {
            EventBus.post(TestEvent("Hello,TAG"),"test")
        })
    }

    @Subscriber
    fun test(event: TestEvent){
        Log.i("MainActivity",event.toString() + "currentThread:" + Thread.currentThread().name)
    }

    @Subscriber("",ThreadMode.ASYNC)
    fun test2(event: TestEvent){
        Log.i("MainActivity",event.toString() + "currentThread:" + Thread.currentThread().name)
    }

    @Subscriber("",ThreadMode.MAIN)
    fun test3(event: TestEvent){
        Log.i("MainActivity",event.toString() + "currentThread:" + Thread.currentThread().name)
    }

    @Subscriber(tag = "test")
    fun test5(event: TestEvent){
        Log.i("MainActivity",event.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.unregister(this)
    }
}
