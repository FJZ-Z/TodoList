package com.fjz.todolist.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fjz.todolist.databinding.ActivityMainBinding
import com.fjz.todolist.datasource.TaskDataSource

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy { TaskListAdapter() }
    private var registerA = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            updateList()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvTasks.adapter = adapter
        updateList()
        insertListeners()
    }
    override fun onResume() {
        super.onResume()
        updateList()
    }
    private fun insertListeners(){
        binding.fab.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            registerA.launch(intent)
            //startActivityForResult(intent, CREATE_NEW_TASK) //deprecated
        }
        adapter.listenerEdit={
            val intent = Intent(this, AddTaskActivity::class.java)
            intent.putExtra(AddTaskActivity.TASK_ID,it.id)
            registerA.launch(intent)
            //startActivityForResult(intent, CREATE_NEW_TASK) //deprecated
        }
        adapter.listenerDelete = {
            TaskDataSource.deleteTask(it)
            updateList()
        }
    }
/*    override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?){
        super.onActivityResult(requestCode,resultCode,data)
        if (requestCode == CREATE_NEW_TASK && resultCode ==Activity.RESULT_OK){
            updateList()
        }
    }*/ //deprecated
    private fun updateList(){
        val list = TaskDataSource.getList()
        if (list.isEmpty()){
            binding.includeEmpty.emptyState.visibility=View.VISIBLE
        } else{
            binding.includeEmpty.emptyState.visibility=View.GONE
        }
        if(adapter.currentList.size > 0) adapter.submitList(null)
        adapter.submitList(list)
        binding.rvTasks.adapter = adapter
    }
    companion object{
        private const val CREATE_NEW_TASK = 1000
    }
}