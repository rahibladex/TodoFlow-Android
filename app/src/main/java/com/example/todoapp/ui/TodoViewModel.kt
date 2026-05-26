package com.example.todoapp.ui

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.example.todoapp.data.TodoRepository
import com.example.todoapp.data.TodoTask
import com.example.todoapp.widget.TodoWidgetProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TodoRepository(application)
    private val _tasks = MutableStateFlow<List<TodoTask>>(emptyList())
    val tasks: StateFlow<List<TodoTask>> = _tasks.asStateFlow()

    private val dataChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "com.example.todoapp.action.DATA_CHANGED") {
                _tasks.value = repository.loadTasks()
            }
        }
    }

    init {
        _tasks.value = repository.loadTasks()
        val filter = IntentFilter("com.example.todoapp.action.DATA_CHANGED")
        // Use ContextCompat to register the receiver correctly
        ContextCompat.registerReceiver(
            application,
            dataChangedReceiver,
            filter,
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    private fun updateWidgets() {
        val intent = Intent(getApplication(), TodoWidgetProvider::class.java).apply {
            action = "com.example.todoapp.action.WIDGET_UPDATE"
        }
        getApplication<Application>().sendBroadcast(intent)
    }

    fun addTask(title: String) {
        if (title.isBlank()) return
        _tasks.update { list ->
            val updated = list + TodoTask(title = title.trim())
            repository.saveTasks(updated)
            updated
        }
        updateWidgets()
    }

    fun toggleTask(id: String) {
        _tasks.update { list ->
            val updated = list.map { if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it }
            repository.saveTasks(updated)
            updated
        }
        updateWidgets()
    }

    fun deleteTask(id: String) {
        _tasks.update { list ->
            val updated = list.filterNot { it.id == id }
            repository.saveTasks(updated)
            updated
        }
        updateWidgets()
    }

    override fun onCleared() {
        super.onCleared()
        try {
            getApplication<Application>().unregisterReceiver(dataChangedReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
