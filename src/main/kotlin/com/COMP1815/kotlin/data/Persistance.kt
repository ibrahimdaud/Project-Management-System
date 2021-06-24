package com.COMP1815.kotlin.data

import com.COMP1815.kotlin.model.Project
import com.COMP1815.kotlin.model.Task
import com.COMP1815.kotlin.model.Team
import com.COMP1815.kotlin.model.User
import java.io.*

class Loader {
    companion object { // Static reference to DB files / folders
        private val PATH = System.getProperty("user.home") + File.separator + "project_manager"
        val DB_PATH = PATH + File.separator + "data"
        val USERS_DB_PATH = DB_PATH + File.separator + "users.dat"
        val TEAMS_DB_PATH = DB_PATH + File.separator + "teams.dat"
        val TASKS_DB_PATH = DB_PATH + File.separator + "tasks.dat"
        val PROJECTS_DB_PATH = DB_PATH + File.separator + "projects.dat"
    }

    init {
        File(DB_PATH).apply { if (!exists()) mkdirs() } // Make sure that folder exists
    }

    // Save
    fun save() {
        saveUsers()
        saveTeams()
        saveTasks()
        saveProjects()
    }

    fun saveUsers() {
        val dat = File(USERS_DB_PATH).apply { if (!exists()) createNewFile() }
        val writer = BufferedWriter(FileWriter(dat))
        writer.write("")
        User.users.forEach {
            writer.appendLine(it.toString())
        }
        writer.flush()
        writer.close()
    }

    fun saveTeams() {
        val dat = File(TEAMS_DB_PATH).apply { if (!exists()) createNewFile() }
        val writer = BufferedWriter(FileWriter(dat))
        writer.write("")
        Team.teams.forEach {
            writer.appendLine(it.toString())
        }
        writer.flush()
        writer.close()
    }

    fun saveTasks() {
        val dat = File(TASKS_DB_PATH).apply { if (!exists()) createNewFile() }
        val writer = BufferedWriter(FileWriter(dat))
        writer.write("")
        Task.tasks.forEach {
            writer.appendLine(it.toString())
        }
        writer.flush()
        writer.close()
    }

    fun saveProjects() {
        val dat = File(PROJECTS_DB_PATH).apply { if (!exists()) createNewFile() }
        val writer = BufferedWriter(FileWriter(dat))
        writer.write("")
        Project.projects.forEach {
            writer.appendLine(it.toString())
        }
        writer.flush()
        writer.close()
    }

    // Load
    fun load() {
        loadUsers()
        loadTeams()
        loadTasks()
        loadProjects()
    }

    private fun loadUsers() {
        val dat = File(USERS_DB_PATH).apply { if (!exists()) createNewFile() }
        val reader = BufferedReader(FileReader(dat))
        reader.lines().forEach {
            User.parseUser(it)
        }
        reader.close()
    }

    private fun loadTeams() {
        val dat = File(TEAMS_DB_PATH).apply { if (!exists()) createNewFile() }
        val reader = BufferedReader(FileReader(dat))
        reader.lines().forEach {
            Team.parseTeam(it)
        }
        reader.close()
    }

    private fun loadTasks() {
        val dat = File(TASKS_DB_PATH).apply { if (!exists()) createNewFile() }
        val reader = BufferedReader(FileReader(dat))
        reader.lines().forEach {
            Task.parseTask(it)
        }
        reader.close()
    }

    private fun loadProjects() {
        val dat = File(PROJECTS_DB_PATH).apply { if (!exists()) createNewFile() }
        val reader = BufferedReader(FileReader(dat))
        reader.lines().forEach {
            Project.parseProject(it)
        }
        reader.close()
    }
}
