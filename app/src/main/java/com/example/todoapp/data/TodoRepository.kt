package com.example.todoapp.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class TodoRepository(private val context: Context) {
    private val fileName = "tasks.json"

    fun loadTasks(): List<TodoTask> {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            // First launch: populate with sample tasks and save them
            val sampleTasks = listOf(
                TodoTask(title = "Buy groceries"),
                TodoTask(title = "Walk the dog"),
                TodoTask(title = "Check Antigravity settings")
            )
            saveTasks(sampleTasks)
            return sampleTasks
        }

        return try {
            val jsonString = context.openFileInput(fileName).bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<TodoTask>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                list.add(
                    TodoTask(
                        id = jsonObject.getString("id"),
                        title = jsonObject.getString("title"),
                        isCompleted = jsonObject.getBoolean("isCompleted"),
                        createdAt = jsonObject.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
            // Sort by creation time to preserve order
            list.sortBy { it.createdAt }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun saveTasks(tasks: List<TodoTask>) {
        try {
            val jsonArray = JSONArray()
            for (task in tasks) {
                val jsonObject = JSONObject()
                jsonObject.put("id", task.id)
                jsonObject.put("title", task.title)
                jsonObject.put("isCompleted", task.isCompleted)
                jsonObject.put("createdAt", task.createdAt)
                jsonArray.put(jsonObject)
            }
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                output.write(jsonArray.toString().toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
