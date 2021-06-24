import com.COMP1815.kotlin.data.Loader
import com.COMP1815.kotlin.model.Project
import com.COMP1815.kotlin.model.Team
import com.COMP1815.kotlin.model.User

fun main() {
    val l = Loader()
    l.load()
    User.users.forEach { println(it.toString()) }
    Team.teams.forEach { println(it.toString()) }
    Project.projects.forEach { println(it.toString()) }
}