package com.example.navigationassignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.Navigation

class FirstFragment : Fragment() {

    private lateinit var name:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_first, container, false)

        view.findViewById<Button>(R.id.button).setOnClickListener {
            name = view.findViewById<TextView>(R.id.name).text.toString()
            val action = FirstFragmentDirections.actionFirstFragmentToSecondFragment(name)
            Navigation.findNavController(view).navigate(action)
        }

        return view
    }
}