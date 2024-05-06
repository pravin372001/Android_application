package com.example.todo.addtodo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todo.R
import com.example.todo.database.TodoDatabase
import com.example.todo.databinding.FragmentAddTodoScreenBinding

class AddTodoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentAddTodoScreenBinding =DataBindingUtil.inflate(inflater, R.layout.fragment_add_todo_screen, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = TodoDatabase.getInstance(application).todoDatabaseDao

        val viewModelFactory = AddTodoViewModelFactory(dataSource, application)

        val viewModel = ViewModelProvider(this, viewModelFactory)[AddTodoViewModel::class.java]
        binding.addTodo = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }
}