package com.COMP1815.kotlin.data

import com.COMP1815.kotlin.model.Project
import com.COMP1815.kotlin.model.Task
import java.text.SimpleDateFormat
import java.util.*

val FORMAT = SimpleDateFormat("dd/MM/yyyy")

// Simple data storing class
data class RefinedTask(var start: Int, var startL: Int, var end: Int, var endL: Int, val task: Task, var done: Boolean = false) {
    private fun slack() = startL - start
    fun isCritical() = slack() == 0
}

fun getCriticalPath(project: Project): List<RefinedTask> = getPath(project).filter { it.isCritical() }

/**
 * Returns the full path with dates and other values (see above - RefinedTask)
 */
fun getPath(project: Project): List<RefinedTask> {
    val tasks = mutableListOf<Task>().apply { addAll(project.tasks) }.map { RefinedTask(0, 0, it.duration, it.duration, it) }.toMutableList()
    val stars = tasks.filter { !depends(it.task, project) }.apply { forEach { it.done = true } }
    tasks.removeAll(stars)

    fun makeF(me: RefinedTask): RefinedTask { // Recursive forward
        if (!me.done) {
            val deps = tasks.filter { other -> me.task.dependencies.contains(other.task.id) } // -->
            deps.filter { !it.done }.forEach { makeF(it) } // Recursive call

            if (deps.isEmpty())
                me.start = 0 // It's the first tasks
            else me.start = deps.maxOf { other -> other.end } // Latest start
            me.end = me.start + me.task.duration // sum my start with the task duration
            me.done = true // Recursion done: DO NOT CONFUSE THE RefinedTask'S DONE WITH THE Task'S DONE
        }
        return me
    }

    fun makeB(me: RefinedTask): RefinedTask { // Recursive backwards
        if (!me.done) {
            val deps = tasks.filter { other -> other.task.dependencies.contains(me.task.id) } // <--
            deps.filter { !it.done }.forEach { makeB(it) } // Recursive call

            if (deps.isNotEmpty())
                me.endL = deps.minOf { other -> other.start } // Max end
            else me.startL = me.endL - me.task.duration // sub my latest end with the task duration
            me.done = true // Recursion done: DO NOT CONFUSE THE RefinedTask'S DONE WITH THE Task'S DONE
        }
        return me
    }

    tasks.map { makeF(it) }
    tasks.addAll(stars)

    tasks.forEach { it.done = false } // Reset to do the other recursive call

    tasks.map { makeB(it) }

    return tasks
}

fun depends(task: Task, project: Project): Boolean { // If the task is contained is any other dependencies tasks list
    return project.tasks.any { it.dependencies.contains(task.id) }
}

/**
 * returns the end date of the project
 */
fun getEnd(total: MutableList<RefinedTask>): Date {
    if (total.isEmpty())
        return Date(System.currentTimeMillis()) // If it's done returns the current date
    val start = total.first().task.projectReference!!.startDate // Get the start date from the project
    return when (total.size) {
        1 -> {
            Calendar.getInstance().apply {
                time = start
                add(Calendar.DAY_OF_YEAR, total[0].end) // Add the min task end to the project start time
            }.time
        }
        else -> Calendar.getInstance()
                .apply {
                    time = start
                    var timeShift = 0
                    val a = total
                            .filter { !depends(it.task, it.task.projectReference!!) } // Filter for non depending tasks
                    if (a.size == 1)
                        timeShift += applyOnDependencies(a[0], total)
                    else for (k: Int in a.indices - 1) {
                        timeShift += if ((a.indices - 1).size >= k + 1)
                            applyOnDependencies(a[k], total).coerceAtLeast(applyOnDependencies(a[k + 1], total))
                        else applyOnDependencies(a[k], total)
                    }
                    add(
                            Calendar.DAY_OF_YEAR,
                            timeShift // Add the project duration
                    )
                }.time
    }
}

fun applyOnDependencies(given: RefinedTask, total: MutableList<RefinedTask>): Int {
    fun helper(rT: RefinedTask, init: Int = 0): Int {
        rT.task.dependencies.map { Task.findTaskByUUID(it.toString()) }.apply {
            if (size == 0)
                return rT.end // If has no dependencies it's the task itself end time

            if (size < 2)
                return this[0]!!.duration + rT.task.duration // If one dependency sum the durations

            var sum = 0

            for (i in (0 until size - 1)) { // For each pair of dependencies
                val filter =
                        total.filter { it.task.id == this[i]!!.id || it.task.id == this[i + 1]!!.id } // Avoid duplicates

                sum += if (i + 1 < size)
                    helper(filter[i], sum).coerceAtLeast(helper(filter[i + 1], sum)) // Recursive call
                else 0
            }
            return init + sum + rT.end
        }
    }

    return helper(given) // Calculation by recursive
}