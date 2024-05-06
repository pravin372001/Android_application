package com.example.recyclerview

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList:ArrayList<Data>
    private lateinit var imageList: Array<Int>
    private lateinit var nameList: Array<String>
    private lateinit var timeList: Array<String>
    private lateinit var messageList: Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageList = arrayOf(
            R.drawable.nagaraj,
            R.drawable.hari,
            R.drawable.ameen,
            R.drawable.santhosh,
            R.drawable.samy,
            R.drawable.pravin,
        )

        nameList = arrayOf(
            "Nagaraj",
            "Hari",
            "Ameen",
            "Santhosh",
            "Samy Nathan",
            "Jothipravin"
        )

        timeList = arrayOf(
            "3:55",
            "4:35",
            "4:55",
            "7:01",
            "8:05",
            "12:30",
        )

        messageList = arrayOf(
            "Meeting yappo vaikalam",
            "Assignment send panu",
            "evalo module over?",
            "enna enna pojo create panra",
            "Broooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo",
            "Hi Bro"
        )
        recyclerView = findViewById(R.id.recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        dataList = arrayListOf()
        getDataList()

    }

    private fun getDataList(){
        for(i in imageList.indices){
            val data = Data(imageList[i], nameList[i], timeList[i], messageList[i])
            dataList.add(data)
        }
        recyclerView.adapter = AdapterClass(dataList)
    }
}