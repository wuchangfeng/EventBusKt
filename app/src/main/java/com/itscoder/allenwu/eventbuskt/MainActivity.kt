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

        findViewById<Button>(R.id.btn).setOnClickListener(View.OnClickListener {
            EventBus.post(TestEvent("Hello,EventBus"))
        })
    }

    @Subscriber("")
    fun test(event: TestEvent){
        Log.i("MainActivity",event.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.unregister(this)
    }
}
