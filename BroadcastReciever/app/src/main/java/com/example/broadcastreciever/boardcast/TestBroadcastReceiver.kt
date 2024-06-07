package com.example.broadcastreciever.boardcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class TestBroadcastReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == "TEST_ACTION"){
            println("Test broadcast received")
            Toast.makeText(context, "Test broadcast received", Toast.LENGTH_SHORT).show()
            Log.d("TestBroadcastReceiver", "Test broadcast received")
        }
    }
}