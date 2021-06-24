import com.COMP1815.kotlin.data.getEnd
import com.COMP1815.kotlin.data.getPath
import com.COMP1815.kotlin.model.Project
import com.COMP1815.kotlin.model.Task
import com.COMP1815.kotlin.model.Team
import com.COMP1815.kotlin.model.User
import java.time.Instant
import java.util.*

fun main() {
    val user = User(" ", "12")
    val user2 = User("sd", "12")
    val team = Team("team", user, mutableListOf(user2))
    val A = Task("A", "", 1, team, null)
    val B = Task("B", "", 2, team, null)
    val C = Task("C", "", 1, team, null)
    val E = Task("E", "", 4, team, null)
    A.dependencies.add(E.id)
    C.dependencies.addAll(listOf(A.id, B.id))
    val tasks = mutableListOf(A, B, C)
    val project = Project("test", Date.from(Instant.now()), tasks, user, mutableListOf(team))

    println(Date.from(Instant.now()).toString())
    println(getEnd(getPath(project).toMutableList()))
}