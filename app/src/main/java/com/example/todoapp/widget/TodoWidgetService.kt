package com.example.todoapp.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.todoapp.R
import com.example.todoapp.data.TodoRepository
import com.example.todoapp.data.TodoTask

class TodoWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return TodoWidgetFactory(applicationContext)
    }
}

class TodoWidgetFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private var tasks: List<TodoTask> = emptyList()
    private val repository = TodoRepository(context)

    override fun onCreate() {
        tasks = repository.loadTasks()
    }

    override fun onDataSetChanged() {
        tasks = repository.loadTasks()
    }

    override fun onDestroy() {
        tasks = emptyList()
    }

    override fun getCount(): Int {
        return tasks.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        if (position >= tasks.size) {
            return RemoteViews(context.packageName, R.layout.todo_widget_item)
        }

        val task = tasks[position]
        val views = RemoteViews(context.packageName, R.layout.todo_widget_item)
        views.setTextViewText(R.id.widget_item_title, task.title)

        // Set checkbox status text
        if (task.isCompleted) {
            views.setTextViewText(R.id.widget_item_status, "✓")
            views.setTextColor(R.id.widget_item_status, 0xFF06B6D4.toInt()) // Neon cyan/teal
            views.setTextColor(R.id.widget_item_title, 0x88FFFFFF.toInt())
        } else {
            views.setTextViewText(R.id.widget_item_status, "○")
            views.setTextColor(R.id.widget_item_status, 0xFF94A3B8.toInt()) // Grey
            views.setTextColor(R.id.widget_item_title, 0xFFFFFFFF.toInt())
        }

        // Clicking a task row will trigger background toggle
        val fillInIntent = Intent().apply {
            putExtra("EXTRA_TASK_ID", task.id)
        }
        views.setOnClickFillInIntent(R.id.widget_item_root, fillInIntent)

        return views
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}
