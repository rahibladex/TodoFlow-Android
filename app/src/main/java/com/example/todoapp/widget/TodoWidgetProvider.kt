package com.example.todoapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.example.todoapp.MainActivity
import com.example.todoapp.R
import com.example.todoapp.data.TodoRepository

class TodoWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action
        
        if (action == ACTION_TOGGLE_TASK) {
            val taskId = intent.getStringExtra("EXTRA_TASK_ID")
            if (taskId != null) {
                // Toggle task in repository
                val repository = TodoRepository(context)
                val tasks = repository.loadTasks().toMutableList()
                val index = tasks.indexOfFirst { it.id == taskId }
                if (index != -1) {
                    val task = tasks[index]
                    tasks[index] = task.copy(isCompleted = !task.isCompleted)
                    repository.saveTasks(tasks)
                    
                    // Notify ViewModel / MainActivity of data change
                    val changeIntent = Intent("com.example.todoapp.action.DATA_CHANGED").apply {
                        setPackage(context.packageName)
                    }
                    context.sendBroadcast(changeIntent)
                }
            }
            
            // Trigger local update immediately
            triggerWidgetUpdate(context)
        } else if (action == AppWidgetManager.ACTION_APPWIDGET_UPDATE || 
                   action == "com.example.todoapp.action.WIDGET_UPDATE") {
            triggerWidgetUpdate(context)
        }
    }

    private fun triggerWidgetUpdate(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, TodoWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        
        // Notify list to refresh data
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list)
        
        // Update widget header text (counts)
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        const val ACTION_TOGGLE_TASK = "com.example.todoapp.action.TOGGLE_TASK"

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.todo_widget)

            // Setup intent to launch MainActivity when clicking the widget header
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            // Setup intent for RemoteViewsService to bind the ListView
            val serviceIntent = Intent(context, TodoWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }
            views.setRemoteAdapter(R.id.widget_list, serviceIntent)
            views.setEmptyView(R.id.widget_list, R.id.widget_empty_view)

            // Update stats count and progress bar in the widget header
            val repository = TodoRepository(context)
            val tasks = repository.loadTasks()
            val completedCount = tasks.count { it.isCompleted }
            views.setTextViewText(R.id.widget_title_count, "${tasks.size} tasks")
            views.setProgressBar(R.id.widget_progress, tasks.size, completedCount, false)

            // Set up template for item click to toggle the task via broadcast receiver
            val toggleIntent = Intent(context, TodoWidgetProvider::class.java).apply {
                action = ACTION_TOGGLE_TASK
            }
            // Must be FLAG_MUTABLE so the fill-in intent can merge extras (task ID)
            val togglePendingIntent = PendingIntent.getBroadcast(
                context,
                2,
                toggleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            views.setPendingIntentTemplate(R.id.widget_list, togglePendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
