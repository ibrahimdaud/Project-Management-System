package com.COMP1815.kotlin.model

import java.util.*
import kotlin.collections.ArrayList

/**
 * Class Project - represents a project
 */
data class Project(val name: String, val startDate: Date, val tasks: MutableList<Task> = ArrayList(), val admin: User, val teams: MutableList<Team> = ArrayList()) {

    // UUID to identify every different project
    var id: UUID = UUID.randomUUID()

    init { // Constructor
        projects.add(this) // Add to the static projects list
        tasks.forEach { it.projectReference = this } // Assign the project reference to it's tasks
    }

    companion object {
        val projects: MutableList<Project> = LinkedList()

        fun findProjectByAdmin(email: String): Project? {
            return projects.find { it.admin.email.toUpperCase() == email.toUpperCase() }
        }

        fun findProjectByID(id: String): Project? {
            return projects.find { it.id.toString() == id }
        }

        fun parseProject(project: String) {
            project.removeSurrounding("Project(", ")")
                    .split(",")
                    .apply {
                        try {
                            val p = Project(
                                    get(0).removePrefix("name="),
                                    admin = User.findUserByEmail(get(2).removePrefix(" admin="))
                                            ?: throw User.IllegalUserException("Can't found user: " + get(2)), // If user is not found throws an exception
                                    startDate = Date(get(4).removePrefix(" start=").toLong())
                            )

                            with(p) {
                                id = UUID.fromString(get(3).removePrefix(" UUID="))
                                get(1).removeSurrounding(" [", "]").split("#").forEach { s -> // Parses tasks for that project
                                    val its = UUID.fromString(s)
                                    val t = Task.tasks
                                            .first { it.id == its }
                                            .apply { projectReference = this@with } // Assigns the project reference
                                    tasks.add(t) // Adds that task to the project private tasks list
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            System.err.println(e.message)
                        }
                    }
        }
    }

    /**
     * Used for data storing
     */
    override fun toString(): String {
        return "Project(name=$name, ${tasks.map { it.id.toString() }.toString().replace(", ", "#")}, admin=${admin.email}, UUID=$id, start=${startDate.time})"
    }

    class IllegalProjectException(s: String) : Throwable(s)
}

