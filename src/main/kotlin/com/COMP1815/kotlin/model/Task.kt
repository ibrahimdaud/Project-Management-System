package com.COMP1815.kotlin.model

import java.util.*
import kotlin.collections.ArrayList

/**
 * Task class - used to represent tasks
 */
data class Task(var name: String, var description: String, var duration: Int, var team: Team, var projectReference: Project?) {

    // UUID to represent a particular task
    var id: UUID = UUID.randomUUID()

    // Every dependency for that task are stored here
    val dependencies: MutableList<UUID> = ArrayList()

    // Is the task done?
    var done = false

    init { // Constructor
        tasks.add(this) // Add that task to the static tasks list
        if (projectReference != null) // If project reference exists
            if (projectReference?.tasks?.none { it.id == id }!!)  // Check for duplicates
                projectReference!!.tasks.add(this) // If no duplicates are found then add this task to that project
    }

    companion object {
        val tasks: MutableSet<Task> = HashSet() // Static tasks list

        fun findTaskByUUID(uuid: String): Task? {
            return tasks.find { it.id == UUID.fromString(uuid) }
        }

        fun parseTask(task: String) {
            task.removeSurrounding("Task(", ")")
                    .split(",")
                    .apply {
                        try {
                            Task(
                                    get(5).removePrefix(" name="),
                                    first().removePrefix("description="),
                                    Integer.parseInt(get(1).removePrefix(" duration=")),
                                    Team.findTeamByName(get(2).removePrefix(" team="))
                                            ?: throw Team.IllegalTeamException("No team found with name: " + get(2)), // If that team is not found throws an exception
                                    null
                            ).apply {
                                id = UUID.fromString(get(3).removePrefix(" UUID="))
                                val split = get(4).removeSurrounding(" deps=[", "]").split("# ")
                                if (split.isNotEmpty())
                                    dependencies.addAll(split.filter { it.isNotBlank() }.map { UUID.fromString(it) })
                                done = get(6) == " true"
                            }
                        } catch (e: Exception) {
                            System.err.println(e.message)
                        }
                    }
        }
    }

    override fun toString(): String {
        return "Task(description=${description}, duration=${duration}, team=${team.name}, UUID=$id, deps=${dependencies.map { it.toString() }.toString().replace(",", "#")}, name=$name, ${done})"
    }

    class InvalidDateRangeException(s: String) : Exception(s)
    class InvalidUUID(s: String) : Exception(s)
}